package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.ai.AiActionSuggestion
import com.example.data.ai.AiMessageResponse
import com.example.data.ai.DailyCoachSummary
import com.example.data.ai.NutritionEstimateResult
import com.example.data.ai.PrimeAiInsight
import com.example.data.ai.WeeklyReviewSummary
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
import com.example.data.repository.PrimeRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ToastMessage(
    val id: Long = System.currentTimeMillis(),
    val message: String,
    val undoAction: (() -> Unit)? = null
)

class PrimeViewModel(
    val repository: PrimeRepository
) : ViewModel() {

    val todayDate: String = repository.getTodayDateString()

    // Profile & Level
    val userProfile: StateFlow<ProfileEntity?> = repository.userProfile.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    // Daily Rollup & PRIME Score
    val currentDailyEntry: StateFlow<DailyEntryEntity?> = repository.getDailyEntry(todayDate).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val currentDailyScore: StateFlow<DailyScoreRecordEntity?> = repository.getDailyScoreRecord(todayDate).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val recentDailyEntries: StateFlow<List<DailyEntryEntity>> = repository.getRecentDailyEntries().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val totalLoggedDays: StateFlow<Int> = repository.getTotalLoggedDays().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 1
    )

    // Workout State
    val workoutTemplates: StateFlow<List<WorkoutTemplateEntity>> = repository.workoutTemplates.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val exercises: StateFlow<List<ExerciseEntity>> = repository.exercises.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allWorkoutSessions: StateFlow<List<WorkoutSessionEntity>> = repository.allWorkoutSessions.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Active Workout
    private val _activeSessionId = MutableStateFlow<Long?>(null)
    val activeSessionId: StateFlow<Long?> = _activeSessionId.asStateFlow()

    private val _activeSessionSets = MutableStateFlow<List<WorkoutSetEntity>>(emptyList())
    val activeSessionSets: StateFlow<List<WorkoutSetEntity>> = _activeSessionSets.asStateFlow()

    private val _restTimerSeconds = MutableStateFlow(0)
    val restTimerSeconds: StateFlow<Int> = _restTimerSeconds.asStateFlow()
    private var restTimerJob: Job? = null

    // Nutrition State
    val allFoods: StateFlow<List<FoodItemEntity>> = repository.allFoods.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val todayNutritionLogs: StateFlow<List<NutritionLogEntity>> = repository.getTodayNutritionLogs(todayDate).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val todayHydrationLogs: StateFlow<List<HydrationLogEntity>> = repository.getTodayHydrationLogs(todayDate).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Habits State
    val activeHabits: StateFlow<List<HabitEntity>> = repository.allActiveHabits.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val todayHabitLogs: StateFlow<List<HabitLogEntity>> = repository.getTodayHabitLogs(todayDate).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Planner State
    val todayTasks: StateFlow<List<TaskEntity>> = repository.getTasksForDate(todayDate).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val todayTimeBlocks: StateFlow<List<TimeBlockEntity>> = repository.getTimeBlocksForDate(todayDate).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Body State
    val allBodyMeasurements: StateFlow<List<BodyMeasurementEntity>> = repository.allBodyMeasurements.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val latestBodyMeasurement: StateFlow<BodyMeasurementEntity?> = repository.latestBodyMeasurement.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val allBodyPhotos: StateFlow<List<BodyPhotoEntity>> = repository.allBodyPhotos.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Phase 2: Grooming State
    val allGroomingItems: StateFlow<List<GroomingRoutineItemEntity>> = repository.allGroomingItems.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val todayGroomingLogs: StateFlow<List<GroomingLogEntity>> = repository.getGroomingLogsForDate(todayDate).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Phase 2: Skincare State
    val allSkincareProducts: StateFlow<List<SkincareProductEntity>> = repository.allSkincareProducts.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val todaySkincareLogs: StateFlow<List<SkincareLogEntity>> = repository.getSkincareLogsForDate(todayDate).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Phase 2: Sleep State
    val todaySleepLog: StateFlow<SleepLogEntity?> = repository.getSleepLogForDate(todayDate).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val recentSleepLogs: StateFlow<List<SleepLogEntity>> = repository.recentSleepLogs.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Phase 2: Mindset & Journal State
    val todayMindsetLog: StateFlow<MindsetLogEntity?> = repository.getMindsetLogForDate(todayDate).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val recentMindsetLogs: StateFlow<List<MindsetLogEntity>> = repository.recentMindsetLogs.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val todayJournalEntry: StateFlow<JournalEntryEntity?> = repository.getJournalForDate(todayDate).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val recentJournalEntries: StateFlow<List<JournalEntryEntity>> = repository.recentJournalEntries.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Phase 2: Focus State & Active Timer
    val todayFocusSessions: StateFlow<List<FocusSessionEntity>> = repository.getFocusSessionsForDate(todayDate).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val recentFocusSessions: StateFlow<List<FocusSessionEntity>> = repository.recentFocusSessions.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Active Focus Timer
    private val _isFocusTimerRunning = MutableStateFlow(false)
    val isFocusTimerRunning: StateFlow<Boolean> = _isFocusTimerRunning.asStateFlow()

    private val _focusRemainingSeconds = MutableStateFlow(25 * 60)
    val focusRemainingSeconds: StateFlow<Int> = _focusRemainingSeconds.asStateFlow()

    private val _focusTotalDurationMinutes = MutableStateFlow(25)
    val focusTotalDurationMinutes: StateFlow<Int> = _focusTotalDurationMinutes.asStateFlow()

    private val _focusMode = MutableStateFlow("pomodoro") // "pomodoro", "deep_work", "custom"
    val focusMode: StateFlow<String> = _focusMode.asStateFlow()

    private val _focusTaskName = MutableStateFlow("Deep Focus Block")
    val focusTaskName: StateFlow<String> = _focusTaskName.asStateFlow()

    private val _focusDistractionsCount = MutableStateFlow(0)
    val focusDistractionsCount: StateFlow<Int> = _focusDistractionsCount.asStateFlow()

    private var focusTimerJob: Job? = null

    // Phase 2: Goals State
    val allGoals: StateFlow<List<GoalEntity>> = repository.allGoals.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Phase 2: PRIME AI State
    val allAiMessages: StateFlow<List<AiChatMessageEntity>> = repository.allAiMessages.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val todayAiUsage: StateFlow<AiUsageStatsEntity?> = repository.getAiUsageForDate(todayDate).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    private val _activeAssistantType = MutableStateFlow("general")
    val activeAssistantType: StateFlow<String> = _activeAssistantType.asStateFlow()

    private val _isAiGenerating = MutableStateFlow(false)
    val isAiGenerating: StateFlow<Boolean> = _isAiGenerating.asStateFlow()

    private val _dailyCoachSummary = MutableStateFlow<DailyCoachSummary?>(null)
    val dailyCoachSummary: StateFlow<DailyCoachSummary?> = _dailyCoachSummary.asStateFlow()

    private val _weeklyReviewSummary = MutableStateFlow<WeeklyReviewSummary?>(null)
    val weeklyReviewSummary: StateFlow<WeeklyReviewSummary?> = _weeklyReviewSummary.asStateFlow()

    private val _foodEstimateState = MutableStateFlow<NutritionEstimateResult?>(null)
    val foodEstimateState: StateFlow<NutritionEstimateResult?> = _foodEstimateState.asStateFlow()

    // Toast Events (Instant undo support)
    private val _toastEvent = MutableSharedFlow<ToastMessage>()
    val toastEvent: SharedFlow<ToastMessage> = _toastEvent.asSharedFlow()

    fun showToast(message: String, undoAction: (() -> Unit)? = null) {
        viewModelScope.launch {
            _toastEvent.emit(ToastMessage(message = message, undoAction = undoAction))
        }
    }

    // Quick Actions & Logging
    fun logQuickWater(amountMl: Int = 250) {
        viewModelScope.launch {
            val id = repository.logHydration(amountMl, todayDate)
            showToast("Logged +$amountMl ml water") {
                viewModelScope.launch {
                    // Undo water
                    val logs = repository.getTodayHydrationLogs(todayDate)
                }
            }
        }
    }

    fun toggleHabit(habitId: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.toggleHabitCompletion(habitId, isCompleted, todayDate)
            showToast(if (isCompleted) "Habit logged (+25 XP)" else "Habit reverted") {
                viewModelScope.launch {
                    repository.toggleHabitCompletion(habitId, !isCompleted, todayDate)
                }
            }
        }
    }

    fun addHabit(name: String, iconName: String, category: String, frequency: String, reminder: String) {
        viewModelScope.launch {
            repository.createHabit(name, iconName, category, frequency, reminder)
            showToast("Added habit: $name")
        }
    }

    fun toggleTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.toggleTaskCompletion(task)
            showToast(if (!task.isCompleted) "Task completed! (+${task.xpReward} XP)" else "Task reverted") {
                viewModelScope.launch {
                    repository.toggleTaskCompletion(task)
                }
            }
        }
    }

    fun addTask(title: String, category: String, priorityLevel: Int, xp: Int = 50) {
        viewModelScope.launch {
            repository.addTask(title, category, priorityLevel, xp, todayDate)
            showToast("Added task: $title")
        }
    }

    fun addTimeBlock(title: String, startTime: String, endTime: String, category: String, colorHex: String) {
        viewModelScope.launch {
            repository.addTimeBlock(title, startTime, endTime, category, colorHex, todayDate)
            showToast("Scheduled block: $title")
        }
    }

    fun logFood(mealType: String, foodName: String, portions: Float, calories: Int, protein: Float, carbs: Float, fat: Float, source: String = "manual") {
        viewModelScope.launch {
            repository.logFoodItem(
                mealType = mealType,
                foodName = foodName,
                portions = portions,
                calories = (calories * portions).toInt(),
                proteinGrams = protein * portions,
                carbsGrams = carbs * portions,
                fatGrams = fat * portions,
                date = todayDate,
                source = source
            )
            showToast("Logged $foodName ($mealType)")
        }
    }

    fun addCustomFood(name: String, servingSize: String, calories: Int, protein: Float, carbs: Float, fat: Float) {
        viewModelScope.launch {
            repository.addCustomFood(name, servingSize, calories, protein, carbs, fat)
            showToast("Added custom food: $name")
        }
    }

    fun logBodyMeasurement(weightKg: Float, heightCm: Float, waistCm: Float?, chestCm: Float?, armsCm: Float?, thighsCm: Float?, neckCm: Float?, notes: String) {
        viewModelScope.launch {
            repository.logBodyMeasurement(
                weightKg = weightKg,
                heightCm = heightCm,
                waistCm = waistCm,
                chestCm = chestCm,
                armsCm = armsCm,
                thighsCm = thighsCm,
                neckCm = neckCm,
                date = todayDate,
                notes = notes
            )
            showToast("Logged body measurement ($weightKg kg)")
        }
    }

    // Workout Session Actions
    fun startWorkout(templateName: String, templateId: Long?) {
        viewModelScope.launch {
            val sessionId = repository.createWorkoutSession(templateName, templateId, todayDate)
            _activeSessionId.value = sessionId
            repository.getWorkoutSets(sessionId).collect { sets ->
                _activeSessionSets.value = sets
            }
        }
    }

    fun addSetToActiveSession(exerciseName: String, setNumber: Int, weightKg: Float, reps: Int, rpe: Int? = null) {
        val sessionId = _activeSessionId.value ?: return
        viewModelScope.launch {
            repository.logWorkoutSet(
                sessionId = sessionId,
                exerciseName = exerciseName,
                setNumber = setNumber,
                weightKg = weightKg,
                reps = reps,
                rpe = rpe
            )
            startRestTimer(90)
            showToast("Set $setNumber logged! Rest timer started.")
        }
    }

    fun completeActiveWorkout(durationMinutes: Int, rpe: Int, notes: String) {
        val sessionId = _activeSessionId.value ?: return
        viewModelScope.launch {
            repository.completeWorkoutSession(sessionId, durationMinutes, rpe, notes, todayDate)
            _activeSessionId.value = null
            _activeSessionSets.value = emptyList()
            cancelRestTimer()
            showToast("Workout Completed! (+150 XP) PRIME score updated.")
        }
    }

    fun cancelActiveWorkout() {
        val sessionId = _activeSessionId.value
        if (sessionId != null) {
            viewModelScope.launch {
                repository.cancelWorkoutSession(sessionId)
            }
        }
        _activeSessionId.value = null
        _activeSessionSets.value = emptyList()
        cancelRestTimer()
    }

    fun startRestTimer(seconds: Int) {
        restTimerJob?.cancel()
        _restTimerSeconds.value = seconds
        restTimerJob = viewModelScope.launch {
            while (_restTimerSeconds.value > 0) {
                delay(1000)
                _restTimerSeconds.value -= 1
            }
        }
    }

    fun cancelRestTimer() {
        restTimerJob?.cancel()
        _restTimerSeconds.value = 0
    }

    // Grooming Actions (Phase 2 - 11)
    fun toggleGroomingItem(itemId: Long, routineType: String, isDone: Boolean) {
        viewModelScope.launch {
            repository.toggleGroomingItem(itemId, routineType, isDone, todayDate)
            showToast(if (isDone) "Grooming item checked (+15 XP)" else "Grooming item unchecked")
        }
    }

    fun addCustomGroomingItem(title: String, routineType: String, iconName: String = "face") {
        viewModelScope.launch {
            repository.addGroomingItem(title, routineType, iconName)
            showToast("Added $title to $routineType routine")
        }
    }

    // Skincare Actions (Phase 2 - 12)
    fun toggleSkincareProduct(productId: Long, routineTime: String, isDone: Boolean) {
        viewModelScope.launch {
            repository.toggleSkincareProduct(productId, routineTime, isDone, todayDate)
            showToast(if (isDone) "Skincare step completed (+15 XP)" else "Step reverted")
        }
    }

    fun addSkincareProduct(name: String, category: String, brand: String, routineTime: String, frequency: String, notes: String) {
        viewModelScope.launch {
            repository.addSkincareProduct(name, category, brand, routineTime, frequency, notes = notes)
            showToast("Added $name to skincare routine")
        }
    }

    // Sleep Actions (Phase 2 - 13)
    fun logSleep(bedtime: String, wakeTime: String, durationMinutes: Int, quality: Int, energy: Int, notes: String) {
        viewModelScope.launch {
            repository.logSleep(bedtime, wakeTime, durationMinutes, quality, energy, notes, date = todayDate)
            showToast("Sleep logged (${durationMinutes / 60}h ${durationMinutes % 60}m) | Score updated")
        }
    }

    // Mindset & Journal Actions (Phase 2 - 14)
    fun logMindset(mood: Int, energy: Int, stress: Int, confidence: Int, focus: Int, moodLabel: String) {
        viewModelScope.launch {
            repository.logMindset(mood, energy, stress, confidence, focus, moodLabel, todayDate)
            showToast("Mindset calibrated: $moodLabel (+30 XP)")
        }
    }

    fun saveJournal(accomplished: String, obstacles: String, distractions: String, learnings: String, improvements: String, freeform: String) {
        viewModelScope.launch {
            repository.saveJournalEntry(accomplished, obstacles, distractions, learnings, improvements, freeform, date = todayDate)
            showToast("Journal entry saved (+50 XP) | Reflection documented")
        }
    }

    // Focus Session Engine (Phase 2 - 15)
    fun startFocusTimer(mode: String = "pomodoro", durationMinutes: Int = 25, taskName: String = "Deep Focus Block") {
        focusTimerJob?.cancel()
        _focusMode.value = mode
        _focusTotalDurationMinutes.value = durationMinutes
        _focusRemainingSeconds.value = durationMinutes * 60
        _focusTaskName.value = taskName
        _focusDistractionsCount.value = 0
        _isFocusTimerRunning.value = true

        focusTimerJob = viewModelScope.launch {
            while (_focusRemainingSeconds.value > 0) {
                delay(1000)
                _focusRemainingSeconds.value -= 1
            }
            _isFocusTimerRunning.value = false
            showToast("Focus block completed! Log your rating.")
        }
    }

    fun pauseFocusTimer() {
        focusTimerJob?.cancel()
        _isFocusTimerRunning.value = false
    }

    fun resumeFocusTimer() {
        if (_focusRemainingSeconds.value <= 0) return
        _isFocusTimerRunning.value = true
        focusTimerJob = viewModelScope.launch {
            while (_focusRemainingSeconds.value > 0) {
                delay(1000)
                _focusRemainingSeconds.value -= 1
            }
            _isFocusTimerRunning.value = false
            showToast("Focus session complete!")
        }
    }

    fun logDistractionDuringFocus(note: String) {
        _focusDistractionsCount.value += 1
        showToast("Distraction logged (${_focusDistractionsCount.value})")
    }

    fun completeFocusSession(taskName: String, rating: Int, notes: String) {
        val durationMins = _focusTotalDurationMinutes.value
        val mode = _focusMode.value
        val distractions = _focusDistractionsCount.value
        focusTimerJob?.cancel()
        _isFocusTimerRunning.value = false

        viewModelScope.launch {
            repository.completeFocusSession(
                taskName = taskName,
                mode = mode,
                durationMinutes = durationMins,
                focusRating = rating,
                distractionsCount = distractions,
                notes = notes,
                date = todayDate
            )
            showToast("Focus block logged! (+${durationMins * 2} XP)")
        }
    }

    fun cancelFocusTimer() {
        focusTimerJob?.cancel()
        _isFocusTimerRunning.value = false
        _focusRemainingSeconds.value = 25 * 60
    }

    // Goals Management (Phase 2 - 16)
    fun addGoal(title: String, description: String, level: String, category: String, deadline: String, priority: String) {
        viewModelScope.launch {
            repository.addGoal(title, description, level, category, deadline, priority)
            showToast("Created $level goal: $title")
        }
    }

    fun addGoalMilestone(goalId: Long, title: String) {
        viewModelScope.launch {
            repository.addGoalMilestone(goalId, title)
            showToast("Milestone added")
        }
    }

    fun toggleGoalMilestone(milestone: GoalMilestoneEntity, goalId: Long) {
        viewModelScope.launch {
            repository.toggleGoalMilestone(milestone, goalId)
        }
    }

    fun updateGoalProgress(goal: GoalEntity, progress: Float) {
        viewModelScope.launch {
            repository.updateGoalProgress(goal, progress)
        }
    }

    fun convertGoalToPlannerTask(goal: GoalEntity) {
        viewModelScope.launch {
            repository.convertGoalToPlannerTask(goal.title, goal.category, 60, todayDate)
            showToast("Goal converted to Planner task!")
        }
    }

    fun deleteGoal(goal: GoalEntity) {
        viewModelScope.launch {
            repository.deleteGoal(goal)
            showToast("Goal removed")
        }
    }

    // PRIME AI Assistants (Phase 2 - 17)
    fun setAssistantType(type: String) {
        _activeAssistantType.value = type
    }

    fun sendAiChatMessage(message: String) {
        if (message.isBlank()) return
        val assistant = _activeAssistantType.value
        viewModelScope.launch {
            // Save user message
            repository.saveAiChatMessage(sender = "user", content = message, assistantType = assistant)
            _isAiGenerating.value = true

            // Generate reply
            val history = allAiMessages.value.filter { it.assistantType == assistant }.map { Pair(it.sender, it.content) }
            val response = repository.sendAssistantChatMessage(assistant, message, history, todayDate)

            // Save AI message
            repository.saveAiChatMessage(
                sender = "assistant",
                content = response.replyText,
                assistantType = assistant,
                actionTitle = response.suggestedAction?.title,
                actionPayload = response.suggestedAction?.payload,
                actionType = response.suggestedAction?.actionType
            )
            _isAiGenerating.value = false
        }
    }

    fun applyAiSuggestedAction(action: AiActionSuggestion) {
        viewModelScope.launch {
            when (action.actionType) {
                "ADD_TASK" -> addTask(action.payload, "AI Suggested", 1, 60)
                "SCHEDULE_BLOCK" -> addTimeBlock(action.payload, "16:00", "17:00", "Work", "#3B82F6")
                "LOG_WATER" -> logQuickWater(action.payload.toIntOrNull() ?: 500)
                "START_FOCUS" -> startFocusTimer("pomodoro", action.payload.toIntOrNull() ?: 25, "Deep Focus Block")
            }
            showToast("Action applied: ${action.title}")
        }
    }

    fun clearAiChat(assistantType: String? = null) {
        viewModelScope.launch {
            repository.clearAiChat(assistantType)
            showToast("Chat history cleared")
        }
    }

    fun refreshDailyCoach() {
        viewModelScope.launch {
            _isAiGenerating.value = true
            val coach = repository.generateDailyCoachSummary(todayDate)
            _dailyCoachSummary.value = coach
            _isAiGenerating.value = false
        }
    }

    fun refreshWeeklyReview() {
        viewModelScope.launch {
            _isAiGenerating.value = true
            val review = repository.generateWeeklyReviewSummary(todayDate)
            _weeklyReviewSummary.value = review
            _isAiGenerating.value = false
        }
    }

    fun estimateFoodNutrition(prompt: String) {
        if (prompt.isBlank()) return
        viewModelScope.launch {
            _isAiGenerating.value = true
            val result = repository.estimateFoodNutrition(prompt)
            _foodEstimateState.value = result
            _isAiGenerating.value = false
        }
    }

    // ----------------------------------------------------
    // PHASE 3: CAREER
    // ----------------------------------------------------
    val allCareerGoals: StateFlow<List<CareerGoalEntity>> = repository.allCareerGoals.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allCareerSkills: StateFlow<List<CareerSkillEntity>> = repository.allCareerSkills.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allCertifications: StateFlow<List<CertificationEntity>> = repository.allCertifications.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allCareerProjects: StateFlow<List<CareerProjectEntity>> = repository.allCareerProjects.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allCareerAchievements: StateFlow<List<CareerAchievementEntity>> = repository.allCareerAchievements.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allResumeItems: StateFlow<List<ResumeItemEntity>> = repository.allResumeItems.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun addCareerGoal(title: String, targetRole: String, targetSalaryOrRevenue: String, timeline: String) {
        viewModelScope.launch {
            repository.addCareerGoal(title, targetRole, targetSalaryOrRevenue, timeline)
            showToast("Career Goal established (+50 XP)")
        }
    }

    fun updateCareerGoalProgress(goal: CareerGoalEntity, newProgress: Float) {
        viewModelScope.launch {
            repository.updateCareerGoalProgress(goal, newProgress)
            showToast("Goal progress updated: ${(newProgress * 100).toInt()}%")
        }
    }

    fun addCareerSkill(name: String, category: String, proficiencyPercent: Int, targetProficiencyPercent: Int) {
        viewModelScope.launch {
            repository.addCareerSkill(name, category, proficiencyPercent, targetProficiencyPercent)
            showToast("Skill added to Mastery Matrix (+30 XP)")
        }
    }

    fun updateSkillLevel(skill: CareerSkillEntity, newProficiencyPercent: Int) {
        viewModelScope.launch {
            repository.updateSkillLevel(skill, newProficiencyPercent)
            showToast("Skill leveled up to $newProficiencyPercent% (+20 XP)")
        }
    }

    fun deleteCareerSkill(skill: CareerSkillEntity) {
        viewModelScope.launch {
            repository.deleteCareerSkill(skill)
            showToast("Skill removed")
        }
    }

    fun addCertification(name: String, issuingOrganization: String, issueDate: String, status: String) {
        viewModelScope.launch {
            repository.addCertification(name, issuingOrganization, issueDate, status)
            showToast("Certification recorded (+75 XP)")
        }
    }

    fun addCareerProject(title: String, role: String, description: String, keyOutcomes: String, techStack: String) {
        viewModelScope.launch {
            repository.addCareerProject(title, role, description, keyOutcomes, techStack)
            showToast("Career Project documented (+60 XP)")
        }
    }

    fun addCareerAchievement(title: String, date: String, impactMetric: String) {
        viewModelScope.launch {
            repository.addCareerAchievement(title, date, impactMetric)
            showToast("Achievement documented (+50 XP)")
        }
    }

    // ----------------------------------------------------
    // PHASE 3: LEARNING
    // ----------------------------------------------------
    val allCourses: StateFlow<List<CourseEntity>> = repository.allCourses.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allBooks: StateFlow<List<BookEntity>> = repository.allBooks.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allStudySessions: StateFlow<List<StudySessionEntity>> = repository.allStudySessions.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun addLearningCourse(title: String, platform: String, instructor: String, totalModules: Int) {
        viewModelScope.launch {
            repository.addLearningCourse(title, platform, instructor, totalModules)
            showToast("Course curriculum enrolled (+40 XP)")
        }
    }

    fun updateCourseProgress(course: CourseEntity, completedCount: Int) {
        viewModelScope.launch {
            repository.updateCourseProgress(course, completedCount)
            showToast("Course progress updated: $completedCount/${course.totalModules}")
        }
    }

    fun addLearningBook(title: String, author: String, format: String, totalPages: Int) {
        viewModelScope.launch {
            repository.addLearningBook(title, author, format, totalPages)
            showToast("Book added to Reading Matrix (+30 XP)")
        }
    }

    fun updateBookProgress(book: BookEntity, newPage: Int) {
        viewModelScope.launch {
            repository.updateBookProgress(book, newPage)
            showToast("Reading progress updated: $newPage/${book.totalPages} pages")
        }
    }

    fun logStudySession(topic: String, category: String, durationMinutes: Int, keyInsights: String) {
        viewModelScope.launch {
            repository.logStudySession(topic, category, durationMinutes, keyInsights)
            showToast("Study sprint logged (+30 XP)")
        }
    }

    // ----------------------------------------------------
    // PHASE 3: FINANCE
    // ----------------------------------------------------
    val allFinanceTransactions: StateFlow<List<FinanceTransactionEntity>> = repository.allFinanceTransactions.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allFinancialGoals: StateFlow<List<FinancialGoalEntity>> = repository.allFinancialGoals.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun addFinanceTransaction(type: String, category: String, amount: Double, notes: String, isRecurring: Boolean) {
        viewModelScope.launch {
            repository.addFinanceTransaction(type, category, amount, notes, isRecurring)
            showToast("Transaction recorded (+20 XP)")
        }
    }

    fun deleteFinanceTransaction(transaction: FinanceTransactionEntity) {
        viewModelScope.launch {
            repository.deleteFinanceTransaction(transaction)
            showToast("Transaction removed")
        }
    }

    fun addFinancialGoal(title: String, goalType: String, targetAmount: Double, currentAmount: Double, deadline: String) {
        viewModelScope.launch {
            repository.addFinancialGoal(title, goalType, targetAmount, currentAmount, deadline)
            showToast("Capital Goal established (+50 XP)")
        }
    }

    fun updateFinancialGoalProgress(goal: FinancialGoalEntity, addedAmount: Double) {
        viewModelScope.launch {
            repository.updateFinancialGoalProgress(goal, addedAmount)
            showToast("Capital allocated to ${goal.title} (+30 XP)")
        }
    }

    // ----------------------------------------------------
    // PHASE 3: GAMIFICATION & STREAKS
    // ----------------------------------------------------
    val allAchievementBadges: StateFlow<List<AchievementBadgeEntity>> = repository.allAchievementBadges.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allXpTransactions: StateFlow<List<XpTransactionEntity>> = repository.allXpTransactions.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun activateStreakGraceShield() {
        viewModelScope.launch {
            repository.activateStreakGraceShield()
            showToast("Streak Grace Shield deployed. Zero guilt, full recovery.")
        }
    }

    // ----------------------------------------------------
    // PHASE 3: VISION & WEEKLY REVIEWS
    // ----------------------------------------------------
    val allVisionItems: StateFlow<List<VisionItemEntity>> = repository.allVisionItems.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allWeeklyReviews: StateFlow<List<WeeklyReviewRecordEntity>> = repository.allWeeklyReviews.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun addVisionMilestone(title: String, content: String, horizon: String) {
        viewModelScope.launch {
            repository.addVisionMilestone(title, content, horizon)
            showToast("Vision milestone anchored (+50 XP)")
        }
    }

    fun toggleVisionMilestone(item: VisionItemEntity) {
        viewModelScope.launch {
            repository.toggleVisionMilestone(item)
            val state = if (!item.isCompleted) "completed (+50 XP)" else "re-opened"
            showToast("Milestone $state")
        }
    }

    fun updateIdentityStatements(whoIAm: String, whoIWantToBecome: String) {
        viewModelScope.launch {
            repository.updateIdentityStatements(whoIAm, whoIWantToBecome)
            showToast("Identity standards updated")
        }
    }

    fun generateAndSaveWeeklyReview() {
        viewModelScope.launch {
            _isAiGenerating.value = true
            repository.generateAndSaveWeeklyReview()
            _isAiGenerating.value = false
            showToast("Weekly Performance Review synthesized (+100 XP)")
        }
    }

    // ----------------------------------------------------
    // PHASE 3: ONBOARDING
    // ----------------------------------------------------
    fun completeOnboarding(
        name: String,
        goal: String,
        calories: Int,
        protein: Float,
        sleepHours: Float,
        focusMinutes: Int,
        currency: String
    ) {
        viewModelScope.launch {
            repository.completeOnboarding(
                name = name,
                goal = goal,
                calories = calories,
                protein = protein,
                sleepHours = sleepHours,
                focusMinutes = focusMinutes,
                currency = currency
            )
            showToast("PRIME OS calibrated! (+200 XP)")
        }
    }

    fun clearFoodEstimate() {
        _foodEstimateState.value = null
    }

    // Profile & Settings Updates
    fun updateProfile(profile: ProfileEntity) {
        viewModelScope.launch {
            repository.saveProfile(profile)
            repository.recalculateDailyRollup(todayDate)
            showToast("Profile & targets updated successfully")
        }
    }

    fun exportUserDataJson(): String {
        val profile = userProfile.value
        val entry = currentDailyEntry.value
        val score = currentDailyScore.value
        return """
            {
              "exportDate": "$todayDate",
              "user": {
                "name": "${profile?.name}",
                "level": ${profile?.level},
                "xp": ${profile?.xp},
                "streak": ${profile?.currentStreak}
              },
              "todaySummary": {
                "primeScore": ${score?.totalScore ?: entry?.primeScore},
                "calories": ${entry?.caloriesLogged},
                "protein": ${entry?.proteinGrams},
                "waterMl": ${entry?.waterTotalMl},
                "workoutDone": ${entry?.workoutDone},
                "sleepHours": ${entry?.sleepHours},
                "focusMinutes": ${entry?.focusMinutes}
              }
            }
        """.trimIndent()
    }

    fun wipeData() {
        viewModelScope.launch {
            repository.wipeAllUserData()
            showToast("All user data wiped and reset to clean baseline.")
        }
    }
}

class PrimeViewModelFactory(
    private val repository: PrimeRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PrimeViewModel::class.java)) {
            return PrimeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
