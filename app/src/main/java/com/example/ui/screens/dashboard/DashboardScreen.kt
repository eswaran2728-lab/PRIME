package com.example.ui.screens.dashboard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.BodyMeasurementEntity
import com.example.data.local.entities.DailyEntryEntity
import com.example.data.local.entities.DailyScoreRecordEntity
import com.example.data.local.entities.HabitEntity
import com.example.data.local.entities.HabitLogEntity
import com.example.data.local.entities.ProfileEntity
import com.example.data.local.entities.TaskEntity
import com.example.ui.components.BlueprintFrame
import com.example.ui.components.QuickActionType
import com.example.ui.components.SquareTag
import com.example.ui.components.SquareTagVariant
import com.example.ui.navigation.PrimeScreen
import com.example.ui.theme.PrimePrimary
import com.example.ui.theme.PrimeSurface
import com.example.ui.theme.PrimeSurfaceVariant
import com.example.ui.theme.PrimeTextPrimary
import com.example.ui.theme.PrimeTextSecondary
import java.util.Locale

@Composable
fun DashboardScreen(
    profile: ProfileEntity?,
    dailyEntry: DailyEntryEntity?,
    dailyScore: DailyScoreRecordEntity?,
    totalLoggedDays: Int,
    todayTasks: List<TaskEntity>,
    latestMeasurement: BodyMeasurementEntity?,
    habits: List<HabitEntity>,
    habitLogs: List<HabitLogEntity>,
    onScoreClick: () -> Unit,
    onNavigate: (PrimeScreen) -> Unit,
    onQuickAction: (QuickActionType) -> Unit,
    onToggleTask: (TaskEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalScore = dailyScore?.totalScore ?: dailyEntry?.primeScore ?: 0
    val isBaselineMode = totalLoggedDays < 3

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        // TODAY'S PRIME SCORE — glass hero card in the blueprint frame.
        item {
            BlueprintFrame(
                background = PrimeSurface.copy(alpha = 0.55f),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onScoreClick() }
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TODAY'S PRIME SCORE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            ),
                            color = PrimeTextSecondary
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Breakdown", style = MaterialTheme.typography.labelSmall, color = PrimePrimary)
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "View score breakdown",
                                tint = PrimePrimary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (isBaselineMode) {
                        Text(
                            text = "Building your baseline — day $totalLoggedDays of 3 required for calibrated scoring.",
                            style = MaterialTheme.typography.bodySmall,
                            color = PrimeTextSecondary
                        )
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ScoreRing(score = totalScore, sizeDp = 132.dp)

                            Spacer(modifier = Modifier.width(20.dp))

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    SubScoreBar(label = "Fitness", value = ((dailyScore?.fitnessScore ?: 8.5f) * 10f).toInt(), modifier = Modifier.weight(1f))
                                    Spacer(modifier = Modifier.width(14.dp))
                                    SubScoreBar(label = "Discipline", value = ((dailyScore?.disciplineScore ?: 9.0f) * 10f).toInt(), modifier = Modifier.weight(1f))
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    SubScoreBar(label = "Nutrition", value = ((dailyScore?.nutritionScore ?: 7.5f) * 10f).toInt(), modifier = Modifier.weight(1f))
                                    Spacer(modifier = Modifier.width(14.dp))
                                    SubScoreBar(label = "Mindset", value = ((dailyScore?.mindsetScore ?: 8.0f) * 10f).toInt(), modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }

        // QUICK ACTIONS
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickActionSquare(Icons.Default.FitnessCenter, "Workout", featured = true, onClick = { onQuickAction(QuickActionType.START_WORKOUT) }, modifier = Modifier.weight(1f))
                QuickActionSquare(Icons.Default.Restaurant, "Log Food", featured = false, onClick = { onQuickAction(QuickActionType.LOG_FOOD) }, modifier = Modifier.weight(1f))
                QuickActionSquare(Icons.Default.WaterDrop, "Water", featured = false, onClick = { onQuickAction(QuickActionType.LOG_WATER_500) }, modifier = Modifier.weight(1f))
                QuickActionSquare(Icons.Default.MenuBook, "Journal", featured = false, onClick = { onQuickAction(QuickActionType.JOURNAL) }, modifier = Modifier.weight(1f))
            }
        }

        // CRITICAL TARGETS
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PrimeSurface)
                    .padding(14.dp)
            ) {
                Text(
                    text = "CRITICAL TARGETS",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp),
                    color = PrimeTextSecondary
                )
                if (todayTasks.isEmpty()) {
                    Text(
                        text = "No critical targets set for today.",
                        style = MaterialTheme.typography.bodySmall,
                        color = PrimeTextSecondary,
                        modifier = Modifier.padding(top = 10.dp)
                    )
                } else {
                    todayTasks.forEach { task ->
                        CriticalTargetRow(task = task, onToggle = { onToggleTask(task) })
                    }
                }
            }
        }

        // MODULES
        item {
            Text(
                text = "MODULES",
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                color = PrimeTextSecondary
            )
        }

        item {
            val loggedCalories = dailyEntry?.caloriesLogged ?: 0
            val targetCalories = profile?.targetCalories ?: 2400
            val completedHabits = habitLogs.count { it.isCompleted }
            val bodySubtitle = if (latestMeasurement != null) {
                "${latestMeasurement.weightKg}kg · ${latestMeasurement.bodyFatEstimate?.let { String.format(Locale.US, "%.1f", it) } ?: "--"}% BF"
            } else {
                "No measurement yet"
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ModuleSummaryCard(
                        icon = Icons.Default.FitnessCenter,
                        title = "Body",
                        subtitle = bodySubtitle,
                        onClick = { onNavigate(PrimeScreen.Body) },
                        modifier = Modifier.weight(1f)
                    )
                    ModuleSummaryCard(
                        icon = Icons.Default.Restaurant,
                        title = "Nutrition",
                        subtitle = "$loggedCalories / $targetCalories kcal",
                        onClick = { onNavigate(PrimeScreen.Nutrition) },
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ModuleSummaryCard(
                        icon = Icons.Default.CheckCircle,
                        title = "Habits",
                        subtitle = "$completedHabits of ${habits.size} done today",
                        onClick = { onNavigate(PrimeScreen.Habits) },
                        modifier = Modifier.weight(1f)
                    )
                    ModuleSummaryCard(
                        icon = Icons.Default.CalendarMonth,
                        title = "Planner",
                        subtitle = "Time blocks & strategic goals",
                        onClick = { onNavigate(PrimeScreen.Planner) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
private fun ScoreRing(score: Int, sizeDp: androidx.compose.ui.unit.Dp) {
    Box(modifier = Modifier.size(sizeDp), contentAlignment = Alignment.Center) {
        val animatedProgress by animateFloatAsState(
            targetValue = (score / 100f).coerceIn(0f, 1f),
            label = "scoreProgress"
        )
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 8.dp.toPx()
            drawArc(
                color = PrimeSurfaceVariant,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth)
            )
            drawArc(
                color = PrimePrimary,
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$score",
                style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold, fontSize = 40.sp),
                color = PrimeTextPrimary
            )
            Text(
                text = if (score >= 80) "OPTIMAL" else if (score >= 60) "ACTIVE" else "BUILDING",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp, letterSpacing = 1.sp),
                color = PrimePrimary
            )
        }
    }
}

@Composable
private fun SubScoreBar(label: String, value: Int, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = PrimeTextSecondary)
        Text(
            text = "$value%",
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
            color = PrimeTextPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .background(PrimeTextPrimary.copy(alpha = 0.14f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = (value / 100f).coerceIn(0f, 1f))
                    .fillMaxSize()
                    .background(PrimePrimary)
            )
        }
    }
}

