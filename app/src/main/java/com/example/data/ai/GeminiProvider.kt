package com.example.data.ai

import com.example.data.local.entities.DailyEntryEntity
import com.example.data.local.entities.NutritionLogEntity
import com.example.data.local.entities.SleepLogEntity
import com.example.data.local.entities.WorkoutSessionEntity
import org.json.JSONObject

/**
 * Concrete Gemini implementation of the AIProvider interface.
 * Implements full REST call with fallback, context builder, safety guards,
 * and structured food estimation.
 */
class GeminiProvider(
    private val apiKey: String = ""
) : AIProvider {

    override val providerName: String = "Gemini 3.5 Flash"

    override val isConfigured: Boolean
        get() = apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY"

    private val systemPrompt = """
        You are PRIME AI, the personal operating system coach for a single user dedicated to relentless self-mastery.
        Philosophy: "BUILD YOURSELF. EVERY DAY." Tagline: "Become Your Best Version."
        Personality: Calm, direct, encouraging, practical, honest, non-judgmental, and action-oriented.
        Avoid fake motivational speeches, empty fluff, toxic shame, fear tactics, and excessive emojis. Keep responses concise and focused on high-leverage execution.
        
        CRITICAL SAFETY PROTOCOLS:
        - Never diagnose or prescribe medical treatments.
        - Never recommend dangerous extreme fasting, dangerous dieting, or extreme exercise.
        - Never make financial guarantees.
        - Recommend consulting certified healthcare or fitness professionals for medical symptoms or injuries.
        
        ACTION SUGGESTION PROTOCOL:
        If you recommend a specific actionable task, schedule block, water intake, or focus session, append a structured action tag on a new line at the very end of your response:
        [ACTION:ADD_TASK|Task Title]
        [ACTION:SCHEDULE_BLOCK|Block Title|Time]
        [ACTION:LOG_WATER|500]
        [ACTION:START_FOCUS|25]
        Only include at most ONE action tag per response when appropriate.
    """.trimIndent()

    override suspend fun estimateNutritionFromPrompt(description: String): NutritionEstimateResult {
        if (!isConfigured) {
            return ruleBasedFoodEstimate(description)
        }

        val prompt = """
            Analyze the following food/meal description and return an estimated nutritional breakdown in STRICT JSON format:
            Description: "$description"
            
            Return ONLY a valid JSON object matching this schema with no markdown code fence or extra text:
            {
              "foodName": "Descriptive standard food name",
              "calories": 450,
              "proteinGrams": 35.0,
              "carbsGrams": 40.0,
              "fatGrams": 12.0,
              "confidence": 0.9,
              "explanation": "Brief 1-sentence note on portion assumption."
            }
        """.trimIndent()

        return try {
            val response = GeminiNetworkClient.service.generateContent(
                apiKey = apiKey,
                request = GeminiRequest(
                    contents = listOf(
                        GeminiContent(
                            role = "user",
                            parts = listOf(GeminiPart(text = prompt))
                        )
                    ),
                    systemInstruction = GeminiContent(parts = listOf(GeminiPart(text = "You are a professional sports nutritionist assistant. Return strictly valid raw JSON only."))),
                    generationConfig = GeminiGenerationConfig(temperature = 0.2f, maxOutputTokens = 300)
                )
            )

            val rawText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
            val cleanJson = rawText.replace("```json", "").replace("```", "").trim()
            val jsonObj = JSONObject(cleanJson)

            NutritionEstimateResult(
                foodName = jsonObj.optString("foodName", description),
                calories = jsonObj.optInt("calories", 350),
                proteinGrams = jsonObj.optDouble("proteinGrams", 20.0).toFloat(),
                carbsGrams = jsonObj.optDouble("carbsGrams", 30.0).toFloat(),
                fatGrams = jsonObj.optDouble("fatGrams", 10.0).toFloat(),
                confidence = jsonObj.optDouble("confidence", 0.9).toFloat(),
                explanation = jsonObj.optString("explanation", "Estimated via Gemini AI."),
                sourceTag = "ai"
            )
        } catch (e: Exception) {
            ruleBasedFoodEstimate(description)
        }
    }

    override suspend fun generateDailyCoachingSummary(context: AiContextData): DailyCoachSummary {
        if (!isConfigured) {
            return generateRuleBasedDailyCoach(context)
        }

        val contextSummary = buildDailyContextString(context)
        val prompt = """
            Here is the user's current context for today:
            $contextSummary
            
            Provide a daily coaching brief in STRICT JSON format matching:
            {
              "priorities": ["Priority 1", "Priority 2"],
              "recommendations": ["Recommendation 1", "Recommendation 2"],
              "warnings": ["Warning or point of friction (or empty)"],
              "encouragement": "One concise, direct, grounding sentence."
            }
            Return ONLY raw JSON.
        """.trimIndent()

        return try {
            val response = GeminiNetworkClient.service.generateContent(
                apiKey = apiKey,
                request = GeminiRequest(
                    contents = listOf(GeminiContent(role = "user", parts = listOf(GeminiPart(text = prompt)))),
                    systemInstruction = GeminiContent(parts = listOf(GeminiPart(text = systemPrompt))),
                    generationConfig = GeminiGenerationConfig(temperature = 0.4f, maxOutputTokens = 500)
                )
            )

            val rawText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
            val cleanJson = rawText.replace("```json", "").replace("```", "").trim()
            val json = JSONObject(cleanJson)

            val priorities = mutableListOf<String>()
            val pArray = json.optJSONArray("priorities")
            if (pArray != null) {
                for (i in 0 until pArray.length()) priorities.add(pArray.getString(i))
            }

            val recs = mutableListOf<String>()
            val rArray = json.optJSONArray("recommendations")
            if (rArray != null) {
                for (i in 0 until rArray.length()) recs.add(rArray.getString(i))
            }

            val warnings = mutableListOf<String>()
            val wArray = json.optJSONArray("warnings")
            if (wArray != null) {
                for (i in 0 until wArray.length()) warnings.add(wArray.getString(i))
            }

            DailyCoachSummary(
                priorities = priorities.ifEmpty { listOf("Execute scheduled priority tasks", "Hit daily protein & water target") },
                recommendations = recs.ifEmpty { listOf("Complete morning grooming routine", "Schedule a 50m Deep Work block") },
                warnings = warnings,
                encouragement = json.optString("encouragement", "Consistency compounds. Focus on the next immediate action."),
                isFallback = false
            )
        } catch (e: Exception) {
            generateRuleBasedDailyCoach(context)
        }
    }

    override suspend fun generateWeeklyReview(
        context: AiContextData,
        recentEntries: List<DailyEntryEntity>
    ): WeeklyReviewSummary {
        if (!isConfigured) {
            return generateRuleBasedWeeklyReview(context, recentEntries)
        }

        val avgScore = if (recentEntries.isNotEmpty()) recentEntries.map { it.primeScore }.average().toInt() else 75
        val completedWorkouts = context.recentWorkouts.size
        val prompt = """
            Review the user's performance over the past week:
            Average PRIME Score: $avgScore / 100
            Workouts Logged: $completedWorkouts
            Logged Days: ${recentEntries.size}
            Active Goals: ${context.activeGoals.joinToString { it.title }}
            
            Provide a weekly debrief in STRICT JSON format:
            {
              "wins": ["Win 1", "Win 2"],
              "weaknesses": ["Area for growth 1"],
              "trends": ["Key metric trend"],
              "recommendations": ["Actionable focus for next week"]
            }
            Return ONLY raw JSON.
        """.trimIndent()

        return try {
            val response = GeminiNetworkClient.service.generateContent(
                apiKey = apiKey,
                request = GeminiRequest(
                    contents = listOf(GeminiContent(role = "user", parts = listOf(GeminiPart(text = prompt)))),
                    systemInstruction = GeminiContent(parts = listOf(GeminiPart(text = systemPrompt))),
                    generationConfig = GeminiGenerationConfig(temperature = 0.3f, maxOutputTokens = 500)
                )
            )

            val rawText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
            val cleanJson = rawText.replace("```json", "").replace("```", "").trim()
            val json = JSONObject(cleanJson)

            val wins = json.optJSONArray("wins")?.let { arr -> (0 until arr.length()).map { arr.getString(it) } } ?: listOf("Consistent daily tracking")
            val weaknesses = json.optJSONArray("weaknesses")?.let { arr -> (0 until arr.length()).map { arr.getString(it) } } ?: listOf("Mid-day focus recovery")
            val trends = json.optJSONArray("trends")?.let { arr -> (0 until arr.length()).map { arr.getString(it) } } ?: listOf("PRIME Score steady around $avgScore")
            val recs = json.optJSONArray("recommendations")?.let { arr -> (0 until arr.length()).map { arr.getString(it) } } ?: listOf("Maintain structured bedtime hygiene")

            WeeklyReviewSummary(
                wins = wins,
                weaknesses = weaknesses,
                trends = trends,
                recommendations = recs,
                isFallback = false
            )
        } catch (e: Exception) {
            generateRuleBasedWeeklyReview(context, recentEntries)
        }
    }

    override suspend fun sendAssistantMessage(
        assistantType: String,
        userMessage: String,
        chatHistory: List<Pair<String, String>>,
        context: AiContextData
    ): AiMessageResponse {
        if (!isConfigured) {
            return generateRuleBasedAssistantReply(assistantType, userMessage, context)
        }

        val contextStr = buildContextForAssistant(assistantType, context)
        val assistantRolePrompt = when (assistantType) {
            "nutrition" -> "You are the PRIME Nutrition Specialist. Focus on macronutrient balance, caloric intake, whole food density, and hydration."
            "workout" -> "You are the PRIME Strength & Conditioning Coach. Focus on progressive overload, RPE calibration, recovery, and hypertrophy/strength fundamentals."
            "schedule" -> "You are the PRIME Time & Productivity Strategist. Focus on time blocking, priority triage, Pomodoro cadences, and deep work hygiene."
            "goal" -> "You are the PRIME Goal Architect. Help break multi-level goals down into tangible milestones and immediate planner actions."
            "journal" -> "You are the PRIME Mindset & Journaling Mentor. Help reflect on friction points, emotional resilience, stoic perspective, and learnings."
            "motivation" -> "You are the PRIME Discipline Catalyst. Deliver direct, grounding, realistic perspective to overcome inertia and fatigue."
            else -> "You are the PRIME Master Coach. Answer user inquiries with clear, actionable, high-discipline guidance."
        }

        val promptContents = mutableListOf<GeminiContent>()
        promptContents.add(
            GeminiContent(
                role = "user",
                parts = listOf(GeminiPart(text = "User Profile & Telemetry Context:\n$contextStr\nAssistant Role: $assistantRolePrompt"))
            )
        )
        promptContents.add(
            GeminiContent(
                role = "model",
                parts = listOf(GeminiPart(text = "Understood. I am calibrated with your active profile context and ready to provide direct, actionable guidance."))
            )
        )

        // Include recent history (last 6 turns)
        val recentHistory = chatHistory.takeLast(6)
        for ((sender, msg) in recentHistory) {
            val role = if (sender == "user") "user" else "model"
            promptContents.add(GeminiContent(role = role, parts = listOf(GeminiPart(text = msg))))
        }

        promptContents.add(GeminiContent(role = "user", parts = listOf(GeminiPart(text = userMessage))))

        return try {
            val response = GeminiNetworkClient.service.generateContent(
                apiKey = apiKey,
                request = GeminiRequest(
                    contents = promptContents,
                    systemInstruction = GeminiContent(parts = listOf(GeminiPart(text = systemPrompt))),
                    generationConfig = GeminiGenerationConfig(temperature = 0.5f, maxOutputTokens = 600)
                )
            )

            val rawText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
            if (rawText.isBlank()) {
                generateRuleBasedAssistantReply(assistantType, userMessage, context)
            } else {
                val (cleanText, action) = parseActionFromText(rawText)
                AiMessageResponse(replyText = cleanText, suggestedAction = action, isFallback = false)
            }
        } catch (e: Exception) {
            generateRuleBasedAssistantReply(assistantType, userMessage, context)
        }
    }

    override fun getRuleBasedDashboardInsight(
        recentEntries: List<DailyEntryEntity>,
        todayNutrition: List<NutritionLogEntity>,
        recentSleep: List<SleepLogEntity>,
        recentWorkouts: List<WorkoutSessionEntity>
    ): PrimeAiInsight {
        val totalWater = todayNutrition.filter { it.mealType == "Water" }.sumOf { it.calories } // or hydration
        val totalProtein = todayNutrition.sumOf { it.proteinGrams.toDouble() }
        val latestSleep = recentSleep.firstOrNull()

        return when {
            latestSleep != null && latestSleep.durationMinutes < 360 -> {
                PrimeAiInsight(
                    category = "Recovery Protocol",
                    headline = "Sleep Duration Below 6h Baseline",
                    recommendation = "Prioritize light hydration and schedule a 20m restorative downtime block today to offset CNS fatigue.",
                    isRuleFallback = true
                )
            }
            totalProtein < 50 && todayNutrition.isNotEmpty() -> {
                PrimeAiInsight(
                    category = "Nutrition Calibration",
                    headline = "Protein Intake Pacing Behind Target",
                    recommendation = "Incorporate a high-density protein source (chicken breast, whey, eggs, or Greek yogurt) in your next meal.",
                    isRuleFallback = true
                )
            }
            recentWorkouts.isNotEmpty() -> {
                val lastWorkout = recentWorkouts.first()
                PrimeAiInsight(
                    category = "Training Consistency",
                    headline = "${lastWorkout.templateName} Logged Successfully",
                    recommendation = "Optimal stimulus registered. Ensure hydration and a minimum 7.5h sleep window tonight for muscular recovery.",
                    isRuleFallback = true
                )
            }
            else -> {
                PrimeAiInsight(
                    category = "Daily Momentum",
                    headline = "Daily Baseline Calibrating",
                    recommendation = "Complete your morning routine and log your first priority time block to maintain score trajectory.",
                    isRuleFallback = true
                )
            }
        }
    }

    private fun parseActionFromText(text: String): Pair<String, AiActionSuggestion?> {
        val actionRegex = Regex("\\[ACTION:([A-Z_]+)\\|(.*?)\\]")
        val match = actionRegex.find(text)
        if (match != null) {
            val type = match.groupValues[1]
            val payload = match.groupValues[2]
            val cleanText = text.replace(match.value, "").trim()
            val suggestion = when (type) {
                "ADD_TASK" -> AiActionSuggestion(
                    actionType = "ADD_TASK",
                    title = payload,
                    payload = payload,
                    buttonLabel = "Add to Planner"
                )
                "SCHEDULE_BLOCK" -> AiActionSuggestion(
                    actionType = "SCHEDULE_BLOCK",
                    title = payload,
                    payload = payload,
                    buttonLabel = "Schedule Block"
                )
                "LOG_WATER" -> AiActionSuggestion(
                    actionType = "LOG_WATER",
                    title = "Log $payload ml Water",
                    payload = payload,
                    buttonLabel = "+${payload}ml Water"
                )
                "START_FOCUS" -> AiActionSuggestion(
                    actionType = "START_FOCUS",
                    title = "Start $payload min Focus Session",
                    payload = payload,
                    buttonLabel = "Start Focus ($payload min)"
                )
                else -> null
            }
            return Pair(cleanText, suggestion)
        }
        return Pair(text.trim(), null)
    }

    private fun ruleBasedFoodEstimate(desc: String): NutritionEstimateResult {
        val lower = desc.lowercase()
        return when {
            "chicken" in lower && "rice" in lower -> NutritionEstimateResult(
                foodName = "Grilled Chicken Breast with White Rice",
                calories = 520,
                proteinGrams = 48f,
                carbsGrams = 60f,
                fatGrams = 8f,
                explanation = "Standard estimate: 200g chicken breast + 1.5 cups jasmine rice."
            )
            "steak" in lower || "beef" in lower -> NutritionEstimateResult(
                foodName = "Lean Sirloin Steak with Potatoes",
                calories = 650,
                proteinGrams = 52f,
                carbsGrams = 45f,
                fatGrams = 22f,
                explanation = "Standard estimate: 250g sirloin + baked potato."
            )
            "egg" in lower -> NutritionEstimateResult(
                foodName = "Whole Eggs & Toast",
                calories = 420,
                proteinGrams = 24f,
                carbsGrams = 35f,
                fatGrams = 18f,
                explanation = "Standard estimate: 3 large whole eggs + 2 slices toast."
            )
            "shake" in lower || "whey" in lower || "protein" in lower -> NutritionEstimateResult(
                foodName = "Whey Protein Shake",
                calories = 280,
                proteinGrams = 35f,
                carbsGrams = 20f,
                fatGrams = 4f,
                explanation = "Standard estimate: 1 scoop whey protein + milk/oats."
            )
            "oat" in lower || "oatmeal" in lower -> NutritionEstimateResult(
                foodName = "Oatmeal Bowl with Fruit",
                calories = 360,
                proteinGrams = 12f,
                carbsGrams = 65f,
                fatGrams = 6f,
                explanation = "Standard estimate: 80g rolled oats + berries & honey."
            )
            else -> NutritionEstimateResult(
                foodName = desc.trim().ifEmpty { "Balanced Whole Meal" },
                calories = 450,
                proteinGrams = 30f,
                carbsGrams = 45f,
                fatGrams = 15f,
                explanation = "Calculated using standard macronutrient density heuristic."
            )
        }
    }

    private fun generateRuleBasedDailyCoach(context: AiContextData): DailyCoachSummary {
        val priorities = mutableListOf<String>()
        if (context.todayTasks.isNotEmpty()) {
            priorities.add("Execute priority task: ${context.todayTasks.first().title}")
        } else {
            priorities.add("Define top 3 high-leverage Planner tasks for today")
        }
        priorities.add("Maintain hydration target and hit daily protein baseline")

        val recs = listOf(
            "Complete morning grooming & skincare routines",
            "Lock in a 50-minute deep focus session before noon",
            "Target minimum 7.5 hours of dark, cool sleep tonight"
        )

        return DailyCoachSummary(
            priorities = priorities,
            recommendations = recs,
            warnings = if (context.todayNutrition.isEmpty()) listOf("No meals logged yet today.") else emptyList(),
            encouragement = "Discipline is choosing between what you want now and what you want most.",
            isFallback = true
        )
    }

    private fun generateRuleBasedWeeklyReview(context: AiContextData, entries: List<DailyEntryEntity>): WeeklyReviewSummary {
        val avgScore = if (entries.isNotEmpty()) entries.map { it.primeScore }.average().toInt() else 80
        return WeeklyReviewSummary(
            wins = listOf(
                "Logged $avgScore average PRIME Score across active days",
                "Maintained structured routine checklists and hydration tracking"
            ),
            weaknesses = listOf(
                "Reduce distraction frequency during afternoon focus blocks"
            ),
            trends = listOf(
                "Consistency trajectory is trending positive over 7 days"
            ),
            recommendations = listOf(
                "Set weekly goals on Sunday evening",
                "Increase progressive overload on main compound lifts by 2.5%"
            ),
            isFallback = true
        )
    }

    private fun generateRuleBasedAssistantReply(
        assistantType: String,
        userMessage: String,
        context: AiContextData
    ): AiMessageResponse {
        val lower = userMessage.lowercase()
        val (reply, action) = when (assistantType) {
            "nutrition" -> {
                when {
                    "protein" in lower -> Pair("Target 1.6g to 2.2g of protein per kg of bodyweight to maximize muscle protein synthesis. Spread across 3-4 meals.", null)
                    "water" in lower || "hydrate" in lower -> Pair("Optimal hydration supports cognitive sharpness and cellular recovery. Aim for 3000ml to 3500ml daily.", AiActionSuggestion("LOG_WATER", "500ml Water", "500", "+500ml Water"))
                    else -> Pair("Track your meals consistently. Prioritize single-ingredient whole foods, lean proteins, complex carbohydrates, and essential fatty acids.", null)
                }
            }
            "workout" -> {
                when {
                    "split" in lower || "routine" in lower -> Pair("An Upper/Lower or Push/Pull/Legs structure provides optimal frequency (2x per week per muscle group) and recovery.", null)
                    "plateau" in lower -> Pair("Overcome plateaus by resetting load by 10% and building back up, or adjusting volume and improving sleep quality.", null)
                    else -> Pair("Focus on progressive overload: increase load, reps, or control tempo with strict execution and RPE 7-9 on working sets.", null)
                }
            }
            "schedule" -> {
                Pair("Protect your high-energy morning hours for uninterrupted deep work. Group administrative tasks into the late afternoon.", AiActionSuggestion("START_FOCUS", "Deep Work Session", "25", "Start Focus (25m)"))
            }
            "goal" -> {
                Pair("Break ambitious yearly goals into quarterly milestones and weekly sprint deliverables. What is the single highest leverage task you can execute today?", AiActionSuggestion("ADD_TASK", "Execute weekly goal milestone", "Execute weekly goal milestone", "Add to Planner"))
            }
            "journal" -> {
                Pair("Reflect objectively on today: What friction occurred? What did you execute well? Control what is within your domain.", null)
            }
            else -> {
                Pair("Discipline over motivation. State your goal, eliminate the distractions, and execute the immediate next step.", null)
            }
        }

        return AiMessageResponse(replyText = reply, suggestedAction = action, isFallback = true)
    }

    private fun buildDailyContextString(context: AiContextData): String {
        val sb = StringBuilder()
        sb.append("User: ${context.profile?.name ?: "User"}\n")
        sb.append("Current PRIME Score: ${context.todayEntry?.primeScore ?: 0}/100\n")
        sb.append("Tasks Completed: ${context.todayTasks.count { it.isCompleted }}/${context.todayTasks.size}\n")
        sb.append("Habits Logged: ${context.activeHabits.size}\n")
        val cals = context.todayNutrition.sumOf { it.calories }
        val protein = context.todayNutrition.sumOf { it.proteinGrams.toDouble() }.toInt()
        sb.append("Nutrition Today: $cals kcal, ${protein}g protein\n")
        return sb.toString()
    }

    private fun buildContextForAssistant(type: String, context: AiContextData): String {
        val sb = StringBuilder()
        sb.append("User Goal: ${context.profile?.primaryGoal ?: "Relentless Self Mastery"}\n")
        when (type) {
            "nutrition" -> {
                val cals = context.todayNutrition.sumOf { it.calories }
                val p = context.todayNutrition.sumOf { it.proteinGrams.toDouble() }.toInt()
                val c = context.todayNutrition.sumOf { it.carbsGrams.toDouble() }.toInt()
                val f = context.todayNutrition.sumOf { it.fatGrams.toDouble() }.toInt()
                sb.append("Today's Macros: $cals kcal (P: ${p}g, C: ${c}g, F: ${f}g)\n")
                sb.append("Target: ${context.profile?.targetCalories ?: 2400} kcal, ${context.profile?.targetProteinGrams ?: 180}g protein\n")
            }
            "workout" -> {
                sb.append("Recent Workouts Count: ${context.recentWorkouts.size}\n")
                if (context.recentWorkouts.isNotEmpty()) {
                    val last = context.recentWorkouts.first()
                    sb.append("Last Workout: ${last.templateName} (Volume: ${last.totalVolumeKg}kg, Duration: ${last.durationMinutes}m)\n")
                }
            }
            "schedule" -> {
                sb.append("Today Tasks: ${context.todayTasks.map { it.title }.joinToString()}\n")
            }
            "goal" -> {
                sb.append("Active Goals: ${context.activeGoals.map { "${it.title} (${it.level})" }.joinToString()}\n")
            }
            "journal" -> {
                if (context.recentMindset.isNotEmpty()) {
                    val m = context.recentMindset.first()
                    sb.append("Mood Rating: ${m.moodRating}/10, Energy: ${m.energyRating}/10, Stress: ${m.stressRating}/10\n")
                }
            }
            else -> {
                sb.append("PRIME Score: ${context.todayEntry?.primeScore ?: 0}/100\n")
            }
        }
        return sb.toString()
    }
}
