package com.example.data.ai

import com.example.data.local.entities.DailyEntryEntity
import com.example.data.local.entities.GoalEntity
import com.example.data.local.entities.HabitEntity
import com.example.data.local.entities.MindsetLogEntity
import com.example.data.local.entities.NutritionLogEntity
import com.example.data.local.entities.ProfileEntity
import com.example.data.local.entities.SleepLogEntity
import com.example.data.local.entities.TaskEntity
import com.example.data.local.entities.WorkoutSessionEntity

data class NutritionEstimateResult(
    val foodName: String,
    val portionSize: String = "1 serving",
    val calories: Int,
    val proteinGrams: Float,
    val carbsGrams: Float,
    val fatGrams: Float,
    val confidence: Float = 0.9f,
    val explanation: String = "Estimated from description.",
    val disclaimer: String = "AI nutritional estimation. Calibrate with packaging labels.",
    val sourceTag: String = "ai" // "ai" | "ai_edited"
)

data class PrimeAiInsight(
    val category: String,
    val headline: String,
    val recommendation: String,
    val confidenceScore: Float = 0.95f,
    val isRuleFallback: Boolean = false
)

data class AiActionSuggestion(
    val actionType: String, // "ADD_TASK", "SCHEDULE_BLOCK", "LOG_WATER", "LOG_FOOD", "START_FOCUS"
    val title: String,
    val payload: String, // json or simple payload
    val buttonLabel: String = "Apply Strategy"
)

data class AiMessageResponse(
    val replyText: String,
    val suggestedAction: AiActionSuggestion? = null,
    val isFallback: Boolean = false,
    val errorMessage: String? = null
)

data class DailyCoachSummary(
    val summary: String = "Execute your highest-leverage habits today.",
    val primaryFocus: String = "Complete critical targets before midday.",
    val potentialFriction: String = "Watch for afternoon fatigue and distraction.",
    val priorities: List<String> = emptyList(),
    val recommendations: List<String> = emptyList(),
    val warnings: List<String> = emptyList(),
    val encouragement: String = "Stay focused on daily execution.",
    val isFallback: Boolean = false
)

data class WeeklyReviewSummary(
    val averageScore: Float = 85f,
    val highlights: String = "Solid consistency across training and nutrition.",
    val consistencyWins: String = "Hit workout consistency and hydration targets.",
    val blindSpots: String = "Sleep consistency dropped on weekends.",
    val tacticalAdjustments: String = "Establish a rigid 10:30 PM wind-down routine.",
    val wins: List<String> = emptyList(),
    val weaknesses: List<String> = emptyList(),
    val trends: List<String> = emptyList(),
    val recommendations: List<String> = emptyList(),
    val isFallback: Boolean = false
)

data class AiContextData(
    val profile: ProfileEntity? = null,
    val todayEntry: DailyEntryEntity? = null,
    val todayNutrition: List<NutritionLogEntity> = emptyList(),
    val recentWorkouts: List<WorkoutSessionEntity> = emptyList(),
    val recentSleep: List<SleepLogEntity> = emptyList(),
    val activeHabits: List<HabitEntity> = emptyList(),
    val todayTasks: List<TaskEntity> = emptyList(),
    val activeGoals: List<GoalEntity> = emptyList(),
    val recentMindset: List<MindsetLogEntity> = emptyList()
)

/**
 * Clean architectural abstraction for AI features.
 * All application modules depend strictly on AIProvider.
 */
interface AIProvider {
    val providerName: String
    val isConfigured: Boolean

    suspend fun estimateNutritionFromPrompt(description: String): NutritionEstimateResult
    suspend fun generateDailyCoachingSummary(context: AiContextData): DailyCoachSummary
    suspend fun generateWeeklyReview(context: AiContextData, recentEntries: List<DailyEntryEntity>): WeeklyReviewSummary
    suspend fun sendAssistantMessage(
        assistantType: String,
        userMessage: String,
        chatHistory: List<Pair<String, String>>, // sender to text
        context: AiContextData
    ): AiMessageResponse
    fun getRuleBasedDashboardInsight(
        recentEntries: List<DailyEntryEntity>,
        todayNutrition: List<NutritionLogEntity>,
        recentSleep: List<SleepLogEntity>,
        recentWorkouts: List<WorkoutSessionEntity>
    ): PrimeAiInsight
}