@Composable
private fun QuickActionSquare(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    featured: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(PrimeSurface)
            .border(BorderStroke(1.dp, if (featured) PrimePrimary else Color.Transparent))
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (featured) PrimePrimary else PrimeTextPrimary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
            color = if (featured) PrimePrimary else PrimeTextPrimary
        )
    }
}

@Composable
private fun CriticalTargetRow(task: TaskEntity, onToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .padding(vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
            contentDescription = if (task.isCompleted) "Completed" else "Incomplete",
            tint = if (task.isCompleted) PrimePrimary else PrimeTextSecondary,
            modifier = Modifier.size(19.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = task.title,
            style = MaterialTheme.typography.bodyMedium.copy(
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
            ),
            color = if (task.isCompleted) PrimeTextSecondary else PrimeTextPrimary,
            modifier = Modifier.weight(1f)
        )
        SquareTag(text = "+${task.xpReward} XP", variant = SquareTagVariant.Accent)
    }
}

@Composable
private fun ModuleSummaryCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .aspectRatio(1.6f)
            .background(PrimeSurface)
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Icon(icon, contentDescription = null, tint = PrimePrimary, modifier = Modifier.size(17.dp))
        Spacer(modifier = Modifier.weight(1f))
        Text(title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = PrimeTextPrimary)
        Text(subtitle, style = MaterialTheme.typography.labelSmall, color = PrimeTextSecondary, maxLines = 1)
    }
}
