package com.example.ui.screens.review

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.WeeklyReviewRecordEntity
import com.example.ui.theme.PrimeBlue
import com.example.ui.theme.PrimeBorder
import com.example.ui.theme.PrimeGreen
import com.example.ui.theme.PrimeOnPrimaryContainer
import com.example.ui.theme.PrimeOnPrimaryDark
import com.example.ui.theme.PrimeOrange
import com.example.ui.theme.PrimePrimary
import com.example.ui.theme.PrimePrimaryContainer
import com.example.ui.theme.PrimePurple
import com.example.ui.theme.PrimeRed
import com.example.ui.theme.PrimeSurface
import com.example.ui.theme.PrimeSurfaceVariant
import com.example.ui.theme.PrimeTextPrimary
import com.example.ui.theme.PrimeTextSecondary

@Composable
fun WeeklyReviewScreen(
    weeklyReviews: List<WeeklyReviewRecordEntity>,
    onGenerateWeeklyReview: () -> Unit,
    modifier: Modifier = Modifier
) {
    val latestReview = weeklyReviews.firstOrNull()

    val wins = latestReview?.winsJson?.split("|||")?.filter { it.isNotBlank() } ?: listOf(
        "Maintained 100% daily macro protein target across 6 training days.",
        "Clocked 14.5 hours of deep focus work with minimal context-switching.",
        "Consistent evening wind-down routine resulting in 8.2h avg restorative sleep."
    )

    val weaknesses = latestReview?.weaknessesJson?.split("|||")?.filter { it.isNotBlank() } ?: listOf(
        "Late-night screen exposure on Thursday delayed sleep onset by 45 minutes.",
        "Hydration dipped below 2,500ml on non-training recovery days.",
        "Skipped mobility / stretching work following heavy lower body session."
    )

    val recommendations = latestReview?.recommendationsJson?.split("|||")?.filter { it.isNotBlank() } ?: listOf(
        "Deploy a hard 10:00 PM digital curfew with grayscale mode enabled.",
        "Pre-fill 1L water canister immediately upon waking.",
        "Program a mandatory 12-minute hip & ankle decompression block post-workout."
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Card
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = PrimeSurfaceVariant),
                border = BorderStroke(1.dp, PrimeBorder.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth().testTag("weekly_review_hero_card")
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "AUTOMATED DEBRIEF",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.5.sp
                                ),
                                color = PrimePrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = latestReview?.weekLabel ?: "Weekly Performance Review",
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                color = PrimeTextPrimary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(PrimePrimaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.RateReview,
                                contentDescription = null,
                                tint = PrimeOnPrimaryContainer,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Big Score Card
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = PrimeSurface,
                        border = BorderStroke(1.dp, PrimeBorder.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Weekly Protocol Score", style = MaterialTheme.typography.labelSmall, color = PrimeTextSecondary)
                                Text(
                                    text = "${latestReview?.overallScorePercent ?: 88}%",
                                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                                    color = PrimePrimary
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.TrendingUp, contentDescription = null, tint = PrimeGreen, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Apex: ${latestReview?.strongestCategory ?: "Nutrition & Fitness"}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = PrimeGreen
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.TrendingDown, contentDescription = null, tint = PrimeOrange, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Focus Area: ${latestReview?.weakestCategory ?: "Sleep Regularity"}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = PrimeOrange
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onGenerateWeeklyReview,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimePrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("regenerate_review_button")
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = PrimeOnPrimaryDark, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Re-Analyze Week with PRIME AI", color = PrimeOnPrimaryDark, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 3 Key Wins
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = PrimeSurfaceVariant),
                border = BorderStroke(1.dp, PrimeGreen.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PrimeGreen, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "3 Major Protocol Wins",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = PrimeGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    wins.forEachIndexed { index, win ->
                        Row(
                            modifier = Modifier.padding(vertical = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text("${index + 1}.", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = PrimeGreen)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(win, style = MaterialTheme.typography.bodyMedium, color = PrimeTextPrimary)
                        }
                    }
                }
            }
        }

        // 3 Weaknesses / Friction Points
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = PrimeSurfaceVariant),
                border = BorderStroke(1.dp, PrimeOrange.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = PrimeOrange, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "3 Friction Points & Deviations",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = PrimeOrange
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    weaknesses.forEachIndexed { index, weakness ->
                        Row(
                            modifier = Modifier.padding(vertical = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text("${index + 1}.", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = PrimeOrange)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(weakness, style = MaterialTheme.typography.bodyMedium, color = PrimeTextPrimary)
                        }
                    }
                }
            }
        }

        // 3 Tactical Recommendations
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = PrimeSurfaceVariant),
                border = BorderStroke(1.dp, PrimeBlue.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lightbulb, contentDescription = null, tint = PrimeBlue, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "3 High-Leverage Directives For Next Week",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = PrimeBlue
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    recommendations.forEachIndexed { index, rec ->
                        Row(
                            modifier = Modifier.padding(vertical = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text("${index + 1}.", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = PrimeBlue)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(rec, style = MaterialTheme.typography.bodyMedium, color = PrimeTextPrimary)
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
