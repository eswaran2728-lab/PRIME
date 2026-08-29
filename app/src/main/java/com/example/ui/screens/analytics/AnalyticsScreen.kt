package com.example.ui.screens.analytics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.BodyMeasurementEntity
import com.example.data.local.entities.DailyEntryEntity
import com.example.data.local.entities.FocusSessionEntity
import com.example.data.local.entities.SleepLogEntity
import com.example.ui.theme.PrimeBorder
import com.example.ui.theme.PrimeCardBg
import com.example.ui.theme.PrimeOnPrimaryDark
import com.example.ui.theme.PrimePrimary
import com.example.ui.theme.PrimeSurface
import com.example.ui.theme.PrimeTextMuted
import com.example.ui.theme.PrimeTextSecondary

@Composable
fun AnalyticsScreen(
    dailyEntries: List<DailyEntryEntity>,
    measurements: List<BodyMeasurementEntity>,
    sleepLogs: List<SleepLogEntity>,
    focusSessions: List<FocusSessionEntity>,
    modifier: Modifier = Modifier
) {
    var selectedRange by remember { mutableStateOf("week") } // "week", "month", "year"
    var selectedMetric by remember { mutableStateOf("score") } // "score", "weight", "workouts", "nutrition", "sleep", "habits", "focus"

    val metrics = listOf(
        Pair("score", "PRIME Score"),
        Pair("weight", "Body Weight"),
        Pair("nutrition", "Calories & Protein"),
        Pair("sleep", "Sleep Hours"),
        Pair("focus", "Deep Focus"),
        Pair("habits", "Habit Consistency")
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "LONGITUDINAL TELEMETRY",
                    style = MaterialTheme.typography.labelMedium,
                    color = PrimePrimary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Analytics & Performance Trends",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Time Range Filter
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(Pair("week", "Last 7 Days"), Pair("month", "Last 30 Days"), Pair("year", "Quarterly / Year")).forEach { (key, label) ->
                    FilterChip(
                        selected = selectedRange == key,
                        onClick = { selectedRange = key },
                        label = { Text(label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimePrimary,
                            selectedLabelColor = PrimeOnPrimaryDark
                        )
                    )
                }
            }
        }

        // Metric Selector Chips
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                metrics.take(3).forEach { (key, label) ->
                    FilterChip(
                        selected = selectedMetric == key,
                        onClick = { selectedMetric = key },
                        label = { Text(label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimePrimary,
                            selectedLabelColor = PrimeOnPrimaryDark
                        )
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                metrics.drop(3).forEach { (key, label) ->
                    FilterChip(
                        selected = selectedMetric == key,
                        onClick = { selectedMetric = key },
                        label = { Text(label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimePrimary,
                            selectedLabelColor = PrimeOnPrimaryDark
                        )
                    )
                }
            }
        }

        // Primary Analytics Chart Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("analytics_chart_card"),
                colors = CardDefaults.cardColors(containerColor = PrimeCardBg),
                shape = RoundedCornerShape(16.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(PrimeBorder))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val metricTitle = metrics.find { it.first == selectedMetric }?.second ?: "PRIME Score"
                    Text(
                        text = "$metricTitle Trend",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Historical distribution across $selectedRange",
                        style = MaterialTheme.typography.bodySmall,
                        color = PrimeTextSecondary
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Dynamic Trend Graph Canvas
                    val dataPoints = when (selectedMetric) {
                        "score" -> dailyEntries.map { it.primeScore.toFloat() }.ifEmpty { listOf(80f, 85f, 82f, 88f, 91f, 89f, 92f) }
                        "weight" -> measurements.map { it.weightKg }.ifEmpty { listOf(83.5f, 83.2f, 82.9f, 82.7f, 82.5f) }
                        "nutrition" -> dailyEntries.map { it.caloriesLogged.toFloat() }.ifEmpty { listOf(2400f, 2550f, 2480f, 2600f, 2500f) }
                        "sleep" -> sleepLogs.map { it.durationMinutes / 60f }.ifEmpty { listOf(7.5f, 8.0f, 7.2f, 7.8f, 8.2f) }
                        "focus" -> focusSessions.map { it.durationMinutes.toFloat() }.ifEmpty { listOf(50f, 90f, 60f, 75f, 90f) }
                        else -> dailyEntries.map { it.habitsCompletedCount.toFloat() }.ifEmpty { listOf(4f, 5f, 6f, 5f, 6f) }
                    }

                    TrendChartCanvas(dataPoints = dataPoints, lineColor = PrimePrimary)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Statistical Summary Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Current", style = MaterialTheme.typography.labelSmall, color = PrimeTextSecondary)
                            Text(
                                text = "%.1f".format(dataPoints.lastOrNull() ?: 0f),
                                style = MaterialTheme.typography.titleMedium,
                                color = PrimePrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column {
                            Text("7-Day Avg", style = MaterialTheme.typography.labelSmall, color = PrimeTextSecondary)
                            Text(
                                text = "%.1f".format(if (dataPoints.isNotEmpty()) dataPoints.average() else 0.0),
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column {
                            Text("Peak High", style = MaterialTheme.typography.labelSmall, color = PrimeTextSecondary)
                            Text(
                                text = "%.1f".format(dataPoints.maxOrNull() ?: 0f),
                                style = MaterialTheme.typography.titleMedium,
                                color = Color(0xFF10B981),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Secondary Key Metric Cards Grid
        item {
            Text("Core Pillar Summaries", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricSummaryCard(
                    title = "Avg Sleep",
                    value = if (sleepLogs.isNotEmpty()) "%.1fh".format(sleepLogs.map { it.durationMinutes / 60f }.average()) else "7.8h",
                    subtitle = "Quality 4.8/5",
                    icon = Icons.Default.Bedtime,
                    color = Color(0xFF8B5CF6),
                    modifier = Modifier.weight(1f)
                )
                MetricSummaryCard(
                    title = "Total Focus",
                    value = "${focusSessions.sumOf { it.durationMinutes }}m",
                    subtitle = "${focusSessions.size} sprints logged",
                    icon = Icons.Default.Timer,
                    color = PrimePrimary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricSummaryCard(
                    title = "Workouts",
                    value = "${dailyEntries.count { it.workoutDone }}",
                    subtitle = "100% adherence",
                    icon = Icons.Default.FitnessCenter,
                    color = Color(0xFFE5A93C),
                    modifier = Modifier.weight(1f)
                )
                MetricSummaryCard(
                    title = "Habits Rate",
                    value = "94%",
                    subtitle = "Disciplined consistency",
                    icon = Icons.Default.CheckCircle,
                    color = Color(0xFF10B981),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun TrendChartCanvas(
    dataPoints: List<Float>,
    lineColor: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .background(PrimeSurface, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (dataPoints.size < 2) return@Canvas

            val minVal = (dataPoints.minOrNull() ?: 0f) * 0.95f
            val maxVal = (dataPoints.maxOrNull() ?: 100f) * 1.05f
            val range = if (maxVal - minVal > 0) maxVal - minVal else 1f

            val stepX = size.width / (dataPoints.size - 1)

            val path = Path()
            dataPoints.forEachIndexed { index, value ->
                val x = index * stepX
                val normalizedY = (value - minVal) / range
                val y = size.height - (normalizedY * size.height)

                if (index == 0) {
                    path.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                }

                // Draw point circle
                drawCircle(
                    color = lineColor,
                    radius = 4.dp.toPx(),
                    center = Offset(x, y)
                )
            }

            drawPath(
                path = path,
                color = lineColor,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )
        }
    }
}

@Composable
private fun MetricSummaryCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = PrimeCardBg),
        shape = RoundedCornerShape(12.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(PrimeBorder))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, style = MaterialTheme.typography.labelMedium, color = PrimeTextSecondary)
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.headlineSmall, color = Color.White, fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.labelSmall, color = PrimeTextMuted)
        }
    }
}
