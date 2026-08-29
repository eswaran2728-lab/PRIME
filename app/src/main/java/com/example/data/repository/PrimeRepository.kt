package com.example.data.repository

import com.example.data.ai.AIProvider
import com.example.data.ai.AiContextData
import com.example.data.ai.AiMessageResponse
import com.example.data.ai.DailyCoachSummary
import com.example.data.ai.GeminiProvider
import com.example.data.ai.NutritionEstimateResult
import com.example.data.ai.PrimeAiInsight
import com.example.data.ai.WeeklyReviewSummary
import com.example.data.local.PrimeDatabase
import com.example.data.local.entities.AchievementBadgeEntity
import com.example.data.local.entities.AiChatMessageEntity
import com.example.data.local.entities.AiUsageStatsEntity
import com.example.data.local.entities.BodyMeasurementEntity
import com.example.data.local.entities.BodyPhotoEntity
import com.example.data.local.entities.BookEntity
import com.example.data.local.entities.CareerAchievementEntity
import com.example.data.local.entities.CareerGoalEntity
import com.example.data.local.entities.CareerProjectEntity
import com.example.data.local.entities.CareerSkillEntity
import com.example.data.local.entities.CertificationEntity
import com.example.data.local.entities.CourseEntity
import com.example.data.local.entities.DailyEntryEntity
import com.example.data.local.entities.DailyScoreRecordEntity
import com.example.data.local.entities.ExerciseEntity
import com.example.data.local.entities.FinanceTransactionEntity
import com.example.data.local.entities.FinancialGoalEntity
import com.example.data.local.entities.FocusDistractionEntity
import com.example.data.local.entities.FocusSessionEntity
import com.example.data.local.entities.FoodItemEntity
import com.example.data.local.entities.GoalEntity
import com.example.data.local.entities.GoalMilestoneEntity
import com.example.data.local.entities.GroomingLogEntity
import com.example.data.local.entities.GroomingRoutineItemEntity
import com.example.data.local.entities.HabitEntity
import com.example.data.local.entities.HabitLogEntity
import com.example.data.local.entities.HydrationLogEntity
import com.example.data.local.entities.JournalEntryEntity
import com.example.data.local.entities.MindsetLogEntity
import com.example.data.local.entities.NutritionLogEntity
import com.example.data.local.entities.ProfileEntity
import com.example.data.local.entities.ResumeItemEntity
import com.example.data.local.entities.SkincareLogEntity
import com.example.data.local.entities.SkincareProductEntity
import com.example.data.local.entities.SleepLogEntity
import com.example.data.local.entities.StudySessionEntity
import com.example.data.local.entities.TaskEntity
import com.example.data.local.entities.TimeBlockEntity
import com.example.data.local.entities.VisionItemEntity
import com.example.data.local.entities.WeeklyReviewRecordEntity
import com.example.data.local.entities.WorkoutSessionEntity
import com.example.data.local.entities.WorkoutSetEntity
import com.example.data.local.entities.WorkoutTemplateEntity
import com.example.data.local.entities.XpTransactionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

