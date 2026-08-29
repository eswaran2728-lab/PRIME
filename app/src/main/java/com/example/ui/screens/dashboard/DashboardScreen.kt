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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.FaceRetouchingNatural
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.DailyEntryEntity
import com.example.data.local.entities.DailyScoreRecordEntity
import com.example.data.local.entities.ProfileEntity
import com.example.data.local.entities.TaskEntity
import com.example.data.local.entities.TimeBlockEntity
import com.example.ui.components.QuickActionType
import com.example.ui.navigation.PrimeScreen
import com.example.ui.theme.PrimeBlue
import com.example.ui.theme.PrimeBorder
import com.example.ui.theme.PrimeCyan
import com.example.ui.theme.PrimeGreen
import com.example.ui.theme.PrimeOnPrimaryContainer
import com.example.ui.theme.PrimeOrange
import com.example.ui.theme.PrimePrimary
import com.example.ui.theme.PrimePrimaryContainer
import com.example.ui.theme.PrimeSurface
import com.example.ui.theme.PrimeSurfaceContainerHigh
import com.example.ui.theme.PrimeSurfaceVariant
import com.example.ui.theme.PrimeTextPrimary
import com.example.ui.theme.PrimeTextSecondary

@Composable
fun DashboardScreen(
    profile: ProfileEntity?,
    dailyEntry: DailyEntryEntity?,
    dailyScore: DailyScoreRecordEntity?,
    totalLoggedDays: Int,
    todayTasks: List<TaskEntity>,
    todayTimeBlocks: List<TimeBlockEntity>,
    onScoreClick: () -> Unit,
    onNavigate: (PrimeScreen) -> Unit,
    onQuickAction: (QuickActionType) -> Unit,
    onToggleTask: (TaskEntity) -> Unit,
    onAddTaskClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalScore = dailyScore?.totalScore ?: dailyEntry?.primeScore ?: 0
    val isBaselineMode = totalLoggedDays < 3

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section 1: TODAY'S PRIME SCORE CARD (Immersive circular gauge + breakdown)
        item {
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = PrimeSurface
                ),
                border = BorderStroke(1.dp, PrimeBorder.copy(alpha = 0.3f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onScoreClick() }
                    .testTag("dashboard_prime_score_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    // Header inside card
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
                            Text(
                                text = "Details",
                                style = MaterialTheme.typography.labelSmall,
                                color = PrimePrimary
                            )
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "View score details",
                                tint = PrimePrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (isBaselineMode) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = PrimeSurfaceVariant,
                            border = BorderStroke(1.dp, PrimeBorder.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = PrimePrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Building your baseline...",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = PrimePrimary
                                    )
                                    Text(
                                        text = "Day $totalLoggedDays of 3 required for calibrated multi-domain scoring.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = PrimeTextSecondary
                                    )
                                }
                            }
                        }
                    } else {
                        // Circular Gauge & Subdomain Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Circular Ring Gauge
                            Box(
                                modifier = Modifier.size(120.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                val animatedProgress by animateFloatAsState(
                                    targetValue = (totalScore / 100f).coerceIn(0f, 1f),
                                    label = "scoreProgress"
                                )
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val strokeWidth = 8.dp.toPx()
                                    // Background Track
                                    drawArc(
                                        color = PrimeBorder.copy(alpha = 0.4f),
                                        startAngle = -90f,
                                        sweepAngle = 360f,
                                        useCenter = false,
                                        style = Stroke(width = strokeWidth)
                                    )
                                    // Progress Arc
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
                                        text = "$totalScore",
                                        style = MaterialTheme.typography.displayMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 36.sp
                                        ),
                                        color = Color.White
                                    )
                                    Text(
                                        text = if (totalScore >= 80) "OPTIMAL" else "ACTIVE",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp,
                                            letterSpacing = 1.sp
                                        ),
                                        color = PrimePrimary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            // 2x2 Sub-scores
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    SubScoreItem(
                                        label = "Fitness",
                                        value = "${((dailyScore?.fitnessScore ?: 8.5f) * 10f).toInt()}%"
                                    )
                                    SubScoreItem(
                                        label = "Discipline",
                                        value = "${((dailyScore?.disciplineScore ?: 9.0f) * 10f).toInt()}%"
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    SubScoreItem(
                                        label = "Nutrition",
                                        value = "${((dailyScore?.nutritionScore ?: 7.5f) * 10f).toInt()}%"
                                    )
                                    SubScoreItem(
                                        label = "Mindset",
                                        value = "${((dailyScore?.mindsetScore ?: 8.0f) * 10f).toInt()}%"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section 2: QUICK ACTIONS (Pill shapes with Immersive Purple Highlights)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickActionButton(
                    icon = Icons.Default.FitnessCenter,
                    label = "Workout",
                    isFeatured = true,
                    onClick = { onQuickAction(QuickActionType.START_WORKOUT) },
                    modifier = Modifier.weight(1f)
                )
                QuickActionButton(
                    icon = Icons.Default.Restaurant,
                    label = "Log Food",
                    isFeatured = false,
                    onClick = { onQuickAction(QuickActionType.LOG_FOOD) },
                    modifier = Modifier.weight(1f)
                )
                QuickActionButton(
                    icon = Icons.Default.WaterDrop,
                    label = "Water",
                    isFeatured = false,
                    onClick = { onQuickAction(QuickActionType.LOG_WATER_500) },
                    modifier = Modifier.weight(1f)
                )
                QuickActionButton(
                    icon = Icons.Default.MenuBook,
                    label = "Journal",
                    isFeatured = false,
                    onClick = { onQuickAction(QuickActionType.JOURNAL) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Section 3: TODAY'S PRIORITIES (Card with rounded list items)
        item {
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = PrimeSurface
                ),
                border = BorderStroke(1.dp, PrimeBorder.copy(alpha = 0.3f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "CRITICAL TARGETS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            ),
                            color = PrimeTextSecondary
                        )
                        IconButton(
                            onClick = onAddTaskClick,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Priority Task",
                                tint = PrimePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (todayTasks.isEmpty()) {
                        Text(
                            text = "No critical targets set. Tap + to set your priority focus.",
                            style = MaterialTheme.typography.bodySmall,
                            color = PrimeTextSecondary,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            todayTasks.forEach { task ->
                                PriorityTaskRow(
                                    task = task,
                                    onToggle = { onToggleTask(task) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section 4: TODAY'S SCHEDULE
        item {
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = PrimeSurface
                ),
                border = BorderStroke(1.dp, PrimeBorder.copy(alpha = 0.3f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TODAY'S SCHEDULE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            ),
                            color = PrimeTextSecondary
                        )
                        Text(
                            text = "View Planner",
                            style = MaterialTheme.typography.labelSmall,
                            color = PrimePrimary,
                            modifier = Modifier.clickable { onNavigate(PrimeScreen.Planner) }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (todayTimeBlocks.isEmpty()) {
                        Text(
                            text = "No time blocks scheduled for today.",
                            style = MaterialTheme.typography.bodySmall,
                            color = PrimeTextSecondary,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            todayTimeBlocks.take(4).forEach { block ->
                                TimeBlockMiniRow(block = block)
                            }
                        }
                    }
                }
            }
        }

        // Section 5: PRIME OPERATING PROTOCOLS & MODULES (Phase 2 Modules)
        item {
            Text(
                text = "PRIME PROTOCOLS & MODULES",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                ),
                color = PrimeTextSecondary
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ProtocolQuickCard(
                    title = "PRIME AI Coach",
                    subtitle = "Tactical debriefs & food scanner",
                    icon = Icons.Default.AutoAwesome,
                    color = PrimePrimary,
                    onClick = { onNavigate(PrimeScreen.Ai) },
                    modifier = Modifier.weight(1f)
                )
                ProtocolQuickCard(
                    title = "Deep Focus",
                    subtitle = "Sprint timer & distraction log",
                    icon = Icons.Default.Timer,
                    color = Color(0xFF3B82F6),
                    onClick = { onNavigate(PrimeScreen.Focus) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ProtocolQuickCard(
                    title = "Sleep & Recovery",
                    subtitle = "Bedtime, quality & sleep debt",
                    icon = Icons.Default.Bedtime,
                    color = Color(0xFF8B5CF6),
                    onClick = { onNavigate(PrimeScreen.Sleep) },
                    modifier = Modifier.weight(1f)
                )
                ProtocolQuickCard(
                    title = "Stoic Mindset",
                    subtitle = "Daily scales & 5-prompt audit",
                    icon = Icons.Default.Psychology,
                    color = Color(0xFF10B981),
                    onClick = { onNavigate(PrimeScreen.Mindset) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ProtocolQuickCard(
                    title = "Grooming & Skin",
                    subtitle = "AM/PM/Weekly hygiene stacks",
                    icon = Icons.Default.FaceRetouchingNatural,
                    color = Color(0xFFF59E0B),
                    onClick = { onNavigate(PrimeScreen.Grooming) },
                    modifier = Modifier.weight(1f)
                )
                ProtocolQuickCard(
                    title = "Analytics Telemetry",
                    subtitle = "Trends, volume & score charts",
                    icon = Icons.Default.QueryStats,
                    color = Color(0xFF06B6D4),
                    onClick = { onNavigate(PrimeScreen.Analytics) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Section 6: PHASE 3 CAPABILITY MODULES
        item {
            Text(
                text = "STRATEGIC & MASTERY MODULES",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                ),
                color = PrimeTextSecondary
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ProtocolQuickCard(
                    title = "Career & Skills",
                    subtitle = "Goals, matrix & resume items",
                    icon = Icons.Default.Work,
                    color = Color(0xFF3B82F6),
                    onClick = { onNavigate(PrimeScreen.Career) },
                    modifier = Modifier.weight(1f)
                )
                ProtocolQuickCard(
                    title = "Learning & Books",
                    subtitle = "Courses, reading list & study logs",
                    icon = Icons.Default.School,
                    color = Color(0xFF10B981),
                    onClick = { onNavigate(PrimeScreen.Learning) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ProtocolQuickCard(
                    title = "Finance & Runway",
                    subtitle = "Cashflow, war chest & goals",
                    icon = Icons.Default.AccountBalanceWallet,
                    color = Color(0xFFE5A93C),
                    onClick = { onNavigate(PrimeScreen.Finance) },
                    modifier = Modifier.weight(1f)
                )
                ProtocolQuickCard(
                    title = "XP & Achievements",
                    subtitle = "Level progress & trophy ledger",
                    icon = Icons.Default.EmojiEvents,
                    color = Color(0xFFF59E0B),
                    onClick = { onNavigate(PrimeScreen.Gamification) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ProtocolQuickCard(
                    title = "PRIME Vision",
                    subtitle = "Identity pillars & 5Y roadmap",
                    icon = Icons.Default.Explore,
                    color = Color(0xFF8B5CF6),
                    onClick = { onNavigate(PrimeScreen.Vision) },
                    modifier = Modifier.weight(1f)
                )
                ProtocolQuickCard(
                    title = "Weekly Review",
                    subtitle = "Performance summary & AI audit",
                    icon = Icons.Default.RateReview,
                    color = Color(0xFFEC4899),
                    onClick = { onNavigate(PrimeScreen.WeeklyReview) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            ProtocolQuickCard(
                title = "Global Universal Search",
                subtitle = "Search exercises, foods, habits, goals and logs across the OS",
                icon = Icons.Default.Search,
                color = PrimePrimary,
                onClick = { onNavigate(PrimeScreen.GlobalSearch) },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ProtocolQuickCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PrimeSurface),
        border = BorderStroke(1.dp, PrimeBorder.copy(alpha = 0.35f)),
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = PrimeTextSecondary, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(title, style = MaterialTheme.typography.titleSmall, color = Color.White, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, style = MaterialTheme.typography.labelSmall, color = PrimeTextSecondary)
        }
    }
}

@Composable
fun SubScoreItem(
    label: String,
    value: String
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = PrimeTextSecondary
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = PrimeTextPrimary
        )
    }
}

@Composable
fun QuickActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isFeatured: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isFeatured) PrimePrimaryContainer else PrimeSurfaceContainerHigh
    val contentColor = if (isFeatured) PrimeOnPrimaryContainer else PrimePrimary

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = bgColor,
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp
                ),
                color = if (isFeatured) PrimeOnPrimaryContainer else PrimeTextPrimary
            )
        }
    }
}

@Composable
fun PriorityTaskRow(
    task: TaskEntity,
    onToggle: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = PrimeSurfaceVariant,
        border = BorderStroke(1.dp, PrimeBorder.copy(alpha = 0.2f)),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onToggle() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                contentDescription = if (task.isCompleted) "Completed" else "Incomplete",
                tint = if (task.isCompleted) PrimePrimary else PrimeBorder,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = task.title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (task.isCompleted) FontWeight.Normal else FontWeight.Medium,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                ),
                color = if (task.isCompleted) PrimeTextSecondary else PrimeTextPrimary,
                modifier = Modifier.weight(1f)
            )
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = PrimePrimaryContainer
            ) {
                Text(
                    text = "+${task.xpReward} XP",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    ),
                    color = PrimeOnPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun TimeBlockMiniRow(
    block: TimeBlockEntity
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = PrimeSurfaceVariant,
        border = BorderStroke(1.dp, PrimeBorder.copy(alpha = 0.2f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(
                            try {
                                Color(android.graphics.Color.parseColor(block.colorHex))
                            } catch (e: Exception) {
                                PrimePrimary
                            }
                        )
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = block.title,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    color = PrimeTextPrimary
                )
            }
            Text(
                text = "${block.startTime} - ${block.endTime}",
                style = MaterialTheme.typography.labelSmall,
                color = PrimeTextSecondary
            )
        }
    }
}

