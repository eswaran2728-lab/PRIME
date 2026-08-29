package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.entities.DailyScoreRecordEntity
import com.example.ui.theme.PrimeBlue
import com.example.ui.theme.PrimeBorder
import com.example.ui.theme.PrimeGreen
import com.example.ui.theme.PrimeOnPrimaryContainer
import com.example.ui.theme.PrimeOnPrimaryDark
import com.example.ui.theme.PrimePrimary
import com.example.ui.theme.PrimePrimaryContainer
import com.example.ui.theme.PrimePurple
import com.example.ui.theme.PrimeSurface
import com.example.ui.theme.PrimeSurfaceVariant
import com.example.ui.theme.PrimeTextPrimary
import com.example.ui.theme.PrimeTextSecondary

data class CategoryBreakdown(
    val name: String,
    val score: Float,
    val maxWeight: Float,
    val color: Color
)

@Composable
fun ScoreBreakdownDialog(
    scoreRecord: DailyScoreRecordEntity?,
    totalLoggedDays: Int,
    onDismiss: () -> Unit
) {
    val isBaselineMode = totalLoggedDays < 3 || (scoreRecord?.isBaselineCalibrated == false && totalLoggedDays < 3)
    val totalScore = scoreRecord?.totalScore ?: 0

    val categories = listOf(
        CategoryBreakdown("Body Composition & Tracking", scoreRecord?.bodyScore ?: 3f, 15f, PrimePrimary),
        CategoryBreakdown("Fitness & Volume Output", scoreRecord?.fitnessScore ?: 0f, 10f, PrimeGreen),
        CategoryBreakdown("Nutrition & Macro Targets", scoreRecord?.nutritionScore ?: 0f, 10f, PrimeBlue),
        CategoryBreakdown("Health, Hydration & Sleep", scoreRecord?.healthScore ?: 5f, 10f, Color(0xFF80D8FF)),
        CategoryBreakdown("Appearance & Conditioning", scoreRecord?.appearanceScore ?: 9f, 10f, Color(0xFFF472B6)),
        CategoryBreakdown("Mindset & Recovery", scoreRecord?.mindsetScore ?: 8f, 10f, PrimePurple),
        CategoryBreakdown("Focus & Deep Work", scoreRecord?.focusScore ?: 0f, 10f, Color(0xFFFFB74D)),
        CategoryBreakdown("Discipline & Habits", scoreRecord?.disciplineScore ?: 0f, 10f, PrimeGreen),
        CategoryBreakdown("Career Execution", scoreRecord?.careerScore ?: 3.5f, 5f, Color(0xFFA78BFA)),
        CategoryBreakdown("Learning & Signal", scoreRecord?.learningScore ?: 4.5f, 5f, Color(0xFFFBBF24)),
        CategoryBreakdown("Finance & Resource Management", scoreRecord?.financeScore ?: 5f, 5f, Color(0xFF2DD4BF))
    )

    val explanations = scoreRecord?.explanationsJson?.split("|||")?.filter { it.isNotBlank() } ?: emptyList()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = PrimeSurface,
            border = BorderStroke(1.dp, PrimeBorder.copy(alpha = 0.3f)),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 24.dp)
                .testTag("score_breakdown_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(PrimePrimaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.QueryStats,
                                contentDescription = null,
                                tint = PrimeOnPrimaryContainer,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "PRIME SCORE ENGINE",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                ),
                                color = PrimeTextPrimary
                            )
                            Text(
                                text = "Transparent Mathematical Breakdown",
                                style = MaterialTheme.typography.bodySmall,
                                color = PrimeTextSecondary
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = PrimeTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Score Hero Banner
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = PrimeSurfaceVariant
                    ),
                    border = BorderStroke(1.dp, PrimeBorder.copy(alpha = 0.25f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (isBaselineMode) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = PrimePrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "Building your baseline...",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = PrimePrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$totalLoggedDays / 3 days required for fully calibrated multi-metric baseline.",
                                style = MaterialTheme.typography.bodySmall,
                                color = PrimeTextSecondary
                            )
                        } else {
                            Text(
                                text = "$totalScore",
                                style = MaterialTheme.typography.displayLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 44.sp
                                ),
                                color = PrimePrimary
                            )
                            Text(
                                text = when {
                                    totalScore >= 90 -> "APEX PERFORMANCE — OPTIMAL EXECUTION"
                                    totalScore >= 75 -> "HIGH OPERATIONAL CAPACITY"
                                    totalScore >= 60 -> "CALIBRATED & PROGRESSING"
                                    else -> "DISCIPLINE RECOVERY REQUIRED"
                                },
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = PrimePrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "WEIGHTED SUB-SCORES (100% TOTAL)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = PrimeTextSecondary
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(categories) { cat ->
                        val ratio = (cat.score / cat.maxWeight).coerceIn(0f, 1f)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(PrimeSurfaceVariant)
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = cat.name,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                    color = PrimeTextPrimary
                                )
                                Text(
                                    text = "${cat.score.toInt()} / ${cat.maxWeight.toInt()} pts",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = cat.color
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { ratio },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = cat.color,
                                trackColor = PrimeBorder.copy(alpha = 0.3f)
                            )
                        }
                    }

                    if (explanations.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "WHAT MOVED YOUR SCORE TODAY",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = PrimePrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                        items(explanations) { exp ->
                            Row(
                                verticalAlignment = Alignment.Top,
                                modifier = Modifier.padding(vertical = 3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = PrimePrimary,
                                    modifier = Modifier
                                        .size(16.dp)
                                        .padding(top = 2.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = exp,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = PrimeTextSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimePrimaryContainer,
                        contentColor = PrimeOnPrimaryContainer
                    )
                ) {
                    Text(
                        text = "DISMISS",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                }
            }
        }
    }
}

