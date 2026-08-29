package com.example.ui.screens.today

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.DailyEntryEntity
import com.example.data.local.entities.HabitEntity
import com.example.data.local.entities.HabitLogEntity
import com.example.data.local.entities.NutritionLogEntity
import com.example.data.local.entities.ProfileEntity
import com.example.data.local.entities.TaskEntity
import com.example.data.local.entities.WorkoutSessionEntity
import com.example.ui.navigation.PrimeScreen
import com.example.ui.theme.PrimeBlue
import com.example.ui.theme.PrimeCyan
import com.example.ui.theme.PrimeGold
import com.example.ui.theme.PrimeGreen
import com.example.ui.theme.PrimeOrange
import com.example.ui.theme.PrimePurple
import com.example.ui.theme.PrimeRed

@Composable
fun TodayScreen(
    profile: ProfileEntity?,
    dailyEntry: DailyEntryEntity?,
    habits: List<HabitEntity>,
    habitLogs: List<HabitLogEntity>,
    nutritionLogs: List<NutritionLogEntity>,
    workoutSessions: List<WorkoutSessionEntity>,
    tasks: List<TaskEntity>,
    onToggleHabit: (Long, Boolean) -> Unit,
    onToggleTask: (TaskEntity) -> Unit,
    onNavigate: (PrimeScreen) -> Unit,
    onQuickWater: () -> Unit,
    modifier: Modifier = Modifier
) {
    val completedHabitIds = habitLogs.filter { it.isCompleted }.map { it.habitId }.toSet()
    val targetCalories = profile?.targetCalories ?: 2400
    val targetProtein = profile?.targetProteinGrams ?: 160f
    val loggedCalories = dailyEntry?.caloriesLogged ?: 0
    val loggedProtein = dailyEntry?.proteinGrams ?: 0f
    val loggedWater = dailyEntry?.waterTotalMl ?: 0
    val targetWater = profile?.targetWaterMl ?: 3000

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "TODAY'S EXECUTION",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp
                ),
                color = PrimeGold
            )
            Text(
                text = "Chronological alignment for total discipline.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // 1. MORNING SECTION
        item {
            TodayDomainCard(
                title = "MORNING ROUTINE",
                timeTag = "06:00 - 09:00",
                icon = Icons.Default.WbSunny,
                color = PrimeOrange
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val morningHabits = habits.filter { it.category in listOf("Health", "Discipline") }
                    if (morningHabits.isEmpty()) {
                        Text("No morning habits scheduled.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    } else {
                        morningHabits.forEach { habit ->
                            val isDone = habit.id in completedHabitIds
                            TodayHabitCheckRow(
                                habit = habit,
                                isCompleted = isDone,
                                onToggle = { onToggleHabit(habit.id, isDone) }
                            )
                        }
                    }
                }
            }
        }

        // 2. WORK SECTION
        item {
            TodayDomainCard(
                title = "WORK & CAREER",
                timeTag = "09:00 - 17:00",
                icon = Icons.Default.Work,
                color = PrimeBlue
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val workTasks = tasks.filter { it.category in listOf("Work", "Priority") }
                    if (workTasks.isEmpty()) {
                        Text("No work priorities logged today.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    } else {
                        workTasks.forEach { task ->
                            TodayTaskCheckRow(task = task, onToggle = { onToggleTask(task) })
                        }
                    }
                }
            }
        }

        // 3. HEALTH & HYDRATION SECTION
        item {
            TodayDomainCard(
                title = "HEALTH & HYDRATION",
                timeTag = "All Day",
                icon = Icons.Default.WaterDrop,
                color = PrimeCyan
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "$loggedWater ml / $targetWater ml",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${((loggedWater.toFloat() / targetWater.toFloat()) * 100).toInt().coerceIn(0, 100)}% of daily hydration target",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = PrimeCyan.copy(alpha = 0.15f),
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onQuickWater() }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.WaterDrop, contentDescription = null, tint = PrimeCyan, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("+250ml", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = PrimeCyan)
                            }
                        }
                    }
                    LinearProgressIndicator(
                        progress = { (loggedWater.toFloat() / targetWater.toFloat()).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = PrimeCyan,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
        }

        // 4. FITNESS SECTION
        item {
            TodayDomainCard(
                title = "FITNESS & PHYSICAL OUTPUT",
                timeTag = "17:00 - 19:00",
                icon = Icons.Default.FitnessCenter,
                color = PrimeGold
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val completedSession = workoutSessions.firstOrNull { it.isCompleted }
                    if (completedSession != null) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = PrimeGreen.copy(alpha = 0.12f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "Completed: ${completedSession.templateName}",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = PrimeGreen
                                    )
                                    Text(
                                        text = "${completedSession.durationMinutes} mins · ${completedSession.totalSetsCompleted} sets · ${completedSession.totalVolumeKg.toInt()} kg volume",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PrimeGreen)
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "No workout completed yet today.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = PrimeGold.copy(alpha = 0.15f),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { onNavigate(PrimeScreen.Workout) }
                            ) {
                                Text(
                                    text = "Start Session",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = PrimeGold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 5. NUTRITION SECTION
        item {
            TodayDomainCard(
                title = "NUTRITION & MACROS",
                timeTag = "Daily Target",
                icon = Icons.Default.Restaurant,
                color = PrimeGreen
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "$loggedCalories / $targetCalories kcal",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Protein: ${loggedProtein.toInt()}g / ${targetProtein.toInt()}g",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = PrimeGreen.copy(alpha = 0.15f),
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onNavigate(PrimeScreen.Nutrition) }
                        ) {
                            Text(
                                text = "Log Meals",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = PrimeGreen,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                    LinearProgressIndicator(
                        progress = { (loggedCalories.toFloat() / targetCalories.toFloat()).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = PrimeGreen,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
        }

        // 6. FOCUS SECTION
        item {
            TodayDomainCard(
                title = "FOCUS & SIGNAL",
                timeTag = "Continuous",
                icon = Icons.Default.HourglassTop,
                color = PrimePurple
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Deep Focus: ${dailyEntry?.focusMinutes ?: 0} mins",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Zero distraction cognitive blocks",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = PrimePurple.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "Target: 120m",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = PrimePurple,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // 7. EVENING SECTION
        item {
            TodayDomainCard(
                title = "EVENING RECOVERY",
                timeTag = "21:00 - 23:00",
                icon = Icons.Default.Bedtime,
                color = Color(0xFF6366F1)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val eveningHabits = habits.filter { it.category in listOf("Mindset", "Learning") }
                    if (eveningHabits.isEmpty()) {
                        Text("Wind-down and plan tomorrow's schedule.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    } else {
                        eveningHabits.forEach { habit ->
                            val isDone = habit.id in completedHabitIds
                            TodayHabitCheckRow(
                                habit = habit,
                                isCompleted = isDone,
                                onToggle = { onToggleHabit(habit.id, isDone) }
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun TodayDomainCard(
    title: String,
    timeTag: String,
    icon: ImageVector,
    color: Color,
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = color.copy(alpha = 0.15f),
                        modifier = Modifier.size(30.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = timeTag,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun TodayHabitCheckRow(
    habit: HabitEntity,
    isCompleted: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onToggle() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (isCompleted) PrimeGreen else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = habit.name,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = if (isCompleted) FontWeight.Normal else FontWeight.Medium),
            color = if (isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        if (habit.currentStreak > 0) {
            Text(
                text = "🔥 ${habit.currentStreak}d",
                style = MaterialTheme.typography.labelSmall,
                color = PrimeOrange
            )
        }
    }
}

@Composable
fun TodayTaskCheckRow(
    task: TaskEntity,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onToggle() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (task.isCompleted) PrimeGreen else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = task.title,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = if (task.isCompleted) FontWeight.Normal else FontWeight.Medium),
            color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
    }
}