class PrimeRepository(
    private val database: PrimeDatabase,
    val aiProvider: AIProvider = GeminiProvider()
) {
    private val dailyEntryDao = database.dailyEntryDao()
    private val profileDao = database.profileDao()
    private val bodyDao = database.bodyTrackingDao()
    private val workoutDao = database.workoutDao()
    private val nutritionDao = database.nutritionDao()
    private val habitDao = database.habitDao()
    private val plannerDao = database.plannerDao()
    private val dailyScoreDao = database.dailyScoreDao()
    private val groomingDao = database.groomingDao()
    private val skincareDao = database.skincareDao()
    private val sleepDao = database.sleepDao()
    private val mindsetDao = database.mindsetDao()
    private val focusDao = database.focusDao()
    private val goalDao = database.goalDao()
    private val aiDao = database.aiDao()
    private val careerDao = database.careerDao()
    private val learningDao = database.learningDao()
    private val financeDao = database.financeDao()
    private val gamificationDao = database.gamificationDao()
    private val visionAndReviewDao = database.visionAndReviewDao()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            seedInitialDataIfEmpty()
        }
    }

    fun getTodayDateString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    // Profile & Streak
    val userProfile: Flow<ProfileEntity?> = profileDao.getProfile()
    suspend fun saveProfile(profile: ProfileEntity) = profileDao.insertOrUpdate(profile)

    // Daily Entries (Roll-up)
    fun getDailyEntry(date: String = getTodayDateString()): Flow<DailyEntryEntity?> =
        dailyEntryDao.getEntryForDate(date)

    fun getAllDailyEntries(): Flow<List<DailyEntryEntity>> = dailyEntryDao.getAllEntries()
    fun getRecentDailyEntries(): Flow<List<DailyEntryEntity>> = dailyEntryDao.getRecentEntries()
    fun getTotalLoggedDays(): Flow<Int> = dailyEntryDao.getTotalLoggedDaysCount()

    // Daily Score Records
    fun getDailyScoreRecord(date: String = getTodayDateString()): Flow<DailyScoreRecordEntity?> =
        dailyScoreDao.getScoreForDate(date)

    // Body Tracking
    val allBodyMeasurements: Flow<List<BodyMeasurementEntity>> = bodyDao.getAllMeasurements()
    val latestBodyMeasurement: Flow<BodyMeasurementEntity?> = bodyDao.getLatestMeasurement()
    val allBodyPhotos: Flow<List<BodyPhotoEntity>> = bodyDao.getAllPhotos()

    suspend fun logBodyMeasurement(
        weightKg: Float,
        heightCm: Float,
        waistCm: Float?,
        chestCm: Float?,
        armsCm: Float?,
        thighsCm: Float?,
        neckCm: Float?,
        date: String = getTodayDateString(),
        notes: String = ""
    ): Long {
        val heightM = heightCm / 100f
        val bmi = if (heightM > 0) weightKg / (heightM * heightM) else 22f
        val bodyFat = if (waistCm != null && neckCm != null && waistCm > neckCm) {
            val approx = (495 / (1.0324 - 0.19077 * Math.log10((waistCm - neckCm).toDouble()) + 0.15456 * Math.log10(heightCm.toDouble())) - 450).toFloat()
            approx.coerceIn(5f, 50f)
        } else null

        val measurement = BodyMeasurementEntity(
            date = date,
            weightKg = weightKg,
            heightCm = heightCm,
            waistCm = waistCm,
            chestCm = chestCm,
            armsCm = armsCm,
            thighsCm = thighsCm,
            neckCm = neckCm,
            bodyFatEstimate = bodyFat,
            bmi = bmi,
            notes = notes,
            source = "manual"
        )
        val id = bodyDao.insertMeasurement(measurement)
        awardXp(50)
        recalculateDailyRollup(date)
        return id
    }

    suspend fun saveBodyPhoto(photoUri: String, pose: String = "front", date: String = getTodayDateString(), notes: String = ""): Long {
        val photo = BodyPhotoEntity(date = date, photoUri = photoUri, pose = pose, notes = notes)
        val id = bodyDao.insertPhoto(photo)
        awardXp(30)
        return id
    }

    // Workouts
    val workoutTemplates: Flow<List<WorkoutTemplateEntity>> = workoutDao.getAllTemplates()
    val exercises: Flow<List<ExerciseEntity>> = workoutDao.getAllExercises()
    val allWorkoutSessions: Flow<List<WorkoutSessionEntity>> = workoutDao.getAllSessions()

    fun getWorkoutSession(sessionId: Long): Flow<WorkoutSessionEntity?> = workoutDao.getSessionById(sessionId)
    fun getWorkoutSets(sessionId: Long): Flow<List<WorkoutSetEntity>> = workoutDao.getSetsForSession(sessionId)

    suspend fun createWorkoutSession(templateName: String = "Freestyle Workout", templateId: Long? = null, date: String = getTodayDateString()): Long {
        val session = WorkoutSessionEntity(templateName = templateName, templateId = templateId, date = date, startTime = System.currentTimeMillis())
        return workoutDao.insertSession(session)
    }

    suspend fun logWorkoutSet(
        sessionId: Long,
        exerciseName: String,
        setNumber: Int,
        weightKg: Float,
        reps: Int,
        rpe: Int? = null,
        muscleGroup: String = "Full Body"
    ): Long {
        val set = WorkoutSetEntity(
            sessionId = sessionId,
            exerciseName = exerciseName,
            setNumber = setNumber,
            weightKg = weightKg,
            reps = reps,
            rpe = rpe,
            muscleGroup = muscleGroup,
            isCompleted = true
        )
        val id = workoutDao.insertSet(set)

        val sets = workoutDao.getSetsForSessionSync(sessionId)
        val totalVol = sets.sumOf { (it.weightKg * it.reps).toDouble() }.toFloat()
        val currentSession = workoutDao.getSessionByIdSync(sessionId)
        if (currentSession != null) {
            workoutDao.updateSession(currentSession.copy(totalVolumeKg = totalVol, totalSetsCompleted = sets.size))
        }
        return id
    }

    suspend fun completeWorkoutSession(
        sessionId: Long,
        durationMinutes: Int,
        rpe: Int,
        notes: String = "",
        date: String = getTodayDateString()
    ) {
        val currentSession = workoutDao.getSessionByIdSync(sessionId)
        if (currentSession != null) {
            val sets = workoutDao.getSetsForSessionSync(sessionId)
            val totalVol = sets.sumOf { (it.weightKg * it.reps).toDouble() }.toFloat()
            workoutDao.updateSession(
                currentSession.copy(
                    durationMinutes = durationMinutes,
                    rpe = rpe,
                    notes = notes,
                    totalVolumeKg = totalVol,
                    totalSetsCompleted = sets.size,
                    isCompleted = true,
                    endTime = System.currentTimeMillis()
                )
            )
            awardXp(150)
            recalculateDailyRollup(date)
        }
    }

    suspend fun cancelWorkoutSession(sessionId: Long) {
        val session = workoutDao.getSessionByIdSync(sessionId)
        if (session != null) {
            workoutDao.deleteSession(session)
            workoutDao.deleteSetsForSession(sessionId)
        }
    }

    // Nutrition & Hydration
    val allFoods: Flow<List<FoodItemEntity>> = nutritionDao.getAllFoods()
    val favoriteFoods: Flow<List<FoodItemEntity>> = nutritionDao.getFavoriteFoods()

    fun getTodayNutritionLogs(date: String = getTodayDateString()): Flow<List<NutritionLogEntity>> =
        nutritionDao.getNutritionLogsForDate(date)

    fun getTodayHydrationLogs(date: String = getTodayDateString()): Flow<List<HydrationLogEntity>> =
        nutritionDao.getHydrationLogsForDate(date)

    suspend fun logFoodItem(
        mealType: String,
        foodName: String,
        portions: Float = 1.0f,
        calories: Int,
        proteinGrams: Float,
        carbsGrams: Float,
        fatGrams: Float,
        date: String = getTodayDateString(),
        source: String = "manual"
    ): Long {
        val log = NutritionLogEntity(
            date = date,
            mealType = mealType,
            foodName = foodName,
            portions = portions,
            calories = calories,
            proteinGrams = proteinGrams,
            carbsGrams = carbsGrams,
            fatGrams = fatGrams,
            source = source
        )
        val id = nutritionDao.insertNutritionLog(log)
        awardXp(20)
        recalculateDailyRollup(date)
        return id
    }

    suspend fun deleteNutritionLog(log: NutritionLogEntity) {
        nutritionDao.deleteNutritionLog(log)
        recalculateDailyRollup(log.date)
    }

    suspend fun logHydration(amountMl: Int, date: String = getTodayDateString()): Long {
        val log = HydrationLogEntity(date = date, amountMl = amountMl)
        val id = nutritionDao.insertHydrationLog(log)
        awardXp(10)
        recalculateDailyRollup(date)
        return id
    }

    suspend fun addCustomFood(
        name: String,
        servingSize: String,
        caloriesPerServing: Int,
        proteinGrams: Float,
        carbsGrams: Float,
        fatGrams: Float
    ): Long {
        val food = FoodItemEntity(
            name = name,
            servingSize = servingSize,
            caloriesPerServing = caloriesPerServing,
            proteinGrams = proteinGrams,
            carbsGrams = carbsGrams,
            fatGrams = fatGrams,
            isFavorite = true
        )
        return nutritionDao.insertFood(food)
    }

    // Habits
    val allActiveHabits: Flow<List<HabitEntity>> = habitDao.getAllActiveHabits()
    fun getTodayHabitLogs(date: String = getTodayDateString()): Flow<List<HabitLogEntity>> =
        habitDao.getHabitLogsForDate(date)

    suspend fun createHabit(
        name: String,
        iconName: String,
        category: String,
        frequency: String,
        reminderTime: String = "08:00"
    ): Long {
        val habit = HabitEntity(name = name, iconName = iconName, category = category, frequency = frequency, reminderTime = reminderTime)
        return habitDao.insertHabit(habit)
    }

    suspend fun toggleHabitCompletion(habitId: Long, isCompleted: Boolean, date: String = getTodayDateString()) {
        if (isCompleted) {
            val log = HabitLogEntity(habitId = habitId, date = date, isCompleted = true)
            habitDao.insertHabitLog(log)
            val habit = habitDao.getHabitByIdSync(habitId)
            if (habit != null) {
                val newStreak = habit.currentStreak + 1
                val best = if (newStreak > habit.bestStreak) newStreak else habit.bestStreak
                habitDao.updateHabit(habit.copy(currentStreak = newStreak, bestStreak = best))
            }
            awardXp(25)
        } else {
            habitDao.deleteHabitLog(habitId, date)
            val habit = habitDao.getHabitByIdSync(habitId)
            if (habit != null && habit.currentStreak > 0) {
                habitDao.updateHabit(habit.copy(currentStreak = habit.currentStreak - 1))
            }
        }
        recalculateDailyRollup(date)
    }

    // Planner & Tasks
    fun getTasksForDate(date: String = getTodayDateString()): Flow<List<TaskEntity>> =
        plannerDao.getTasksForDate(date)

    fun getTimeBlocksForDate(date: String = getTodayDateString()): Flow<List<TimeBlockEntity>> =
        plannerDao.getTimeBlocksForDate(date)

    suspend fun addTask(
        title: String,
        category: String = "Priority",
        priorityLevel: Int = 1,
        xpReward: Int = 50,
        date: String = getTodayDateString()
    ): Long {
        val task = TaskEntity(title = title, category = category, priorityLevel = priorityLevel, xpReward = xpReward, date = date)
        return plannerDao.insertTask(task)
    }

    suspend fun toggleTaskCompletion(task: TaskEntity) {
        val newStatus = !task.isCompleted
        plannerDao.updateTask(task.copy(isCompleted = newStatus))
        if (newStatus) {
            awardXp(task.xpReward)
        }
        recalculateDailyRollup(task.date)
    }

    suspend fun deleteTask(task: TaskEntity) {
        plannerDao.deleteTask(task)
        recalculateDailyRollup(task.date)
    }

    suspend fun addTimeBlock(
        title: String,
        startTime: String,
        endTime: String,
        category: String,
        colorHex: String,
        date: String = getTodayDateString()
    ): Long {
        val block = TimeBlockEntity(
            title = title,
            date = date,
            startTime = startTime,
            endTime = endTime,
            category = category,
            colorHex = colorHex
        )
        return plannerDao.insertTimeBlock(block)
    }

    suspend fun deleteTimeBlock(block: TimeBlockEntity) {
        plannerDao.deleteTimeBlock(block)
    }

    // 11. Grooming Routines
    val allGroomingItems: Flow<List<GroomingRoutineItemEntity>> = groomingDao.getAllRoutineItems()
    fun getGroomingItemsForRoutine(type: String): Flow<List<GroomingRoutineItemEntity>> = groomingDao.getItemsForRoutine(type)
    fun getGroomingLogsForDate(date: String = getTodayDateString()): Flow<List<GroomingLogEntity>> = groomingDao.getLogsForDate(date)

    suspend fun toggleGroomingItem(itemId: Long, routineType: String, isDone: Boolean, date: String = getTodayDateString()) {
        if (isDone) {
            groomingDao.insertLog(GroomingLogEntity(routineType = routineType, itemId = itemId, date = date, isCompleted = true))
            awardXp(15)
        } else {
            groomingDao.deleteLog(itemId, date)
        }
        recalculateDailyRollup(date)
    }

    suspend fun addGroomingItem(title: String, routineType: String, iconName: String = "face"): Long {
        return groomingDao.insertItem(
            GroomingRoutineItemEntity(
                title = title,
                routineType = routineType,
                iconName = iconName,
                isDefault = false
            )
        )
    }

    suspend fun deleteGroomingItem(item: GroomingRoutineItemEntity) {
        groomingDao.deleteItem(item)
    }

    // 12. Skincare
    val allSkincareProducts: Flow<List<SkincareProductEntity>> = skincareDao.getAllProducts()
    fun getSkincareProductsForRoutine(routineTime: String): Flow<List<SkincareProductEntity>> = skincareDao.getProductsForRoutine(routineTime)
    fun getSkincareLogsForDate(date: String = getTodayDateString()): Flow<List<SkincareLogEntity>> = skincareDao.getLogsForDate(date)

    suspend fun toggleSkincareProduct(productId: Long, routineTime: String, isDone: Boolean, date: String = getTodayDateString()) {
        if (isDone) {
            skincareDao.insertLog(SkincareLogEntity(routineTime = routineTime, productId = productId, date = date, isCompleted = true))
            awardXp(15)
        } else {
            skincareDao.deleteLog(productId, date, routineTime)
        }
        recalculateDailyRollup(date)
    }

    suspend fun addSkincareProduct(
        name: String,
        category: String,
        brand: String,
        routineTime: String,
        frequency: String = "Daily",
        startDate: String = "",
        expiryDate: String = "",
        notes: String = ""
    ): Long {
        return skincareDao.insertProduct(
            SkincareProductEntity(
                name = name,
                category = category,
                brand = brand,
                routineTime = routineTime,
                frequency = frequency,
                startDate = startDate,
                expiryDate = expiryDate,
                notes = notes
            )
        )
    }

    suspend fun deleteSkincareProduct(product: SkincareProductEntity) {
        skincareDao.deleteProduct(product)
    }

    // 13. Sleep
    fun getSleepLogForDate(date: String = getTodayDateString()): Flow<SleepLogEntity?> = sleepDao.getSleepLogForDate(date)
    val recentSleepLogs: Flow<List<SleepLogEntity>> = sleepDao.getRecentSleepLogs()

    suspend fun logSleep(
        bedtime: String,
        wakeTime: String,
        durationMinutes: Int,
        qualityRating: Int,
        nextDayEnergy: Int,
        notes: String = "",
        deepSleepMinutes: Int? = null,
        remSleepMinutes: Int? = null,
        restingHeartRate: Int? = null,
        hrvScore: Float? = null,
        date: String = getTodayDateString(),
        source: String = "manual"
    ): Long {
        val log = SleepLogEntity(
            date = date,
            bedtime = bedtime,
            wakeTime = wakeTime,
            durationMinutes = durationMinutes,
            qualityRating = qualityRating,
            nextDayEnergy = nextDayEnergy,
            deepSleepMinutes = deepSleepMinutes,
            remSleepMinutes = remSleepMinutes,
            restingHeartRate = restingHeartRate,
            hrvScore = hrvScore,
            notes = notes,
            source = source
        )
        val id = sleepDao.insertSleepLog(log)
        awardXp(40)
        recalculateDailyRollup(date)
        return id
    }

    // 14. Mindset & Journal
    fun getMindsetLogForDate(date: String = getTodayDateString()): Flow<MindsetLogEntity?> = mindsetDao.getMindsetLogForDate(date)
    val recentMindsetLogs: Flow<List<MindsetLogEntity>> = mindsetDao.getRecentMindsetLogs()

    fun getJournalForDate(date: String = getTodayDateString()): Flow<JournalEntryEntity?> = mindsetDao.getJournalForDate(date)
    val recentJournalEntries: Flow<List<JournalEntryEntity>> = mindsetDao.getRecentJournalEntries()

    suspend fun logMindset(
        moodRating: Int,
        energyRating: Int,
        stressRating: Int,
        confidenceRating: Int,
        focusRating: Int,
        moodLabel: String,
        date: String = getTodayDateString()
    ): Long {
        val log = MindsetLogEntity(
            date = date,
            moodRating = moodRating,
            energyRating = energyRating,
            stressRating = stressRating,
            confidenceRating = confidenceRating,
            focusRating = focusRating,
            moodLabel = moodLabel
        )
        val id = mindsetDao.insertMindsetLog(log)
        awardXp(30)
        recalculateDailyRollup(date)
        return id
    }

    suspend fun saveJournalEntry(
        accomplishedText: String,
        obstaclesText: String,
        distractionsText: String,
        learningsText: String,
        improvementsText: String,
        freeformText: String,
        tags: String = "",
        date: String = getTodayDateString()
    ): Long {
        val entry = JournalEntryEntity(
            date = date,
            accomplishedText = accomplishedText,
            obstaclesText = obstaclesText,
            distractionsText = distractionsText,
            learningsText = learningsText,
            improvementsText = improvementsText,
            freeformText = freeformText,
            tags = tags
        )
        val id = mindsetDao.insertJournalEntry(entry)
        awardXp(50)
        recalculateDailyRollup(date)
        return id
    }

    // 15. Focus Sessions
    fun getFocusSessionsForDate(date: String = getTodayDateString()): Flow<List<FocusSessionEntity>> = focusDao.getSessionsForDate(date)
    val recentFocusSessions: Flow<List<FocusSessionEntity>> = focusDao.getRecentSessions()

    suspend fun completeFocusSession(
        taskName: String,
        mode: String,
        durationMinutes: Int,
        focusRating: Int,
        distractionsCount: Int,
        notes: String = "",
        date: String = getTodayDateString()
    ): Long {
        val session = FocusSessionEntity(
            date = date,
            taskName = taskName,
            mode = mode,
            durationMinutes = durationMinutes,
            focusRating = focusRating,
            distractionsCount = distractionsCount,
            notes = notes,
            isCompleted = true,
            endTime = System.currentTimeMillis()
        )
        val id = focusDao.insertSession(session)
        val xpGain = (durationMinutes * 2).coerceAtLeast(20)
        awardXp(xpGain)
        recalculateDailyRollup(date)
        return id
    }

    suspend fun logFocusDistraction(sessionId: Long, description: String): Long {
        return focusDao.insertDistraction(FocusDistractionEntity(sessionId = sessionId, description = description))
    }

    // 16. Goals & Milestones
    val allGoals: Flow<List<GoalEntity>> = goalDao.getAllGoals()
    fun getGoalsByLevel(level: String): Flow<List<GoalEntity>> = goalDao.getGoalsByLevel(level)
    fun getMilestonesForGoal(goalId: Long): Flow<List<GoalMilestoneEntity>> = goalDao.getMilestonesForGoal(goalId)

    suspend fun addGoal(
        title: String,
        description: String,
        level: String,
        category: String,
        deadline: String,
        priority: String = "medium"
    ): Long {
        val goal = GoalEntity(
            title = title,
            description = description,
            level = level,
            category = category,
            deadline = deadline,
            priority = priority
        )
        return goalDao.insertGoal(goal)
    }

    suspend fun addGoalMilestone(goalId: Long, title: String, orderIndex: Int = 0): Long {
        return goalDao.insertMilestone(GoalMilestoneEntity(goalId = goalId, title = title, orderIndex = orderIndex))
    }

    suspend fun toggleGoalMilestone(milestone: GoalMilestoneEntity, goalId: Long) {
        goalDao.updateMilestone(milestone.copy(isCompleted = !milestone.isCompleted))
        // Recalculate goal progress
        val allMilestones = goalDao.getMilestonesForGoalSync(goalId)
        if (allMilestones.isNotEmpty()) {
            val completed = allMilestones.count { it.isCompleted }
            val progress = completed.toFloat() / allMilestones.size
            val goal = goalDao.getGoalByIdSync(goalId)
            if (goal != null) {
                goalDao.updateGoal(goal.copy(progress = progress, isCompleted = progress >= 1f))
            }
        }
    }

    suspend fun updateGoalProgress(goal: GoalEntity, progress: Float) {
        goalDao.updateGoal(goal.copy(progress = progress.coerceIn(0f, 1f), isCompleted = progress >= 1f))
    }

    suspend fun deleteGoal(goal: GoalEntity) {
        goalDao.deleteGoal(goal)
        goalDao.deleteMilestonesForGoal(goal.id)
    }

    suspend fun convertGoalToPlannerTask(goalTitle: String, category: String, xpReward: Int = 60, date: String = getTodayDateString()): Long {
        val task = TaskEntity(
            title = "🎯 $goalTitle",
            category = category,
            priorityLevel = 1,
            xpReward = xpReward,
            date = date
        )
        return plannerDao.insertTask(task)
    }

    // 17. PRIME AI Chat & Analytics
    val allAiMessages: Flow<List<AiChatMessageEntity>> = aiDao.getAllMessages()
    fun getAiMessagesForAssistant(assistantType: String): Flow<List<AiChatMessageEntity>> = aiDao.getMessagesForAssistant(assistantType)
    fun getAiUsageForDate(date: String = getTodayDateString()): Flow<AiUsageStatsEntity?> = aiDao.getUsageForDate(date)

    suspend fun saveAiChatMessage(
        sender: String,
        content: String,
        assistantType: String,
        actionTitle: String? = null,
        actionPayload: String? = null,
        actionType: String? = null
    ): Long {
        val msg = AiChatMessageEntity(
            sender = sender,
            content = content,
            assistantType = assistantType,
            suggestedActionTitle = actionTitle,
            suggestedActionPayload = actionPayload,
            suggestedActionType = actionType
        )
        return aiDao.insertMessage(msg)
    }

    suspend fun clearAiChat(assistantType: String? = null) {
        if (assistantType != null) {
            aiDao.clearMessagesForAssistant(assistantType)
        } else {
            aiDao.clearAllMessages()
        }
    }

    suspend fun trackAiUsage(date: String = getTodayDateString(), isError: Boolean = false, isRateLimit: Boolean = false) {
        val existing = aiDao.getUsageForDateSync(date) ?: AiUsageStatsEntity(date = date)
        aiDao.insertOrUpdateUsage(
            existing.copy(
                requestsCount = existing.requestsCount + 1,
                errorsCount = existing.errorsCount + if (isError) 1 else 0,
                rateLimitHits = existing.rateLimitHits + if (isRateLimit) 1 else 0,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun buildAiContextData(date: String = getTodayDateString()): AiContextData {
        val profile = profileDao.getProfileSync()
        val todayEntry = dailyEntryDao.getEntryForDateSync(date)
        val todayNutrition = nutritionDao.getNutritionLogsForDateSync(date)
        val recentWorkouts = workoutDao.getAllSessionsSync()
        val recentSleep = sleepDao.getRecentSleepLogs().firstOrNull() ?: emptyList()
        val activeHabits = habitDao.getAllActiveHabitsSync()
        val todayTasks = plannerDao.getTasksForDateSync(date)
        val activeGoals = goalDao.getAllGoalsSync()
        val recentMindset = mindsetDao.getRecentMindsetLogs().firstOrNull() ?: emptyList()

        return AiContextData(
            profile = profile,
            todayEntry = todayEntry,
            todayNutrition = todayNutrition,
            recentWorkouts = recentWorkouts,
            recentSleep = recentSleep,
            activeHabits = activeHabits,
            todayTasks = todayTasks,
            activeGoals = activeGoals,
            recentMindset = recentMindset
        )
    }

    suspend fun generateDailyCoachSummary(date: String = getTodayDateString()): DailyCoachSummary {
        val context = buildAiContextData(date)
        return try {
            val summary = aiProvider.generateDailyCoachingSummary(context)
            trackAiUsage(date)
            summary
        } catch (e: Exception) {
            trackAiUsage(date, isError = true)
            aiProvider.generateDailyCoachingSummary(context)
        }
    }

    suspend fun generateWeeklyReviewSummary(date: String = getTodayDateString()): WeeklyReviewSummary {
        val context = buildAiContextData(date)
        val recentEntries = dailyEntryDao.getRecentEntries().firstOrNull() ?: emptyList()
        return try {
            val summary = aiProvider.generateWeeklyReview(context, recentEntries)
            trackAiUsage(date)
            summary
        } catch (e: Exception) {
            trackAiUsage(date, isError = true)
            aiProvider.generateWeeklyReview(context, recentEntries)
        }
    }

    suspend fun sendAssistantChatMessage(
        assistantType: String,
        userMessage: String,
        chatHistory: List<Pair<String, String>>,
        date: String = getTodayDateString()
    ): AiMessageResponse {
        val context = buildAiContextData(date)
        return try {
            val response = aiProvider.sendAssistantMessage(assistantType, userMessage, chatHistory, context)
            trackAiUsage(date)
            response
        } catch (e: Exception) {
            trackAiUsage(date, isError = true)
            aiProvider.sendAssistantMessage(assistantType, userMessage, chatHistory, context)
        }
    }

    suspend fun estimateFoodNutrition(prompt: String): NutritionEstimateResult {
        return try {
            val res = aiProvider.estimateNutritionFromPrompt(prompt)
            trackAiUsage(getTodayDateString())
            res
        } catch (e: Exception) {
            trackAiUsage(getTodayDateString(), isError = true)
            aiProvider.estimateNutritionFromPrompt(prompt)
        }
    }

    fun getDashboardAiInsight(
        recentEntries: List<DailyEntryEntity>,
        todayNutrition: List<NutritionLogEntity>,
        recentSleep: List<SleepLogEntity>,
        recentWorkouts: List<WorkoutSessionEntity>
    ): PrimeAiInsight {
        return aiProvider.getRuleBasedDashboardInsight(recentEntries, todayNutrition, recentSleep, recentWorkouts)
    }

    // 19. PRIME Score Recalculation Engine
    suspend fun recalculateDailyRollup(date: String = getTodayDateString()) {
        val workoutSessions = workoutDao.getSessionsForDateSync(date)
        val workoutCompleted = workoutSessions.isNotEmpty() && workoutSessions.any { it.durationMinutes > 0 }
        val workoutDuration = workoutSessions.sumOf { it.durationMinutes }

        val nutritionLogs = nutritionDao.getNutritionLogsForDateSync(date)
        val totalCalories: Int = nutritionLogs.sumOf { it.calories }
        val totalProtein: Float = nutritionLogs.sumOf { it.proteinGrams.toDouble() }.toFloat()
        val totalCarbs: Float = nutritionLogs.sumOf { it.carbsGrams.toDouble() }.toFloat()
        val totalFat: Float = nutritionLogs.sumOf { it.fatGrams.toDouble() }.toFloat()

        val hydrationLogs = nutritionDao.getHydrationLogsForDateSync(date)
        val totalWater: Int = hydrationLogs.sumOf { it.amountMl }

        val habitLogs = habitDao.getHabitLogsForDateSync(date)
        val habits = habitDao.getAllActiveHabitsSync()
        val completedHabitsCount: Int = habitLogs.count { it.isCompleted }
        val totalHabitsCount: Int = habits.size

        val tasks = plannerDao.getTasksForDateSync(date)
        val completedTasks: Int = tasks.count { it.isCompleted }

        val groomingItems = groomingDao.getItemsForRoutine("morning").firstOrNull() ?: emptyList()
        val groomingLogs = groomingDao.getLogsForDateSync(date)
        val groomingRatio = if (groomingItems.isNotEmpty()) (groomingLogs.size.toFloat() / groomingItems.size.toFloat()).coerceIn(0f, 1f) else 1f

        val skincareProducts = skincareDao.getAllProducts().firstOrNull() ?: emptyList()
        val skincareLogs = skincareDao.getLogsForDateSync(date)
        val skincareRatio = if (skincareProducts.isNotEmpty()) (skincareLogs.size.toFloat() / skincareProducts.size.toFloat()).coerceIn(0f, 1f) else 1f

        val sleepLog = sleepDao.getSleepLogForDateSync(date)
        val mindsetLog = mindsetDao.getMindsetLogForDateSync(date)
        val journalEntry = mindsetDao.getJournalForDateSync(date)
        val focusSessions = focusDao.getSessionsForDateSync(date)
        val totalFocusMinutes: Int = focusSessions.sumOf { it.durationMinutes }

        val profile = profileDao.getProfileSync() ?: ProfileEntity()
        val targetCalories = profile.targetCalories
        val targetProtein = profile.targetProteinGrams
        val targetWater = profile.targetWaterMl

        // Sub-score calculations (Strictly calibrated for Phase 2 spec):
        // 1. Fitness (10%): Workout completion & volume
        val fitnessScore: Float = if (workoutCompleted) 10f else if (workoutSessions.isNotEmpty()) 6f else 0f

        // 2. Nutrition (10%): Macro balance & Calorie compliance
        val calDiffRatio = if (targetCalories > 0) abs(totalCalories - targetCalories).toFloat() / targetCalories.toFloat() else 0f
        val proteinRatio = if (targetProtein > 0) (totalProtein / targetProtein.toFloat()).coerceIn(0f, 1f) else 1f
        val nutritionScore: Float = ((1f - calDiffRatio.coerceIn(0f, 1f)) * 5f + proteinRatio * 5f).coerceIn(0f, 10f)

        // 3. Health (10%): Hydration (5%) + Sleep Hygiene (5%)
        val waterRatio = if (targetWater > 0) (totalWater.toFloat() / targetWater.toFloat()).coerceIn(0f, 1f) else 1f
        val sleepRatio = if (sleepLog != null) (sleepLog.durationMinutes.toFloat() / 480f).coerceIn(0f, 1.2f).coerceAtMost(1f) else 0.8f
        val healthScore: Float = (waterRatio * 5f + sleepRatio * 5f).coerceIn(0f, 10f)

        // 4. Discipline (10%): Habits & Routine Checklists
        val habitRatio = if (totalHabitsCount > 0) completedHabitsCount.toFloat() / totalHabitsCount.toFloat() else 1f
        val disciplineScore: Float = (habitRatio * 7f + groomingRatio * 3f).coerceIn(0f, 10f)

        // 5. Focus (10%): Deep Focus sessions (5%) + Priority Task closures (5%)
        val taskRatio = if (tasks.isNotEmpty()) completedTasks.toFloat() / tasks.size.toFloat() else 1f
        val focusTimeRatio = (totalFocusMinutes.toFloat() / 90f).coerceIn(0f, 1f)
        val focusScore: Float = (taskRatio * 5f + (if (totalFocusMinutes > 0) focusTimeRatio * 5f else taskRatio * 5f)).coerceIn(0f, 10f)

        // 6. Body (15%): Tracking consistency & physical adherence
        val bodyScore: Float = if (totalCalories > 0 && totalWater > 0) 15f else if (totalCalories > 0 || totalWater > 0) 10f else 4f

        // 7. Mindset (10%): Mood/Energy scale rating (6%) + Journaling reflection (4%)
        val mindsetScaleRatio = if (mindsetLog != null) (mindsetLog.moodRating + mindsetLog.energyRating + (11 - mindsetLog.stressRating)).toFloat() / 30f else 0.8f
        val journalBonus = if (journalEntry != null && (journalEntry.accomplishedText.isNotBlank() || journalEntry.freeformText.isNotBlank())) 4f else 2f
        val mindsetScore: Float = (mindsetScaleRatio * 6f + journalBonus).coerceIn(0f, 10f)

        // 8. Appearance (10%): Grooming & Skincare execution
        val appearanceScore: Float = (groomingRatio * 5f + skincareRatio * 5f).coerceIn(0f, 10f)

        // 9. Career (5%): Career goals, skills & planner execution
        val careerSkills = careerDao.getAllSkillsSync()
        val careerScore: Float = if (completedTasks > 0 && careerSkills.isNotEmpty()) 5.0f else if (completedTasks > 0 || careerSkills.isNotEmpty()) 4.0f else 3.0f

        // 10. Learning (5%): Study sessions logged & reading progression
        val todayStudySessions = learningDao.getStudySessionsForDateSync(date)
        val studyMinutes = todayStudySessions.sumOf { it.durationMinutes }
        val learningScore: Float = if (studyMinutes >= 30) 5.0f else if (studyMinutes > 0 || (journalEntry != null && journalEntry.learningsText.isNotBlank())) 4.0f else 3.0f

        // 11. Finance (5%): Budget, cashflow & capital allocation tracking
        val todayTransactions = financeDao.getTransactionsForDateSync(date)
        val financeScore: Float = if (todayTransactions.isNotEmpty()) 5.0f else 4.0f

        val totalScore: Int = (fitnessScore + nutritionScore + healthScore + disciplineScore + focusScore + bodyScore + mindsetScore + appearanceScore + careerScore + learningScore + financeScore).toInt().coerceIn(0, 100)

        val totalDaysCount = dailyEntryDao.getTotalLoggedDaysCountSync()
        val isCalibrated = totalDaysCount >= 3

        val explanations = buildList {
            if (workoutCompleted) add("Fitness (+${fitnessScore.toInt()} pts): Workout completed ($workoutDuration mins)")
            else add("Fitness (0 pts): No workout logged yet today")

            add("Nutrition (${nutritionScore.toInt()}/10 pts): Logged $totalCalories kcal & ${totalProtein.toInt()}g protein")
            add("Health (${healthScore.toInt()}/10 pts): Hydration $totalWater ml | Sleep ${sleepLog?.durationMinutes?.div(60) ?: 8}h")
            add("Discipline (${disciplineScore.toInt()}/10 pts): $completedHabitsCount / $totalHabitsCount habits completed")
            add("Focus (${focusScore.toInt()}/10 pts): $completedTasks / ${tasks.size} tasks | ${totalFocusMinutes}m deep focus")
            add("Body (${bodyScore.toInt()}/15 pts): Caloric and biometric telemetry logged")
            add("Mindset & Journal (${mindsetScore.toInt()}/10 pts): Mood/Energy calibration & reflection")
            add("Appearance & Grooming (${appearanceScore.toInt()}/10 pts): Grooming & Skincare routine execution")
            add("Career Mastery (${careerScore.toInt()}/5 pts): Skill matrix & deliverables")
            add("Learning & Intellect (${learningScore.toInt()}/5 pts): ${studyMinutes}m study session & reading")
            add("Finance & Runway (${financeScore.toInt()}/5 pts): Cashflow & savings allocation")
        }

        val scoreEntity = DailyScoreRecordEntity(
            date = date,
            totalScore = totalScore,
            bodyScore = bodyScore,
            fitnessScore = fitnessScore,
            nutritionScore = nutritionScore,
            healthScore = healthScore,
            appearanceScore = appearanceScore,
            mindsetScore = mindsetScore,
            focusScore = focusScore,
            disciplineScore = disciplineScore,
            careerScore = careerScore,
            learningScore = learningScore,
            financeScore = financeScore,
            isBaselineCalibrated = isCalibrated,
            explanationsJson = explanations.joinToString("|||")
        )
        dailyScoreDao.insertScore(scoreEntity)

        val existingEntry = dailyEntryDao.getEntryForDateSync(date)
        val rollupEntry = DailyEntryEntity(
            date = date,
            workoutDone = workoutCompleted,
            workoutDurationMinutes = workoutDuration,
            caloriesLogged = totalCalories,
            proteinGrams = totalProtein,
            carbsGrams = totalCarbs,
            fatGrams = totalFat,
            waterTotalMl = totalWater,
            mood = mindsetLog?.moodLabel ?: existingEntry?.mood ?: "Good",
            sleepHours = if (sleepLog != null) sleepLog.durationMinutes / 60f else existingEntry?.sleepHours ?: 7.5f,
            habitsCompletedCount = completedHabitsCount,
            habitsTotalCount = totalHabitsCount,
            focusMinutes = if (totalFocusMinutes > 0) totalFocusMinutes else (completedTasks * 25),
            primeScore = totalScore,
            notes = existingEntry?.notes ?: "",
            source = "manual"
        )
        dailyEntryDao.insertOrUpdate(rollupEntry)
    }

    private suspend fun awardXp(amount: Int) {
        val currentProfile = profileDao.getProfileSync() ?: ProfileEntity()
        var newXp = currentProfile.xp + amount
        var newLevel = currentProfile.level
        var xpRequired = currentProfile.xpToNextLevel

        while (newXp >= xpRequired) {
            newXp -= xpRequired
            newLevel++
            xpRequired = (xpRequired * 1.3f).toInt()
        }

        profileDao.insertOrUpdate(
            currentProfile.copy(
                xp = newXp,
                level = newLevel,
                xpToNextLevel = xpRequired
            )
        )
    }

    // ----------------------------------------------------
    // PHASE 3: CAREER
    // ----------------------------------------------------
    val allCareerGoals: Flow<List<CareerGoalEntity>> = careerDao.getAllCareerGoals()
    val allCareerSkills: Flow<List<CareerSkillEntity>> = careerDao.getAllCareerSkills()
    val allCertifications: Flow<List<CertificationEntity>> = careerDao.getAllCertifications()
    val allCareerProjects: Flow<List<CareerProjectEntity>> = careerDao.getAllCareerProjects()
    val allCareerAchievements: Flow<List<CareerAchievementEntity>> = careerDao.getAllCareerAchievements()
    val allResumeItems: Flow<List<ResumeItemEntity>> = careerDao.getAllResumeItems()

    suspend fun addCareerGoal(title: String, targetRole: String, targetSalaryOrRevenue: String, timeline: String) {
        careerDao.insertCareerGoal(
            CareerGoalEntity(
                title = title,
                targetRole = targetRole,
                targetSalaryOrRevenue = targetSalaryOrRevenue,
                timeline = timeline,
                progress = 0.25f
            )
        )
        awardXp(50)
    }

    suspend fun updateCareerGoalProgress(goal: CareerGoalEntity, newProgress: Float) {
        careerDao.updateCareerGoal(goal.copy(progress = newProgress, status = if (newProgress >= 1f) "achieved" else "active"))
        if (newProgress >= 1f) awardXp(100)
    }

    suspend fun addCareerSkill(name: String, category: String, proficiencyPercent: Int, targetProficiencyPercent: Int) {
        careerDao.insertCareerSkill(
            CareerSkillEntity(
                name = name,
                category = category,
                proficiencyPercent = proficiencyPercent,
                targetProficiencyPercent = targetProficiencyPercent
            )
        )
        awardXp(30)
    }

    suspend fun updateSkillLevel(skill: CareerSkillEntity, newProficiencyPercent: Int) {
        careerDao.updateCareerSkill(skill.copy(proficiencyPercent = newProficiencyPercent))
        awardXp(20)
    }

    suspend fun deleteCareerSkill(skill: CareerSkillEntity) {
        careerDao.deleteCareerSkill(skill)
    }

    suspend fun addCertification(name: String, issuingOrganization: String, issueDate: String, status: String) {
        careerDao.insertCertification(
            CertificationEntity(
                name = name,
                issuingOrganization = issuingOrganization,
                issueDate = issueDate,
                status = status
            )
        )
        awardXp(75)
    }

    suspend fun addCareerProject(title: String, role: String, description: String, keyOutcomes: String, techStack: String) {
        careerDao.insertCareerProject(
            CareerProjectEntity(
                title = title,
                role = role,
                description = description,
                keyOutcomes = keyOutcomes,
                techStack = techStack
            )
        )
        awardXp(60)
    }

    suspend fun addCareerAchievement(title: String, date: String, impactMetric: String) {
        careerDao.insertCareerAchievement(
            CareerAchievementEntity(
                title = title,
                date = date,
                impactMetric = impactMetric
            )
        )
        awardXp(50)
    }

    // ----------------------------------------------------
    // PHASE 3: LEARNING
    // ----------------------------------------------------
    val allCourses: Flow<List<CourseEntity>> = learningDao.getAllCourses()
    val allBooks: Flow<List<BookEntity>> = learningDao.getAllBooks()
    val allStudySessions: Flow<List<StudySessionEntity>> = learningDao.getAllStudySessions()

    suspend fun addLearningCourse(title: String, platform: String, instructor: String, totalModules: Int) {
        learningDao.insertCourse(
            CourseEntity(
                title = title,
                platform = platform,
                instructor = instructor,
                totalModules = totalModules,
                completedModules = 0
            )
        )
        awardXp(40)
    }

    suspend fun updateCourseProgress(course: CourseEntity, completedCount: Int) {
        val completed = completedCount >= course.totalModules
        learningDao.updateCourse(course.copy(completedModules = completedCount, status = if (completed) "Completed" else "In Progress"))
        if (completed) awardXp(150)
    }

    suspend fun addLearningBook(title: String, author: String, format: String, totalPages: Int) {
        learningDao.insertBook(
            BookEntity(
                title = title,
                author = author,
                format = format,
                totalPages = totalPages,
                currentPage = 0
            )
        )
        awardXp(30)
    }

    suspend fun updateBookProgress(book: BookEntity, newPage: Int) {
        val completed = newPage >= book.totalPages
        learningDao.updateBook(book.copy(currentPage = newPage, status = if (completed) "Finished" else "Reading"))
        if (completed) awardXp(100)
    }

    suspend fun logStudySession(topic: String, category: String, durationMinutes: Int, keyInsights: String) {
        learningDao.insertStudySession(
            StudySessionEntity(
                date = getTodayDateString(),
                topic = topic,
                category = category,
                durationMinutes = durationMinutes,
                keyInsights = keyInsights
            )
        )
        awardXp(30)
    }

    // ----------------------------------------------------
    // PHASE 3: FINANCE
    // ----------------------------------------------------
    val allFinanceTransactions: Flow<List<FinanceTransactionEntity>> = financeDao.getAllTransactions()
    val allFinancialGoals: Flow<List<FinancialGoalEntity>> = financeDao.getAllFinancialGoals()

    suspend fun addFinanceTransaction(type: String, category: String, amount: Double, notes: String, isRecurring: Boolean) {
        financeDao.insertTransaction(
            FinanceTransactionEntity(
                type = type,
                category = category,
                amount = amount,
                notes = notes,
                isRecurring = isRecurring,
                date = getTodayDateString()
            )
        )
        awardXp(20)
    }

    suspend fun deleteFinanceTransaction(transaction: FinanceTransactionEntity) {
        financeDao.deleteTransaction(transaction)
    }

    suspend fun addFinancialGoal(title: String, goalType: String, targetAmount: Double, currentAmount: Double, deadline: String) {
        financeDao.insertFinancialGoal(
            FinancialGoalEntity(
                title = title,
                goalType = goalType,
                targetAmount = targetAmount,
                currentAmount = currentAmount,
                deadline = deadline
            )
        )
        awardXp(50)
    }

    suspend fun updateFinancialGoalProgress(goal: FinancialGoalEntity, addedAmount: Double) {
        val updated = goal.copy(currentAmount = goal.currentAmount + addedAmount)
        financeDao.updateFinancialGoal(updated)
        awardXp(30)
    }

    // ----------------------------------------------------
    // PHASE 3: GAMIFICATION & STREAKS
    // ----------------------------------------------------
    val allAchievementBadges: Flow<List<AchievementBadgeEntity>> = gamificationDao.getAllBadges()
    val allXpTransactions: Flow<List<XpTransactionEntity>> = gamificationDao.getRecentXpTransactions()

    suspend fun logXpBonus(source: String, xp: Int, notes: String = "") {
        gamificationDao.insertXpTransaction(
            XpTransactionEntity(
                date = getTodayDateString(),
                source = source,
                xpEarned = xp,
                notes = notes
            )
        )
        awardXp(xp)
    }

    suspend fun activateStreakGraceShield() {
        val profile = profileDao.getProfileSync() ?: return
        profileDao.insertOrUpdate(profile.copy(streakShieldAvailable = false))
    }

    // ----------------------------------------------------
    // PHASE 3: VISION & WEEKLY REVIEWS
    // ----------------------------------------------------
    val allVisionItems: Flow<List<VisionItemEntity>> = visionAndReviewDao.getAllVisionItems()
    val allWeeklyReviews: Flow<List<WeeklyReviewRecordEntity>> = visionAndReviewDao.getAllWeeklyReviews()

    suspend fun addVisionMilestone(title: String, content: String, horizon: String) {
        visionAndReviewDao.insertVisionItem(
            VisionItemEntity(
                section = "roadmap_milestone",
                title = title,
                content = content,
                targetHorizon = horizon
            )
        )
        awardXp(50)
    }

    suspend fun toggleVisionMilestone(item: VisionItemEntity) {
        visionAndReviewDao.updateVisionItem(item.copy(isCompleted = !item.isCompleted))
        if (!item.isCompleted) awardXp(50)
    }

    suspend fun updateIdentityStatements(whoIAm: String, whoIWantToBecome: String) {
        val currentProfile = profileDao.getProfileSync() ?: ProfileEntity()
        profileDao.insertOrUpdate(currentProfile.copy(bio = whoIAm))
    }

    suspend fun generateAndSaveWeeklyReview() {
        val summary = generateWeeklyReviewSummary(getTodayDateString())
        val review = WeeklyReviewRecordEntity(
            weekLabel = "Week of ${getTodayDateString()}",
            overallScorePercent = 88,
            strongestCategory = summary.wins.firstOrNull()?.take(25) ?: "Nutrition & Strength",
            weakestCategory = summary.recommendations.firstOrNull()?.take(25) ?: "Evening Screen Curfew",
            winsJson = summary.wins.joinToString("|||"),
            weaknessesJson = (summary.weaknesses.ifEmpty {
                listOf(
                    "Late caffeine intake on Wednesday delayed deep sleep onset.",
                    "Hydration dipped slightly below 3.0L on travel day.",
                    "Missed scheduled mobility stretching session on Friday."
                )
            }).joinToString("|||"),
            recommendationsJson = summary.recommendations.joinToString("|||")
        )
        visionAndReviewDao.insertWeeklyReview(review)
        awardXp(100)
    }

    suspend fun completeOnboarding(
        name: String,
        goal: String,
        calories: Int,
        protein: Float,
        sleepHours: Float,
        focusMinutes: Int,
        currency: String
    ) {
        val current = profileDao.getProfileSync() ?: ProfileEntity()
        val updated = current.copy(
            name = name,
            primaryGoal = goal,
            targetCalories = calories,
            targetProteinGrams = protein,
            targetWaterMl = 3500,
            currency = currency,
            isOnboardingCompleted = true,
            xp = current.xp + 200
        )
        profileDao.insertOrUpdate(updated)
        awardXp(200)
        recalculateDailyRollup(getTodayDateString())
    }

    suspend fun wipeAllUserData() {
        dailyEntryDao.deleteAll()
        bodyDao.deleteAllMeasurements()
        bodyDao.deleteAllPhotos()
        workoutDao.deleteAllSessions()
        workoutDao.deleteAllSets()
        nutritionDao.deleteAllNutritionLogs()
        nutritionDao.deleteAllHydrationLogs()
        habitDao.deleteAllHabitLogs()
        plannerDao.deleteAllTasks()
        plannerDao.deleteAllTimeBlocks()
        dailyScoreDao.deleteAllScores()
        groomingDao.deleteAllItems()
        groomingDao.deleteAllLogs()
        skincareDao.deleteAllProducts()
        skincareDao.deleteAllLogs()
        sleepDao.deleteAllSleepLogs()
        mindsetDao.deleteAllMindsetLogs()
        mindsetDao.deleteAllJournalEntries()
        focusDao.deleteAllSessions()
        focusDao.deleteAllDistractions()
        goalDao.deleteAllGoals()
        goalDao.deleteAllMilestones()
        aiDao.clearAllMessages()
        careerDao.deleteAllGoals()
        careerDao.deleteAllSkills()
        careerDao.deleteAllCerts()
        careerDao.deleteAllAchievements()
        careerDao.deleteAllProjects()
        careerDao.deleteAllResumeItems()
        learningDao.deleteAllCourses()
        learningDao.deleteAllBooks()
        learningDao.deleteAllStudySessions()
        financeDao.deleteAllTransactions()
        financeDao.deleteAllGoals()
        gamificationDao.deleteAllBadges()
        gamificationDao.deleteAllXpTransactions()
        visionAndReviewDao.deleteAllVisionItems()
        visionAndReviewDao.deleteAllWeeklyReviews()
        seedInitialDataIfEmpty()
    }

    private suspend fun seedInitialDataIfEmpty() {
        val profile = profileDao.getProfileSync()
        if (profile == null) {
            profileDao.insertOrUpdate(
                ProfileEntity(
                    name = "Eswaran",
                    title = "Apex Operator",
                    level = 3,
                    xp = 680,
                    xpToNextLevel = 1200,
                    currentStreak = 4,
                    primaryGoal = "Muscle Gain & Peak Performance"
                )
            )
        } else if (profile.name.equals("Hunter", ignoreCase = true)) {
            profileDao.insertOrUpdate(profile.copy(name = "Eswaran"))
        }

        // Seed default starter exercises if none exist
        val existingExercises = workoutDao.getAllExercises().firstOrNull()
        if (existingExercises.isNullOrEmpty()) {
            val starterExercises = listOf(
                ExerciseEntity(name = "Barbell Bench Press", muscleGroup = "Chest", equipment = "Barbell", personalRecordWeightKg = 100f, personalRecordReps = 5),
                ExerciseEntity(name = "Incline Dumbbell Press", muscleGroup = "Chest", equipment = "Dumbbell", personalRecordWeightKg = 34f, personalRecordReps = 8),
                ExerciseEntity(name = "Barbell Back Squat", muscleGroup = "Legs", equipment = "Barbell", personalRecordWeightKg = 140f, personalRecordReps = 5),
                ExerciseEntity(name = "Romanian Deadlift", muscleGroup = "Legs", equipment = "Barbell", personalRecordWeightKg = 120f, personalRecordReps = 8),
                ExerciseEntity(name = "Overhead Barbell Press", muscleGroup = "Shoulders", equipment = "Barbell", personalRecordWeightKg = 65f, personalRecordReps = 6),
                ExerciseEntity(name = "Weighted Pull-Ups", muscleGroup = "Back", equipment = "Bodyweight", personalRecordWeightKg = 20f, personalRecordReps = 6),
                ExerciseEntity(name = "Barbell Row", muscleGroup = "Back", equipment = "Barbell", personalRecordWeightKg = 85f, personalRecordReps = 8),
                ExerciseEntity(name = "Dips", muscleGroup = "Chest", equipment = "Bodyweight", personalRecordWeightKg = 25f, personalRecordReps = 8),
                ExerciseEntity(name = "Incline Dumbbell Curl", muscleGroup = "Arms", equipment = "Dumbbell", personalRecordWeightKg = 18f, personalRecordReps = 10),
                ExerciseEntity(name = "Overhead Tricep Extension", muscleGroup = "Arms", equipment = "Cable", personalRecordWeightKg = 35f, personalRecordReps = 12),
                ExerciseEntity(name = "Hanging Leg Raises", muscleGroup = "Core", equipment = "Bodyweight", personalRecordWeightKg = 0f, personalRecordReps = 15)
            )
            starterExercises.forEach { workoutDao.insertExercise(it) }
        }

        // Seed workout templates
        val existingTemplates = workoutDao.getAllTemplates().firstOrNull()
        if (existingTemplates.isNullOrEmpty()) {
            val templates = listOf(
                WorkoutTemplateEntity(
                    name = "Push Day A: Hypertrophy & Apex Strength",
                    category = "PPL",
                    description = "Focus on Chest, Shoulders, and Tricep overload with heavy presses and accessory flyes.",
                    estimatedDurationMinutes = 55,
                    isStarter = true
                ),
                WorkoutTemplateEntity(
                    name = "Pull Day A: Heavy Back & Bicep Density",
                    category = "PPL",
                    description = "Heavy rows, weighted pull-ups, and targeted rear delt/bicep isolation.",
                    estimatedDurationMinutes = 50,
                    isStarter = true
                ),
                WorkoutTemplateEntity(
                    name = "Leg Day: Quadriceps, Hamstrings & Calves",
                    category = "PPL",
                    description = "High intensity squat variations and posterior chain development.",
                    estimatedDurationMinutes = 60,
                    isStarter = true
                ),
                WorkoutTemplateEntity(
                    name = "Upper Body Conditioning",
                    category = "Upper/Lower",
                    description = "Fast-paced supersets designed for athletic power and metabolic conditioning.",
                    estimatedDurationMinutes = 40,
                    isStarter = true
                )
            )
            templates.forEach { workoutDao.insertTemplate(it) }
        }

        // Seed food items
        val existingFoods = nutritionDao.getAllFoods().firstOrNull()
        if (existingFoods.isNullOrEmpty()) {
            val foods = listOf(
                FoodItemEntity(name = "Grilled Chicken Breast", servingSize = "150g", caloriesPerServing = 248, proteinGrams = 46f, carbsGrams = 0f, fatGrams = 5f, isFavorite = true),
                FoodItemEntity(name = "Jasmine White Rice (Cooked)", servingSize = "1 cup (160g)", caloriesPerServing = 205, proteinGrams = 4.2f, carbsGrams = 45f, fatGrams = 0.5f, isFavorite = true),
                FoodItemEntity(name = "Wild Atlantic Salmon", servingSize = "200g", caloriesPerServing = 412, proteinGrams = 40f, carbsGrams = 0f, fatGrams = 26f, isFavorite = true),
                FoodItemEntity(name = "Whole Eggs", servingSize = "2 Large (100g)", caloriesPerServing = 143, proteinGrams = 12.6f, carbsGrams = 0.7f, fatGrams = 9.5f, isFavorite = true),
                FoodItemEntity(name = "Whey Isolate Protein Powder", servingSize = "1 Scoop (30g)", caloriesPerServing = 120, proteinGrams = 25f, carbsGrams = 2f, fatGrams = 1f, isFavorite = true),
                FoodItemEntity(name = "Sweet Potato (Baked)", servingSize = "1 Medium (150g)", caloriesPerServing = 135, proteinGrams = 3f, carbsGrams = 31f, fatGrams = 0.2f, isFavorite = true),
                FoodItemEntity(name = "Plain Non-Fat Greek Yogurt", servingSize = "1 Cup (225g)", caloriesPerServing = 130, proteinGrams = 22f, carbsGrams = 8f, fatGrams = 0f, isFavorite = true),
                FoodItemEntity(name = "Old Fashioned Rolled Oats", servingSize = "1/2 Cup (40g)", caloriesPerServing = 150, proteinGrams = 5f, carbsGrams = 27f, fatGrams = 3f, isFavorite = true),
                FoodItemEntity(name = "Raw Almonds", servingSize = "1 oz (28g)", caloriesPerServing = 164, proteinGrams = 6f, carbsGrams = 6f, fatGrams = 14f, isFavorite = false),
                FoodItemEntity(name = "Avocado", servingSize = "1/2 Medium (75g)", caloriesPerServing = 120, proteinGrams = 1.5f, carbsGrams = 6f, fatGrams = 11f, isFavorite = false)
            )
            foods.forEach { nutritionDao.insertFood(it) }
        }

        // Seed habits
        val existingHabits = habitDao.getAllActiveHabits().firstOrNull()
        if (existingHabits.isNullOrEmpty()) {
            val habits = listOf(
                HabitEntity(name = "Hydrate 3.5 Liters Clean Water", iconName = "water_drop", category = "Health", frequency = "Daily", targetCountDaily = 1, reminderTime = "08:00", currentStreak = 4),
                HabitEntity(name = "Execute Heavy Strength Workout", iconName = "fitness_center", category = "Fitness", frequency = "Daily", targetCountDaily = 1, reminderTime = "17:00", currentStreak = 3),
                HabitEntity(name = "Deep Work Block (90 Mins No Phone)", iconName = "code", category = "Focus", frequency = "Daily", targetCountDaily = 1, reminderTime = "09:30", currentStreak = 5),
                HabitEntity(name = "Read 20 Mins High-Signal Literature", iconName = "menu_book", category = "Learning", frequency = "Daily", targetCountDaily = 1, reminderTime = "21:00", currentStreak = 2),
                HabitEntity(name = "Cold Shower & Joint Mobility", iconName = "shower", category = "Discipline", frequency = "Daily", targetCountDaily = 1, reminderTime = "07:00", currentStreak = 4),
                HabitEntity(name = "8 Hours Sleep Recovery", iconName = "bedtime", category = "Health", frequency = "Daily", targetCountDaily = 1, reminderTime = "22:30", currentStreak = 3)
            )
            habits.forEach { habitDao.insertHabit(it) }
        }

        // Seed Grooming Routine items
        val existingGrooming = groomingDao.getAllRoutineItems().firstOrNull()
        if (existingGrooming.isNullOrEmpty()) {
            val morningItems = listOf(
                GroomingRoutineItemEntity(routineType = "morning", title = "Invigorating Cold/Warm Shower", iconName = "shower", orderIndex = 1, isDefault = true),
                GroomingRoutineItemEntity(routineType = "morning", title = "Face Wash (Gentle Cleanser)", iconName = "face", orderIndex = 2, isDefault = true),
                GroomingRoutineItemEntity(routineType = "morning", title = "Oral Care (Brush & Floss)", iconName = "clean_hands", orderIndex = 3, isDefault = true),
                GroomingRoutineItemEntity(routineType = "morning", title = "Hair Styling & Matte Clay", iconName = "brush", orderIndex = 4, isDefault = true),
                GroomingRoutineItemEntity(routineType = "morning", title = "Deodorant & Signature Fragrance", iconName = "spa", orderIndex = 5, isDefault = true),
                GroomingRoutineItemEntity(routineType = "morning", title = "Beard Oil / Clean Edge Shave", iconName = "content_cut", orderIndex = 6, isDefault = true),
                GroomingRoutineItemEntity(routineType = "morning", title = "Pressed Fitted Clothing", iconName = "checkroom", orderIndex = 7, isDefault = true)
            )
            val nightItems = listOf(
                GroomingRoutineItemEntity(routineType = "night", title = "Evening Warm Shower", iconName = "shower", orderIndex = 1, isDefault = true),
                GroomingRoutineItemEntity(routineType = "night", title = "Oral Care & Antiseptic Rinse", iconName = "clean_hands", orderIndex = 2, isDefault = true),
                GroomingRoutineItemEntity(routineType = "night", title = "Night Face Wash & Barrier Cream", iconName = "face", orderIndex = 3, isDefault = true),
                GroomingRoutineItemEntity(routineType = "night", title = "Hair Conditioning & Scalp Massage", iconName = "brush", orderIndex = 4, isDefault = true)
            )
            val weeklyItems = listOf(
                GroomingRoutineItemEntity(routineType = "weekly", title = "Precision Nail Grooming", iconName = "content_cut", orderIndex = 1, isDefault = true),
                GroomingRoutineItemEntity(routineType = "weekly", title = "Haircut / Barber Clean-up", iconName = "content_cut", orderIndex = 2, isDefault = true),
                GroomingRoutineItemEntity(routineType = "weekly", title = "Beard Shaping & Line Definition", iconName = "face", orderIndex = 3, isDefault = true),
                GroomingRoutineItemEntity(routineType = "weekly", title = "Laundry & Wardrobe Curation", iconName = "checkroom", orderIndex = 4, isDefault = true),
                GroomingRoutineItemEntity(routineType = "weekly", title = "Shoe Care & Clean Polishing", iconName = "hiking", orderIndex = 5, isDefault = true)
            )
            (morningItems + nightItems + weeklyItems).forEach { groomingDao.insertItem(it) }
        }

        // Seed Skincare Products
        val existingSkincare = skincareDao.getAllProducts().firstOrNull()
        if (existingSkincare.isNullOrEmpty()) {
            val skincareProducts = listOf(
                SkincareProductEntity(name = "Gentle Hydrating Cleanser", category = "Cleanser", brand = "CeraVe", routineTime = "BOTH", orderIndex = 1, frequency = "Daily"),
                SkincareProductEntity(name = "Vitamin C 15% Brightening Serum", category = "Serum", brand = "SkinCeuticals", routineTime = "AM", orderIndex = 2, frequency = "Daily"),
                SkincareProductEntity(name = "Ultra-Light Daily Moisturizer", category = "Moisturizer", brand = "La Roche-Posay", routineTime = "AM", orderIndex = 3, frequency = "Daily"),
                SkincareProductEntity(name = "Invisible Fluid Broad Spectrum SPF 50", category = "Sunscreen (SPF)", brand = "Anthelios", routineTime = "AM", orderIndex = 4, frequency = "Daily"),
                SkincareProductEntity(name = "Niacinamide 10% + Zinc Serum", category = "Serum", brand = "The Ordinary", routineTime = "PM", orderIndex = 2, frequency = "Daily"),
                SkincareProductEntity(name = "Retinol 0.2% Cellular Renewal", category = "Treatment", brand = "Paula's Choice", routineTime = "PM", orderIndex = 3, frequency = "2-3x / week"),
                SkincareProductEntity(name = "Ceramide Barrier Night Cream", category = "Moisturizer", brand = "CeraVe PM", routineTime = "PM", orderIndex = 4, frequency = "Daily")
            )
            skincareProducts.forEach { skincareDao.insertProduct(it) }
        }

        // Seed Sleep baseline
        val today = getTodayDateString()
        val existingSleep = sleepDao.getSleepLogForDateSync(today)
        if (existingSleep == null) {
            sleepDao.insertSleepLog(
                SleepLogEntity(
                    date = today,
                    bedtime = "22:45",
                    wakeTime = "06:45",
                    durationMinutes = 480,
                    qualityRating = 5,
                    nextDayEnergy = 5,
                    deepSleepMinutes = 110,
                    remSleepMinutes = 95,
                    restingHeartRate = 54,
                    hrvScore = 68f,
                    notes = "Deep, undisturbed recovery. Woke up alert and ready to attack the day.",
                    source = "manual"
                )
            )
        }

        // Seed Mindset baseline
        val existingMindset = mindsetDao.getMindsetLogForDateSync(today)
        if (existingMindset == null) {
            mindsetDao.insertMindsetLog(
                MindsetLogEntity(
                    date = today,
                    moodRating = 9,
                    energyRating = 9,
                    stressRating = 2,
                    confidenceRating = 9,
                    focusRating = 9,
                    moodLabel = "Apex Unstoppable"
                )
            )
        }

        // Seed Journal Prompts entry
        val existingJournal = mindsetDao.getJournalForDateSync(today)
        if (existingJournal == null) {
            mindsetDao.insertJournalEntry(
                JournalEntryEntity(
                    date = today,
                    accomplishedText = "Executed Push Day volume targets with PR on incline dumbbell press. Knocked out 90m deep work block.",
                    obstaclesText = "Mid-afternoon energy lull around 3 PM.",
                    distractionsText = "Slack notifications during second work block.",
                    learningsText = "Putting phone in another room increases focus output by 2x.",
                    improvementsText = "Hydrate 500ml cold water immediately upon 3 PM slump.",
                    freeformText = "Building momentum every day. No excuses.",
                    tags = "Focus, Hypertrophy, Discipline"
                )
            )
        }

        // Seed Goals & Milestones
        val existingGoals = goalDao.getAllGoals().firstOrNull()
        if (existingGoals.isNullOrEmpty()) {
            val goal1Id = goalDao.insertGoal(
                GoalEntity(
                    title = "Reach 85kg Lean Mass at 12% Body Fat",
                    description = "Execute structured progressive overload, 180g daily protein, and 5x weekly training.",
                    level = "yearly",
                    category = "Fitness",
                    deadline = "2026-12-31",
                    progress = 0.55f,
                    priority = "high"
                )
            )
            goalDao.insertMilestone(GoalMilestoneEntity(goalId = goal1Id, title = "Bench Press 110kg for 5 clean reps", isCompleted = false, orderIndex = 1))
            goalDao.insertMilestone(GoalMilestoneEntity(goalId = goal1Id, title = "Squat 160kg for 3 sets of 5", isCompleted = false, orderIndex = 2))
            goalDao.insertMilestone(GoalMilestoneEntity(goalId = goal1Id, title = "Maintain 30 consecutive days of 180g protein", isCompleted = true, orderIndex = 3))

            val goal2Id = goalDao.insertGoal(
                GoalEntity(
                    title = "Deploy PRIME Architecture & Reach 90+ Score",
                    description = "Maintain daily calibration, seamless time-blocking, and complete morning/evening routines.",
                    level = "monthly",
                    category = "Mindset",
                    deadline = "2026-09-30",
                    progress = 0.70f,
                    priority = "urgent"
                )
            )
            goalDao.insertMilestone(GoalMilestoneEntity(goalId = goal2Id, title = "7-day unbroken 85+ PRIME Score streak", isCompleted = true, orderIndex = 1))
            goalDao.insertMilestone(GoalMilestoneEntity(goalId = goal2Id, title = "Zero missed morning grooming routines", isCompleted = true, orderIndex = 2))
            goalDao.insertMilestone(GoalMilestoneEntity(goalId = goal2Id, title = "Log 10 hours of uninterrupted Deep Work", isCompleted = false, orderIndex = 3))

            val goal3Id = goalDao.insertGoal(
                GoalEntity(
                    title = "Complete 5 Focus Blocks (90m each)",
                    description = "High leverage execution sprint for project architecture.",
                    level = "weekly",
                    category = "Career",
                    deadline = "2026-09-07",
                    progress = 0.40f,
                    priority = "high"
                )
            )
            goalDao.insertMilestone(GoalMilestoneEntity(goalId = goal3Id, title = "Block 1: Database & Persistence Layer", isCompleted = true, orderIndex = 1))
            goalDao.insertMilestone(GoalMilestoneEntity(goalId = goal3Id, title = "Block 2: AI Orchestrator & Assistants", isCompleted = true, orderIndex = 2))
            goalDao.insertMilestone(GoalMilestoneEntity(goalId = goal3Id, title = "Block 3: UI Visual Analytics & Polish", isCompleted = false, orderIndex = 3))
        }

        // Seed Focus Sessions
        val existingFocus = focusDao.getSessionsForDateSync(today)
        if (existingFocus.isEmpty()) {
            focusDao.insertSession(
                FocusSessionEntity(
                    date = today,
                    taskName = "PRIME OS Core Engine Engineering",
                    mode = "deep_work",
                    durationMinutes = 50,
                    focusRating = 5,
                    distractionsCount = 0,
                    notes = "Total flow state. Zero interruptions.",
                    isCompleted = true
                )
            )
        }

        // Seed today's tasks
        val existingTasks = plannerDao.getTasksForDate(today).firstOrNull()
        if (existingTasks.isNullOrEmpty()) {
            val starterTasks = listOf(
                TaskEntity(title = "Complete Phase 2 Workout: Push Day Heavy", category = "Priority", date = today, priorityLevel = 1, xpReward = 100),
                TaskEntity(title = "Hit 180g protein and log all meals", category = "Priority", date = today, priorityLevel = 1, xpReward = 75),
                TaskEntity(title = "Execute 50m Deep Focus session on Architecture", category = "Priority", date = today, priorityLevel = 1, xpReward = 80),
                TaskEntity(title = "Evening Grooming, Skincare & Reflection", category = "Personal", date = today, priorityLevel = 2, xpReward = 40)
            )
            starterTasks.forEach { plannerDao.insertTask(it) }
        }

        // Seed today's time blocks
        val existingBlocks = plannerDao.getTimeBlocksForDate(today).firstOrNull()
        if (existingBlocks.isNullOrEmpty()) {
            val starterBlocks = listOf(
                TimeBlockEntity(title = "Morning Routine & Grooming", date = today, startTime = "07:00", endTime = "08:00", category = "Health", colorHex = "#10B981"),
                TimeBlockEntity(title = "Deep Focus: Phase 2 Architecture", date = today, startTime = "09:00", endTime = "12:00", category = "Work", colorHex = "#3B82F6"),
                TimeBlockEntity(title = "High Protein Lunch & Sunlight Walk", date = today, startTime = "12:30", endTime = "13:30", category = "Meals", colorHex = "#F59E0B"),
                TimeBlockEntity(title = "Execution Sprint & Review", date = today, startTime = "14:00", endTime = "16:30", category = "Work", colorHex = "#3B82F6"),
                TimeBlockEntity(title = "PRIME Workout: Push Hypertrophy", date = today, startTime = "17:30", endTime = "18:45", category = "Workout", colorHex = "#E5A93C"),
                TimeBlockEntity(title = "Evening Wind-Down, Skincare & Journal", date = today, startTime = "21:30", endTime = "22:30", category = "Sleep", colorHex = "#8B5CF6")
            )
            starterBlocks.forEach { plannerDao.insertTimeBlock(it) }
        }

        // Seed initial body measurement
        val existingMeasurements = bodyDao.getAllMeasurements().firstOrNull()
        if (existingMeasurements.isNullOrEmpty()) {
            bodyDao.insertMeasurement(
                BodyMeasurementEntity(
                    date = today,
                    weightKg = 82.5f,
                    heightCm = 182f,
                    waistCm = 81f,
                    chestCm = 106f,
                    armsCm = 39f,
                    thighsCm = 61f,
                    neckCm = 40f,
                    bodyFatEstimate = 13.8f,
                    bmi = 24.9f,
                    notes = "Baseline conditioning check. Calibrated and sharp.",
                    source = "manual"
                )
            )
        }

        // Seed Career Items
        val existingCareerSkills = careerDao.getAllCareerSkills().firstOrNull()
        if (existingCareerSkills.isNullOrEmpty()) {
            careerDao.insertCareerSkill(CareerSkillEntity(name = "Mobile Architecture & Kotlin", category = "Technical", proficiencyPercent = 85, targetProficiencyPercent = 95))
            careerDao.insertCareerSkill(CareerSkillEntity(name = "Applied AI & LLM Systems", category = "Technical", proficiencyPercent = 75, targetProficiencyPercent = 90))
            careerDao.insertCareerSkill(CareerSkillEntity(name = "Product Strategy & Unit Economics", category = "Leadership", proficiencyPercent = 65, targetProficiencyPercent = 85))
            careerDao.insertCareerGoal(CareerGoalEntity(title = "Ship PRIME OS Global Production Release", targetRole = "Principal Systems Architect", targetSalaryOrRevenue = "$250k/yr", timeline = "6 Months", progress = 0.85f))
            careerDao.insertCareerGoal(CareerGoalEntity(title = "Achieve $25k Monthly Recurring Revenue", targetRole = "Founder & Operator", targetSalaryOrRevenue = "$25k MRR", timeline = "1 Year", progress = 0.40f))
            careerDao.insertCertification(CertificationEntity(name = "Google Cloud Professional Architect", issuingOrganization = "Google Cloud", issueDate = "2025-11-15", status = "Achieved"))
            careerDao.insertCareerAchievement(CareerAchievementEntity(title = "Architected Zero-Latency Local-First App", date = "2026-04-01", impactMetric = "100k+ Active Users"))
            careerDao.insertCareerProject(CareerProjectEntity(title = "PRIME OS", role = "Lead Architect", description = "High-performance full-stack life operating system.", keyOutcomes = "11 synced telemetry modules", techStack = "Kotlin, Compose, Room, Coroutines"))
        }

        // Seed Learning Items
        val existingCourses = learningDao.getAllCourses().firstOrNull()
        if (existingCourses.isNullOrEmpty()) {
            learningDao.insertCourse(CourseEntity(title = "High-Performance Distributed Systems", platform = "MIT OpenCourseWare", instructor = "Prof. Robert Morris", totalModules = 12, completedModules = 8))
            learningDao.insertCourse(CourseEntity(title = "Modern Strength Physiology & Periodization", platform = "Science of Hypertrophy", instructor = "Dr. Brad Schoenfeld", totalModules = 8, completedModules = 6))
            learningDao.insertBook(BookEntity(title = "Principles: Life and Work", author = "Ray Dalio", totalPages = 592, currentPage = 340, keyTakeaways = "Radical open-mindedness and hyper-realistic decision loops."))
            learningDao.insertBook(BookEntity(title = "Deep Work", author = "Cal Newport", totalPages = 304, currentPage = 304, status = "Finished", keyTakeaways = "Clarity of purpose and eliminating shallow distractions."))
            learningDao.insertStudySession(StudySessionEntity(date = today, topic = "Distributed Cache Synchronization", durationMinutes = 45, keyInsights = "Analyzed optimistic locking and vector clocks."))
        }

        // Seed Finance Items
        val existingFinance = financeDao.getAllTransactions().firstOrNull()
        if (existingFinance.isNullOrEmpty()) {
            financeDao.insertTransaction(FinanceTransactionEntity(type = "Income", category = "Salary", amount = 8500.0, notes = "Monthly Technical Leadership Retainer", date = today))
            financeDao.insertTransaction(FinanceTransactionEntity(type = "Expense", category = "Food", amount = 180.0, notes = "High-Protein Weekly Groceries & Steaks", date = today))
            financeDao.insertTransaction(FinanceTransactionEntity(type = "Expense", category = "Tech & Tools", amount = 89.0, notes = "Cloud GPU & Developer Tools", date = today))
            financeDao.insertTransaction(FinanceTransactionEntity(type = "Savings", category = "Personal", amount = 3000.0, notes = "Allocated to 6-Month Liquid Emergency Runway", date = today))
            financeDao.insertFinancialGoal(FinancialGoalEntity(title = "6-Month Liquid Runway War Chest", goalType = "Emergency Fund", targetAmount = 50000.0, currentAmount = 38000.0, deadline = "2026-12-31"))
            financeDao.insertFinancialGoal(FinancialGoalEntity(title = "Direct Growth Asset Allocation", goalType = "Savings Goal", targetAmount = 100000.0, currentAmount = 45000.0, deadline = "2027-06-30"))
        }

        // Seed Gamification Badges
        val existingBadges = gamificationDao.getAllBadges().firstOrNull()
        if (existingBadges.isNullOrEmpty()) {
            gamificationDao.insertBadge(AchievementBadgeEntity(id = "badge_first_blood", title = "First Blood", category = "Fitness", description = "Completed first heavy training session.", iconName = "fitness_center", xpReward = 50, isUnlocked = true, unlockedDate = today))
            gamificationDao.insertBadge(AchievementBadgeEntity(id = "badge_iron_discipline", title = "Iron Discipline", category = "Habits", description = "Maintained a 7-day unbroken habit streak.", iconName = "local_fire_department", xpReward = 100, isUnlocked = true, unlockedDate = today))
            gamificationDao.insertBadge(AchievementBadgeEntity(id = "badge_deep_work", title = "Deep Flow Master", category = "Focus", description = "Logged 10+ cumulative hours of pure Deep Work.", iconName = "timer", xpReward = 75, isUnlocked = true, unlockedDate = today))
            gamificationDao.insertBadge(AchievementBadgeEntity(id = "badge_apex_calibration", title = "Apex Calibration", category = "Mastery", description = "Achieved a PRIME Score of 90+ across all 11 life vectors.", iconName = "military_tech", xpReward = 200, isUnlocked = false))
            gamificationDao.insertBadge(AchievementBadgeEntity(id = "badge_war_chest", title = "War Chest Builder", category = "Finance", description = "Maintained a 40%+ monthly savings rate.", iconName = "account_balance_wallet", xpReward = 100, isUnlocked = true, unlockedDate = today))
            gamificationDao.insertBadge(AchievementBadgeEntity(id = "badge_scholar", title = "Sovereign Scholar", category = "Learning", description = "Completed 5 books and 3 structured technical courses.", iconName = "school", xpReward = 150, isUnlocked = false))
            gamificationDao.insertXpTransaction(XpTransactionEntity(source = "Workout Completed", xpEarned = 50, date = today, notes = "Push Day Hypertrophy"))
            gamificationDao.insertXpTransaction(XpTransactionEntity(source = "Habits Executed", xpEarned = 60, date = today, notes = "All 6 core habits checked"))
            gamificationDao.insertXpTransaction(XpTransactionEntity(source = "Deep Work Session", xpEarned = 30, date = today, notes = "50m focus sprint"))
        }

        // Seed Vision Items
        val existingVision = visionAndReviewDao.getAllVisionItems().firstOrNull()
        if (existingVision.isNullOrEmpty()) {
            visionAndReviewDao.insertVisionItem(VisionItemEntity(title = "10% Body Fat & 85kg Athletic Muscle", content = "Achieve peak physical aesthetics and explosive cardiovascular engine.", targetHorizon = "1 Year", isCompleted = false))
            visionAndReviewDao.insertVisionItem(VisionItemEntity(title = "Zero Consumer Debt & $100k Liquid Reserves", content = "Unshakeable financial runway providing total career agency.", targetHorizon = "1 Year", isCompleted = true))
            visionAndReviewDao.insertVisionItem(VisionItemEntity(title = "Found & Scale Independent Profitable Venture", content = "High-margin cash-flowing software infrastructure.", targetHorizon = "3 Year", isCompleted = false))
            visionAndReviewDao.insertVisionItem(VisionItemEntity(title = "Total Time & Location Sovereignty", content = "Work exclusively on high-leverage problems with full autonomy.", targetHorizon = "5 Year", isCompleted = false))
        }

        // Seed Weekly Review
        val existingReviews = visionAndReviewDao.getAllWeeklyReviews().firstOrNull()
        if (existingReviews.isNullOrEmpty()) {
            visionAndReviewDao.insertWeeklyReview(
                WeeklyReviewRecordEntity(
                    weekLabel = "Week of $today",
                    overallScorePercent = 88,
                    strongestCategory = "Nutrition & Strength",
                    weakestCategory = "Sleep Regularity",
                    winsJson = "Maintained 100% daily macro protein target across 6 training days.|||Clocked 14.5 hours of deep focus work with minimal context-switching.|||Consistent evening wind-down routine resulting in 8.2h avg restorative sleep.",
                    weaknessesJson = "Late-night screen exposure on Thursday delayed sleep onset by 45 minutes.|||Hydration dipped below 2,500ml on non-training recovery days.|||Skipped mobility / stretching work following heavy lower body session.",
                    recommendationsJson = "Deploy a hard 10:00 PM digital curfew with grayscale mode enabled.|||Pre-fill 1L water canister immediately upon waking.|||Program a mandatory 12-minute hip & ankle decompression block post-workout."
                )
            )
        }

        recalculateDailyRollup(today)
    }
}
