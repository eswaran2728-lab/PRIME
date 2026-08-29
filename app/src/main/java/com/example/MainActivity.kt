package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.PrimeDatabase
import com.example.data.repository.PrimeRepository
import com.example.ui.components.PrimeBottomBar
import com.example.ui.components.PrimeToastSnackbar
import com.example.ui.components.PrimeTopBar
import com.example.ui.components.QuickActionDialog
import com.example.ui.components.QuickActionType
import com.example.ui.components.ScoreBreakdownDialog
import com.example.ui.navigation.PrimeScreen
import com.example.ui.screens.ai.PrimeAiScreen
import com.example.ui.screens.analytics.AnalyticsScreen
import com.example.ui.screens.body.BodyScreen
import com.example.ui.screens.career.CareerScreen
import com.example.ui.screens.dashboard.DashboardScreen
import com.example.ui.screens.finance.FinanceScreen
import com.example.ui.screens.focus.FocusScreen
import com.example.ui.screens.gamification.GamificationScreen
import com.example.ui.screens.grooming.GroomingScreen
import com.example.ui.screens.habits.HabitsScreen
import com.example.ui.screens.learning.LearningScreen
import com.example.ui.screens.mindset.MindsetScreen
import com.example.ui.screens.nutrition.NutritionScreen
import com.example.ui.screens.onboarding.OnboardingScreen
import com.example.ui.screens.planner.PlannerScreen
import com.example.ui.screens.review.WeeklyReviewScreen
import com.example.ui.screens.search.GlobalSearchScreen
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.screens.sleep.SleepScreen
import com.example.ui.screens.today.TodayScreen
import com.example.ui.screens.vision.VisionScreen
import com.example.ui.screens.workout.WorkoutScreen
import com.example.ui.theme.PrimeTheme
import com.example.ui.viewmodel.PrimeViewModel
import com.example.ui.viewmodel.PrimeViewModelFactory
import com.example.ui.viewmodel.ToastMessage
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val database = remember { PrimeDatabase.getInstance(context) }
            val repository = remember { PrimeRepository(database) }
            val viewModel: PrimeViewModel = viewModel(factory = PrimeViewModelFactory(repository))

            PrimeTheme {
                PrimeApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun PrimeApp(viewModel: PrimeViewModel) {
    var currentScreen by remember { mutableStateOf(PrimeScreen.Dashboard) }
    var showScoreDialog by remember { mutableStateOf(false) }
    var showQuickActionDialog by remember { mutableStateOf(false) }
    var activeToast by remember { mutableStateOf<ToastMessage?>(null) }

    // Collect Core StateFlows from ViewModel
    val profile by viewModel.userProfile.collectAsState()
    val dailyEntry by viewModel.currentDailyEntry.collectAsState()
    val dailyScore by viewModel.currentDailyScore.collectAsState()
    val totalLoggedDays by viewModel.totalLoggedDays.collectAsState()
    val recentDailyEntries by viewModel.recentDailyEntries.collectAsState()

    // Workout & Nutrition State
    val workoutTemplates by viewModel.workoutTemplates.collectAsState()
    val exercises by viewModel.exercises.collectAsState()
    val workoutSessions by viewModel.allWorkoutSessions.collectAsState()
    val activeSessionId by viewModel.activeSessionId.collectAsState()
    val activeSessionSets by viewModel.activeSessionSets.collectAsState()
    val restTimerSeconds by viewModel.restTimerSeconds.collectAsState()

    val availableFoods by viewModel.allFoods.collectAsState()
    val nutritionLogs by viewModel.todayNutritionLogs.collectAsState()
    val hydrationLogs by viewModel.todayHydrationLogs.collectAsState()

    // Habits & Planner State
    val habits by viewModel.activeHabits.collectAsState()
    val habitLogs by viewModel.todayHabitLogs.collectAsState()

    val tasks by viewModel.todayTasks.collectAsState()
    val timeBlocks by viewModel.todayTimeBlocks.collectAsState()
    val strategicGoals by viewModel.allGoals.collectAsState()

    // Body & Measurements
    val latestMeasurement by viewModel.latestBodyMeasurement.collectAsState()
    val allMeasurements by viewModel.allBodyMeasurements.collectAsState()
    val photos by viewModel.allBodyPhotos.collectAsState()

    // Phase 2: Grooming & Skincare State
    val groomingItems by viewModel.allGroomingItems.collectAsState()
    val todayGroomingLogs by viewModel.todayGroomingLogs.collectAsState()
    val skincareProducts by viewModel.allSkincareProducts.collectAsState()
    val todaySkincareLogs by viewModel.todaySkincareLogs.collectAsState()

    // Phase 2: Sleep State
    val todaySleepLog by viewModel.todaySleepLog.collectAsState()
    val recentSleepLogs by viewModel.recentSleepLogs.collectAsState()

    // Phase 2: Mindset & Journal State
    val todayMindsetLog by viewModel.todayMindsetLog.collectAsState()
    val recentMindsetLogs by viewModel.recentMindsetLogs.collectAsState()
    val todayJournalEntry by viewModel.todayJournalEntry.collectAsState()
    val recentJournalEntries by viewModel.recentJournalEntries.collectAsState()

    // Phase 2: Focus Engine State
    val isFocusTimerRunning by viewModel.isFocusTimerRunning.collectAsState()
    val remainingFocusSeconds by viewModel.focusRemainingSeconds.collectAsState()
    val totalFocusDurationMinutes by viewModel.focusTotalDurationMinutes.collectAsState()
    val focusMode by viewModel.focusMode.collectAsState()
    val focusTaskName by viewModel.focusTaskName.collectAsState()
    val distractionsCount by viewModel.focusDistractionsCount.collectAsState()
    val recentFocusSessions by viewModel.recentFocusSessions.collectAsState()

    // Phase 2: AI Engine State
    val aiMessages by viewModel.allAiMessages.collectAsState()
    val activeAssistantType by viewModel.activeAssistantType.collectAsState()
    val isAiGenerating by viewModel.isAiGenerating.collectAsState()
    val dailyCoachSummary by viewModel.dailyCoachSummary.collectAsState()
    val weeklyReviewSummary by viewModel.weeklyReviewSummary.collectAsState()
    val foodEstimateResult by viewModel.foodEstimateState.collectAsState()
    val aiUsageStats by viewModel.todayAiUsage.collectAsState()

    // Phase 3: Career State
    val careerGoals by viewModel.allCareerGoals.collectAsState()
    val careerSkills by viewModel.allCareerSkills.collectAsState()
    val certifications by viewModel.allCertifications.collectAsState()
    val careerProjects by viewModel.allCareerProjects.collectAsState()
    val careerAchievements by viewModel.allCareerAchievements.collectAsState()
    val resumeItems by viewModel.allResumeItems.collectAsState()

    // Phase 3: Learning State
    val courses by viewModel.allCourses.collectAsState()
    val books by viewModel.allBooks.collectAsState()
    val studySessions by viewModel.allStudySessions.collectAsState()

    // Phase 3: Finance State
    val financeTransactions by viewModel.allFinanceTransactions.collectAsState()
    val financialGoals by viewModel.allFinancialGoals.collectAsState()

    // Phase 3: Gamification State
    val achievementBadges by viewModel.allAchievementBadges.collectAsState()
    val xpTransactions by viewModel.allXpTransactions.collectAsState()

    // Phase 3: Vision & Review State
    val visionItems by viewModel.allVisionItems.collectAsState()
    val weeklyReviews by viewModel.allWeeklyReviews.collectAsState()

    // Handle Toast Events with auto-dismiss
    LaunchedEffect(Unit) {
        viewModel.toastEvent.collect { toast ->
            activeToast = toast
            delay(4500)
            if (activeToast?.id == toast.id) {
                activeToast = null
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 600.dp),
            topBar = {
                PrimeTopBar(
                    profile = profile,
                    primeScore = dailyScore?.totalScore ?: dailyEntry?.primeScore ?: 0,
                    onScoreClick = { showScoreDialog = true },
                    onProfileClick = { currentScreen = PrimeScreen.Settings }
                )
            },
            bottomBar = {
                PrimeBottomBar(
                    currentScreen = currentScreen,
                    onNavigate = { currentScreen = it },
                    onQuickActionClick = { showQuickActionDialog = true }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentScreen) {
                    PrimeScreen.Dashboard -> {
                        DashboardScreen(
                            profile = profile,
                            dailyEntry = dailyEntry,
                            dailyScore = dailyScore,
                            totalLoggedDays = totalLoggedDays,
                            todayTasks = tasks,
                            todayTimeBlocks = timeBlocks,
                            onScoreClick = { showScoreDialog = true },
                            onNavigate = { currentScreen = it },
                            onQuickAction = { action ->
                                handleQuickAction(action, viewModel, onNavigate = { currentScreen = it })
                            },
                            onToggleTask = { viewModel.toggleTask(it) },
                            onAddTaskClick = { currentScreen = PrimeScreen.Planner }
                        )
                    }

                    PrimeScreen.Today -> {
                        TodayScreen(
                            profile = profile,
                            dailyEntry = dailyEntry,
                            habits = habits,
                            habitLogs = habitLogs,
                            nutritionLogs = nutritionLogs,
                            workoutSessions = workoutSessions,
                            tasks = tasks,
                            onToggleHabit = { id, isDone -> viewModel.toggleHabit(id, isDone) },
                            onToggleTask = { viewModel.toggleTask(it) },
                            onNavigate = { currentScreen = it },
                            onQuickWater = { viewModel.logQuickWater(250) }
                        )
                    }

                    PrimeScreen.Workout -> {
                        WorkoutScreen(
                            templates = workoutTemplates,
                            exercises = exercises,
                            sessionsHistory = workoutSessions,
                            activeSessionId = activeSessionId,
                            activeSessionSets = activeSessionSets,
                            restTimerSeconds = restTimerSeconds,
                            onStartWorkout = { name, id -> viewModel.startWorkout(name, id) },
                            onAddSet = { name, setNum, w, reps, rpe ->
                                viewModel.addSetToActiveSession(name, setNum, w, reps, rpe)
                            },
                            onCompleteWorkout = { dur, rpe, notes ->
                                viewModel.completeActiveWorkout(dur, rpe, notes)
                            },
                            onCancelWorkout = { viewModel.cancelActiveWorkout() },
                            onStartRestTimer = { viewModel.startRestTimer(it) },
                            onCancelRestTimer = { viewModel.cancelRestTimer() }
                        )
                    }

                    PrimeScreen.Nutrition -> {
                        NutritionScreen(
                            profile = profile,
                            dailyEntry = dailyEntry,
                            nutritionLogs = nutritionLogs,
                            availableFoods = availableFoods,
                            onLogFood = { meal, name, portions, cals, p, c, f ->
                                viewModel.logFood(meal, name, portions, cals, p, c, f)
                            },
                            onAddCustomFood = { name, size, cals, p, c, f ->
                                viewModel.addCustomFood(name, size, cals, p, c, f)
                            },
                            onQuickWater = { viewModel.logQuickWater(it) }
                        )
                    }

                    PrimeScreen.Habits -> {
                        HabitsScreen(
                            habits = habits,
                            habitLogs = habitLogs,
                            onToggleHabit = { id, isDone -> viewModel.toggleHabit(id, isDone) },
                            onAddHabit = { name, icon, cat, freq, reminder ->
                                viewModel.addHabit(name, icon, cat, freq, reminder)
                            }
                        )
                    }

                    PrimeScreen.Planner -> {
                        PlannerScreen(
                            tasks = tasks,
                            timeBlocks = timeBlocks,
                            goals = strategicGoals,
                            onToggleTask = { viewModel.toggleTask(it) },
                            onAddTask = { title, cat, prio, xp ->
                                viewModel.addTask(title, cat, prio, xp)
                            },
                            onAddTimeBlock = { title, start, end, cat, color ->
                                viewModel.addTimeBlock(title, start, end, cat, color)
                            },
                            onAddGoal = { title, desc, level, cat, deadline, prio ->
                                viewModel.addGoal(title, desc, level, cat, deadline, prio)
                            },
                            onAddMilestone = { goalId, title ->
                                viewModel.addGoalMilestone(goalId, title)
                            },
                            onToggleMilestone = { milestone, goalId ->
                                viewModel.toggleGoalMilestone(milestone, goalId)
                            },
                            onConvertGoalToTask = { goal ->
                                viewModel.convertGoalToPlannerTask(goal)
                            },
                            onDeleteGoal = { goal ->
                                viewModel.deleteGoal(goal)
                            }
                        )
                    }

                    PrimeScreen.Body -> {
                        BodyScreen(
                            latestMeasurement = latestMeasurement,
                            allMeasurements = allMeasurements,
                            photos = photos,
                            onLogMeasurement = { w, h, waist, chest, arms, thighs, neck, notes ->
                                viewModel.logBodyMeasurement(w, h, waist, chest, arms, thighs, neck, notes)
                            }
                        )
                    }

                    PrimeScreen.Grooming -> {
                        GroomingScreen(
                            groomingItems = groomingItems,
                            groomingLogs = todayGroomingLogs,
                            skincareProducts = skincareProducts,
                            skincareLogs = todaySkincareLogs,
                            onToggleGrooming = { id, routineType, isDone ->
                                viewModel.toggleGroomingItem(id, routineType, isDone)
                            },
                            onAddGroomingItem = { title, routineType ->
                                viewModel.addCustomGroomingItem(title, routineType)
                            },
                            onToggleSkincare = { id, routineTime, isDone ->
                                viewModel.toggleSkincareProduct(id, routineTime, isDone)
                            },
                            onAddSkincareProduct = { name, cat, brand, time, freq, notes ->
                                viewModel.addSkincareProduct(name, cat, brand, time, freq, notes)
                            }
                        )
                    }

                    PrimeScreen.Sleep -> {
                        SleepScreen(
                            todaySleep = todaySleepLog,
                            recentSleepLogs = recentSleepLogs,
                            onLogSleep = { bed, wake, dur, qual, energy, notes ->
                                viewModel.logSleep(bed, wake, dur, qual, energy, notes)
                            }
                        )
                    }

                    PrimeScreen.Mindset -> {
                        MindsetScreen(
                            todayMindset = todayMindsetLog,
                            recentMindsetLogs = recentMindsetLogs,
                            todayJournal = todayJournalEntry,
                            recentJournalEntries = recentJournalEntries,
                            onLogMindset = { mood, energy, stress, conf, focus, label ->
                                viewModel.logMindset(mood, energy, stress, conf, focus, label)
                            },
                            onSaveJournal = { acc, obst, dist, learn, imp, free ->
                                viewModel.saveJournal(acc, obst, dist, learn, imp, free)
                            }
                        )
                    }

                    PrimeScreen.Focus -> {
                        FocusScreen(
                            isTimerRunning = isFocusTimerRunning,
                            remainingSeconds = remainingFocusSeconds,
                            totalDurationMinutes = totalFocusDurationMinutes,
                            mode = focusMode,
                            taskName = focusTaskName,
                            distractionsCount = distractionsCount,
                            recentSessions = recentFocusSessions,
                            onStartTimer = { mode, dur, task ->
                                viewModel.startFocusTimer(mode, dur, task)
                            },
                            onPauseTimer = { viewModel.pauseFocusTimer() },
                            onResumeTimer = { viewModel.resumeFocusTimer() },
                            onCancelTimer = { viewModel.cancelFocusTimer() },
                            onLogDistraction = { note -> viewModel.logDistractionDuringFocus(note) },
                            onCompleteSession = { task, rating, notes ->
                                viewModel.completeFocusSession(task, rating, notes)
                            }
                        )
                    }

                    PrimeScreen.Ai,
                    PrimeScreen.PrimeAI -> {
                        PrimeAiScreen(
                            messages = aiMessages,
                            activeAssistantType = activeAssistantType,
                            isGenerating = isAiGenerating,
                            dailyCoach = dailyCoachSummary,
                            weeklyReview = weeklyReviewSummary,
                            foodEstimate = foodEstimateResult,
                            aiUsage = aiUsageStats,
                            onSelectAssistant = { viewModel.setAssistantType(it) },
                            onSendMessage = { viewModel.sendAiChatMessage(it) },
                            onApplyAction = { viewModel.applyAiSuggestedAction(it) },
                            onRefreshDailyCoach = { viewModel.refreshDailyCoach() },
                            onRefreshWeeklyReview = { viewModel.refreshWeeklyReview() },
                            onEstimateFood = { viewModel.estimateFoodNutrition(it) },
                            onLogEstimatedFood = { estimate ->
                                viewModel.logFood(
                                    mealType = "Snack",
                                    foodName = estimate.foodName,
                                    portions = 1f,
                                    calories = estimate.calories,
                                    protein = estimate.proteinGrams,
                                    carbs = estimate.carbsGrams,
                                    fat = estimate.fatGrams,
                                    source = "ai_estimate"
                                )
                                viewModel.clearFoodEstimate()
                            },
                            onClearChat = { viewModel.clearAiChat(it) }
                        )
                    }

                    PrimeScreen.Analytics -> {
                        AnalyticsScreen(
                            dailyEntries = recentDailyEntries,
                            measurements = allMeasurements,
                            sleepLogs = recentSleepLogs,
                            focusSessions = recentFocusSessions
                        )
                    }

                    PrimeScreen.Goals -> {
                        PlannerScreen(
                            tasks = tasks,
                            timeBlocks = timeBlocks,
                            goals = strategicGoals,
                            onToggleTask = { viewModel.toggleTask(it) },
                            onAddTask = { title, cat, prio, xp ->
                                viewModel.addTask(title, cat, prio, xp)
                            },
                            onAddTimeBlock = { title, start, end, cat, color ->
                                viewModel.addTimeBlock(title, start, end, cat, color)
                            },
                            onAddGoal = { title, desc, level, cat, deadline, prio ->
                                viewModel.addGoal(title, desc, level, cat, deadline, prio)
                            },
                            onAddMilestone = { goalId, title ->
                                viewModel.addGoalMilestone(goalId, title)
                            },
                            onToggleMilestone = { milestone, goalId ->
                                viewModel.toggleGoalMilestone(milestone, goalId)
                            },
                            onConvertGoalToTask = { goal ->
                                viewModel.convertGoalToPlannerTask(goal)
                            },
                            onDeleteGoal = { goal ->
                                viewModel.deleteGoal(goal)
                            }
                        )
                    }

                    PrimeScreen.Score -> {
                        DashboardScreen(
                            profile = profile,
                            dailyEntry = dailyEntry,
                            dailyScore = dailyScore,
                            totalLoggedDays = totalLoggedDays,
                            todayTasks = tasks,
                            todayTimeBlocks = timeBlocks,
                            onScoreClick = { showScoreDialog = true },
                            onNavigate = { currentScreen = it },
                            onQuickAction = { action ->
                                handleQuickAction(action, viewModel, onNavigate = { currentScreen = it })
                            },
                            onToggleTask = { viewModel.toggleTask(it) },
                            onAddTaskClick = { currentScreen = PrimeScreen.Planner }
                        )
                    }

                    PrimeScreen.Settings -> {
                        SettingsScreen(
                            profile = profile,
                            onUpdateProfile = { viewModel.updateProfile(it) },
                            onExportData = { viewModel.exportUserDataJson() },
                            onWipeData = { viewModel.wipeData() }
                        )
                    }

                    PrimeScreen.Career -> {
                        CareerScreen(
                            careerGoals = careerGoals,
                            careerSkills = careerSkills,
                            certifications = certifications,
                            projects = careerProjects,
                            achievements = careerAchievements,
                            resumeItems = resumeItems,
                            onAddGoal = { title, targetRole, salary, timeline ->
                                viewModel.addCareerGoal(title, targetRole, salary, timeline)
                            },
                            onAddSkill = { name, category, proficiency, target ->
                                viewModel.addCareerSkill(name, category, proficiency, target)
                            },
                            onUpdateSkillProgress = { skill, newProficiency ->
                                viewModel.updateSkillLevel(skill, newProficiency)
                            },
                            onAddCertification = { name, issuer, issueDate, status ->
                                viewModel.addCertification(name, issuer, issueDate, status)
                            },
                            onAddProject = { title, role, desc, outcomes, stack ->
                                viewModel.addCareerProject(title, role, desc, outcomes, stack)
                            },
                            onAddAchievement = { title, date, impact ->
                                viewModel.addCareerAchievement(title, date, impact)
                            },
                            onDeleteSkill = { skill ->
                                viewModel.deleteCareerSkill(skill)
                            }
                        )
                    }

                    PrimeScreen.Learning -> {
                        LearningScreen(
                            courses = courses,
                            books = books,
                            studySessions = studySessions,
                            onAddCourse = { title, platform, instructor, totalModules ->
                                viewModel.addLearningCourse(title, platform, instructor, totalModules)
                            },
                            onUpdateCourseProgress = { course, completedModules ->
                                viewModel.updateCourseProgress(course, completedModules)
                            },
                            onAddBook = { title, author, format, totalPages ->
                                viewModel.addLearningBook(title, author, format, totalPages)
                            },
                            onUpdateBookProgress = { book, currentPage ->
                                viewModel.updateBookProgress(book, currentPage)
                            },
                            onLogStudySession = { topic, category, durationMinutes, keyInsights ->
                                viewModel.logStudySession(topic, category, durationMinutes, keyInsights)
                            }
                        )
                    }

                    PrimeScreen.Finance -> {
                        FinanceScreen(
                            transactions = financeTransactions,
                            financialGoals = financialGoals,
                            userCurrency = profile?.currency ?: "MYR",
                            onAddTransaction = { type, category, amount, notes, isRecurring ->
                                viewModel.addFinanceTransaction(type, category, amount, notes, isRecurring)
                            },
                            onAddGoal = { title, goalType, targetAmount, currentAmount, deadline ->
                                viewModel.addFinancialGoal(title, goalType, targetAmount, currentAmount, deadline)
                            },
                            onUpdateGoalProgress = { goal, addedAmount ->
                                viewModel.updateFinancialGoalProgress(goal, addedAmount)
                            },
                            onDeleteTransaction = { tx ->
                                viewModel.deleteFinanceTransaction(tx)
                            }
                        )
                    }

                    PrimeScreen.Gamification -> {
                        GamificationScreen(
                            profile = profile,
                            badges = achievementBadges,
                            xpTransactions = xpTransactions,
                            onActivateStreakRecovery = {
                                viewModel.activateStreakGraceShield()
                            }
                        )
                    }

                    PrimeScreen.Vision -> {
                        VisionScreen(
                            profile = profile,
                            visionItems = visionItems,
                            onAddVisionMilestone = { title, content, horizon ->
                                viewModel.addVisionMilestone(title, content, horizon)
                            },
                            onToggleMilestone = { item ->
                                viewModel.toggleVisionMilestone(item)
                            },
                            onUpdateIdentityStatements = { whoIAm, whoIWantToBecome ->
                                viewModel.updateIdentityStatements(whoIAm, whoIWantToBecome)
                            }
                        )
                    }

                    PrimeScreen.WeeklyReview -> {
                        WeeklyReviewScreen(
                            weeklyReviews = weeklyReviews,
                            onGenerateWeeklyReview = {
                                viewModel.generateAndSaveWeeklyReview()
                            }
                        )
                    }

                    PrimeScreen.GlobalSearch -> {
                        GlobalSearchScreen(
                            onNavigateToScreen = { currentScreen = it },
                            allFoods = availableFoods.map { it.name },
                            allExercises = exercises.map { it.name },
                            allHabits = habits.map { it.name },
                            allGoals = strategicGoals.map { it.title },
                            allTasks = tasks.map { it.title },
                            allNotes = recentJournalEntries.mapNotNull { it.freeformText.takeIf { t -> t.isNotBlank() } },
                            allCourses = courses.map { it.title },
                            allTransactions = financeTransactions.map { "${it.category}: ${it.amount}" },
                            allSkills = careerSkills.map { it.name }
                        )
                    }

                    PrimeScreen.Onboarding -> {
                        OnboardingScreen(
                            onFinishOnboarding = { name, goal, calories, protein, sleepHours, focusMinutes, currency ->
                                viewModel.completeOnboarding(
                                    name = name,
                                    goal = goal,
                                    calories = calories,
                                    protein = protein,
                                    sleepHours = sleepHours,
                                    focusMinutes = focusMinutes,
                                    currency = currency
                                )
                                currentScreen = PrimeScreen.Dashboard
                            }
                        )
                    }
                }

                // Floating Toast / Undo Bar
                PrimeToastSnackbar(
                    toast = activeToast,
                    onDismiss = { activeToast = null },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }

        // Explainable Score Breakdown Modal Dialog
        if (showScoreDialog) {
            ScoreBreakdownDialog(
                scoreRecord = dailyScore,
                totalLoggedDays = totalLoggedDays,
                onDismiss = { showScoreDialog = false }
            )
        }

        // Quick Actions Dialog
        if (showQuickActionDialog) {
            QuickActionDialog(
                onDismiss = { showQuickActionDialog = false },
                onActionSelected = { action ->
                    handleQuickAction(action, viewModel, onNavigate = { currentScreen = it })
                }
            )
        }
    }
}

private fun handleQuickAction(
    action: QuickActionType,
    viewModel: PrimeViewModel,
    onNavigate: (PrimeScreen) -> Unit
) {
    when (action) {
        QuickActionType.LOG_WATER_500 -> viewModel.logQuickWater(500)
        QuickActionType.LOG_FOOD -> onNavigate(PrimeScreen.Nutrition)
        QuickActionType.START_WORKOUT -> onNavigate(PrimeScreen.Workout)
        QuickActionType.ADD_HABIT -> onNavigate(PrimeScreen.Habits)
        QuickActionType.ADD_TASK -> onNavigate(PrimeScreen.Planner)
        QuickActionType.LOG_WEIGHT -> onNavigate(PrimeScreen.Body)
        QuickActionType.START_FOCUS -> {
            viewModel.showToast("Opening Deep Focus Protocol sprint.")
            onNavigate(PrimeScreen.Focus)
        }
        QuickActionType.JOURNAL -> {
            viewModel.showToast("Opening Stoic Journal calibration.")
            onNavigate(PrimeScreen.Mindset)
        }
    }
}
