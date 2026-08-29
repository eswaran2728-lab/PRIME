package com.example.ui.screens.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PrimeBlue
import com.example.ui.theme.PrimeBorder
import com.example.ui.theme.PrimeCyan
import com.example.ui.theme.PrimeGreen
import com.example.ui.theme.PrimeOnPrimaryDark
import com.example.ui.theme.PrimeOrange
import com.example.ui.theme.PrimePrimary
import com.example.ui.theme.PrimePrimaryContainer
import com.example.ui.theme.PrimePurple
import com.example.ui.theme.PrimeSurface
import com.example.ui.theme.PrimeSurfaceVariant
import com.example.ui.theme.PrimeTextPrimary
import com.example.ui.theme.PrimeTextSecondary

@Composable
fun OnboardingScreen(
    onFinishOnboarding: (
        name: String,
        goal: String,
        calories: Int,
        protein: Float,
        sleepHours: Float,
        focusMinutes: Int,
        currency: String
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    var step by remember { mutableIntStateOf(0) }

    var userName by remember { mutableStateOf("Eswaran") }
    var userGoal by remember { mutableStateOf("Muscle Gain & Recomp") }
    var targetCalories by remember { mutableFloatStateOf(2400f) }
    var targetProtein by remember { mutableFloatStateOf(160f) }
    var targetSleepHours by remember { mutableFloatStateOf(8.0f) }
    var targetFocusMinutes by remember { mutableFloatStateOf(120f) }
    var selectedCurrency by remember { mutableStateOf("MYR") }

    val totalSteps = 4
    val stepProgress = ((step + 1).toFloat() / totalSteps.toFloat()).coerceIn(0f, 1f)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            // Header Progress
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "INITIAL PROTOCOL CALIBRATION",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp),
                        color = PrimePrimary
                    )
                    Text(
                        text = "Step ${step + 1} of $totalSteps",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = PrimeTextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { stepProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = PrimePrimary,
                    trackColor = PrimeBorder.copy(alpha = 0.4f)
                )
            }
        }

        item {
            when (step) {
                0 -> {
                    // Step 0: Welcome & Name
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = PrimeSurfaceVariant),
                        border = BorderStroke(1.dp, PrimeBorder.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth().testTag("onboarding_step_0")
                    ) {
                        Column(modifier = Modifier.padding(22.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(PrimePrimaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.MilitaryTech, contentDescription = null, tint = PrimePrimary, modifier = Modifier.size(28.dp))
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Welcome to PRIME OS",
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                color = PrimeTextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "An uncompromising 11-module life architecture system built for high-agency builders and operators.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = PrimeTextSecondary
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            OutlinedTextField(
                                value = userName,
                                onValueChange = { userName = it },
                                label = { Text("Your Operator Call Sign / Name") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PrimePrimary,
                                    unfocusedBorderColor = PrimeBorder
                                ),
                                modifier = Modifier.fillMaxWidth().testTag("onboarding_name_input")
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text("Default Operating Currency", style = MaterialTheme.typography.labelSmall, color = PrimeTextSecondary)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("MYR", "USD", "EUR", "SGD", "GBP").forEach { curr ->
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (selectedCurrency == curr) PrimePrimary else PrimeSurface,
                                        border = BorderStroke(1.dp, if (selectedCurrency == curr) PrimePrimary else PrimeBorder),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .clickable { selectedCurrency = curr }
                                    ) {
                                        Box(modifier = Modifier.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                                            Text(
                                                text = curr,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = if (selectedCurrency == curr) FontWeight.Bold else FontWeight.Normal
                                                ),
                                                color = if (selectedCurrency == curr) PrimeOnPrimaryDark else PrimeTextSecondary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // Step 1: Body & Nutrition Targets
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = PrimeSurfaceVariant),
                        border = BorderStroke(1.dp, PrimeBorder.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth().testTag("onboarding_step_1")
                    ) {
                        Column(modifier = Modifier.padding(22.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(PrimeGreen.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = PrimeGreen, modifier = Modifier.size(28.dp))
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Body & Nutrition Targets",
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                color = PrimeTextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Define your daily energy intake and lean mass protection targets.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = PrimeTextSecondary
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            Text("Daily Caloric Target: ${targetCalories.toInt()} kcal", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = PrimeTextPrimary)
                            Slider(
                                value = targetCalories,
                                onValueChange = { targetCalories = it },
                                valueRange = 1500f..3800f,
                                steps = 22,
                                colors = SliderDefaults.colors(thumbColor = PrimeGreen, activeTrackColor = PrimeGreen)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text("Daily Protein Target: ${targetProtein.toInt()} g", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = PrimeTextPrimary)
                            Slider(
                                value = targetProtein,
                                onValueChange = { targetProtein = it },
                                valueRange = 80f..250f,
                                steps = 16,
                                colors = SliderDefaults.colors(thumbColor = PrimePrimary, activeTrackColor = PrimePrimary)
                            )
                        }
                    }
                }

                2 -> {
                    // Step 2: Sleep & Recovery
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = PrimeSurfaceVariant),
                        border = BorderStroke(1.dp, PrimeBorder.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth().testTag("onboarding_step_2")
                    ) {
                        Column(modifier = Modifier.padding(22.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(PrimePurple.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.NightlightRound, contentDescription = null, tint = PrimePurple, modifier = Modifier.size(28.dp))
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Restorative Sleep Baseline",
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                color = PrimeTextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Cognitive recovery and testosterone synthesis depend on non-negotiable sleep windows.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = PrimeTextSecondary
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            Text("Target Sleep Duration: ${String.format("%.1f", targetSleepHours)} hours", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = PrimeTextPrimary)
                            Slider(
                                value = targetSleepHours,
                                onValueChange = { targetSleepHours = it },
                                valueRange = 6.0f..9.5f,
                                steps = 6,
                                colors = SliderDefaults.colors(thumbColor = PrimePurple, activeTrackColor = PrimePurple)
                            )
                        }
                    }
                }

                3 -> {
                    // Step 3: Deep Work & Focus
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = PrimeSurfaceVariant),
                        border = BorderStroke(1.dp, PrimeBorder.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth().testTag("onboarding_step_3")
                    ) {
                        Column(modifier = Modifier.padding(22.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(PrimeCyan.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Timer, contentDescription = null, tint = PrimeCyan, modifier = Modifier.size(28.dp))
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Deep Work Daily Quota",
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                color = PrimeTextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "High-impact cognitive blocks without notifications or task-switching.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = PrimeTextSecondary
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            Text("Daily Deep Focus Quota: ${targetFocusMinutes.toInt()} minutes", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = PrimeTextPrimary)
                            Slider(
                                value = targetFocusMinutes,
                                onValueChange = { targetFocusMinutes = it },
                                valueRange = 30f..300f,
                                steps = 8,
                                colors = SliderDefaults.colors(thumbColor = PrimeCyan, activeTrackColor = PrimeCyan)
                            )
                        }
                    }
                }
            }
        }

        // Navigation button
        item {
            Button(
                onClick = {
                    if (step < totalSteps - 1) {
                        step++
                    } else {
                        onFinishOnboarding(
                            userName,
                            userGoal,
                            targetCalories.toInt(),
                            targetProtein,
                            targetSleepHours,
                            targetFocusMinutes.toInt(),
                            selectedCurrency
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimePrimary),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("onboarding_next_button")
            ) {
                if (step < totalSteps - 1) {
                    Text("Continue", color = PrimeOnPrimaryDark, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = null, tint = PrimeOnPrimaryDark)
                } else {
                    Text("Initialize PRIME OS (+200 XP)", color = PrimeOnPrimaryDark, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.RocketLaunch, contentDescription = null, tint = PrimeOnPrimaryDark)
                }
            }
        }
    }
}
