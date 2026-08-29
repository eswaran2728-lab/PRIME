package com.example.ui.screens.sleep

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.DeviceHub
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.window.Dialog
import com.example.data.local.entities.SleepLogEntity
import com.example.ui.theme.PrimeBorder
import com.example.ui.theme.PrimeCardBg
import com.example.ui.theme.PrimeOnPrimaryDark
import com.example.ui.theme.PrimePrimary
import com.example.ui.theme.PrimeSurface
import com.example.ui.theme.PrimeTextMuted
import com.example.ui.theme.PrimeTextSecondary

@Composable
fun SleepScreen(
    todaySleep: SleepLogEntity?,
    recentSleepLogs: List<SleepLogEntity>,
    onLogSleep: (bedtime: String, wakeTime: String, durationMinutes: Int, quality: Int, energy: Int, notes: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showLogDialog by remember { mutableStateOf(false) }

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
                    text = "RECOVERY & CIRCADIAN RHYTHM",
                    style = MaterialTheme.typography.labelMedium,
                    color = PrimePrimary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Sleep & Biological Restoration",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Today's Sleep Overview Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("today_sleep_card"),
                colors = CardDefaults.cardColors(containerColor = PrimeCardBg),
                shape = RoundedCornerShape(16.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(PrimeBorder))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(PrimePrimary.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Bedtime, contentDescription = null, tint = PrimePrimary)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Last Night's Sleep", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
                                Text(todaySleep?.date ?: "Today", style = MaterialTheme.typography.bodySmall, color = PrimeTextSecondary)
                            }
                        }
                        IconButton(
                            onClick = { showLogDialog = true },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(PrimeSurface)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Log Sleep", tint = PrimePrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (todaySleep != null) {
                        val hours = todaySleep.durationMinutes / 60
                        val mins = todaySleep.durationMinutes % 60
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Total Duration", style = MaterialTheme.typography.bodySmall, color = PrimeTextSecondary)
                                Text("${hours}h ${mins}m", style = MaterialTheme.typography.headlineMedium, color = PrimePrimary, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Schedule", style = MaterialTheme.typography.bodySmall, color = PrimeTextSecondary)
                                Text("${todaySleep.bedtime} → ${todaySleep.wakeTime}", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Quality Badge
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(containerColor = PrimeSurface),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB800), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Quality: ${todaySleep.qualityRating}/5", style = MaterialTheme.typography.bodySmall, color = Color.White, fontWeight = FontWeight.Medium)
                                }
                            }
                            // Energy Badge
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(containerColor = PrimeSurface),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Bolt, contentDescription = null, tint = PrimePrimary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Energy: ${todaySleep.nextDayEnergy}/5", style = MaterialTheme.typography.bodySmall, color = Color.White, fontWeight = FontWeight.Medium)
                                }
                            }
                        }

                        if (todaySleep.notes.isNotBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "“${todaySleep.notes}”",
                                style = MaterialTheme.typography.bodySmall,
                                color = PrimeTextMuted
                            )
                        }
                    } else {
                        Text("No sleep logged yet for last night. Tap to record your sleep session.", style = MaterialTheme.typography.bodyMedium, color = PrimeTextSecondary)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { showLogDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimePrimary, contentColor = PrimeOnPrimaryDark),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Log Sleep")
                        }
                    }
                }
            }
        }

        // Wearable Integration Readiness Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = PrimeSurface),
                shape = RoundedCornerShape(12.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(PrimeBorder))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.DeviceHub, contentDescription = null, tint = PrimePrimary, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Telemetry Pipeline Ready", style = MaterialTheme.typography.labelLarge, color = Color.White, fontWeight = FontWeight.Bold)
                        Text(
                            "Schema calibrated for direct ingestion from Apple HealthKit, Oura Ring & Whoop straps.",
                            style = MaterialTheme.typography.bodySmall,
                            color = PrimeTextSecondary
                        )
                    }
                }
            }
        }

        // Weekly Sleep History
        item {
            Text(
                text = "Recent Sleep History",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        if (recentSleepLogs.isEmpty()) {
            item {
                Text("No past sleep logs found.", style = MaterialTheme.typography.bodyMedium, color = PrimeTextMuted)
            }
        } else {
            items(recentSleepLogs, key = { it.id }) { log ->
                val hours = log.durationMinutes / 60
                val mins = log.durationMinutes % 60
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = PrimeCardBg),
                    shape = RoundedCornerShape(12.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(PrimeBorder))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(log.date, style = MaterialTheme.typography.titleSmall, color = Color.White, fontWeight = FontWeight.Bold)
                            Text("${log.bedtime} → ${log.wakeTime}", style = MaterialTheme.typography.bodySmall, color = PrimeTextSecondary)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("${hours}h ${mins}m", style = MaterialTheme.typography.titleMedium, color = PrimePrimary, fontWeight = FontWeight.Bold)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB800), modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text("${log.qualityRating}/5", style = MaterialTheme.typography.labelSmall, color = PrimeTextSecondary)
                            }
                        }
                    }
                }
            }
        }
    }

    // Log Sleep Dialog
    if (showLogDialog) {
        var bedtime by remember { mutableStateOf(todaySleep?.bedtime ?: "22:30") }
        var wakeTime by remember { mutableStateOf(todaySleep?.wakeTime ?: "06:30") }
        var hoursDuration by remember { mutableFloatStateOf((todaySleep?.durationMinutes ?: 480) / 60f) }
        var quality by remember { mutableIntStateOf(todaySleep?.qualityRating ?: 4) }
        var energy by remember { mutableIntStateOf(todaySleep?.nextDayEnergy ?: 4) }
        var notes by remember { mutableStateOf(todaySleep?.notes ?: "") }

        Dialog(onDismissRequest = { showLogDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = PrimeCardBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimeBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Log Sleep Session", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = bedtime,
                            onValueChange = { bedtime = it },
                            label = { Text("Bedtime (e.g. 22:30)") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimePrimary,
                                unfocusedBorderColor = PrimeBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = wakeTime,
                            onValueChange = { wakeTime = it },
                            label = { Text("Wake (e.g. 06:30)") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimePrimary,
                                unfocusedBorderColor = PrimeBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Duration: ${hoursDuration.toInt()}h ${(hoursDuration % 1 * 60).toInt()}m (${(hoursDuration * 60).toInt()} mins)", style = MaterialTheme.typography.bodySmall, color = PrimeTextSecondary)
                    Slider(
                        value = hoursDuration,
                        onValueChange = { hoursDuration = it },
                        valueRange = 3f..12f,
                        steps = 17,
                        colors = SliderDefaults.colors(thumbColor = PrimePrimary, activeTrackColor = PrimePrimary)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Sleep Quality (1-5): $quality / 5", style = MaterialTheme.typography.bodySmall, color = PrimeTextSecondary)
                    Slider(
                        value = quality.toFloat(),
                        onValueChange = { quality = it.toInt() },
                        valueRange = 1f..5f,
                        steps = 3,
                        colors = SliderDefaults.colors(thumbColor = PrimePrimary, activeTrackColor = PrimePrimary)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Waking Energy (1-5): $energy / 5", style = MaterialTheme.typography.bodySmall, color = PrimeTextSecondary)
                    Slider(
                        value = energy.toFloat(),
                        onValueChange = { energy = it.toInt() },
                        valueRange = 1f..5f,
                        steps = 3,
                        colors = SliderDefaults.colors(thumbColor = PrimePrimary, activeTrackColor = PrimePrimary)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notes (e.g. cold room, magnesium)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimePrimary,
                            unfocusedBorderColor = PrimeBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = { showLogDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = PrimeTextSecondary)
                        ) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                onLogSleep(
                                    bedtime,
                                    wakeTime,
                                    (hoursDuration * 60).toInt(),
                                    quality,
                                    energy,
                                    notes
                                )
                                showLogDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimePrimary, contentColor = PrimeOnPrimaryDark)
                        ) {
                            Text("Save Sleep")
                        }
                    }
                }
            }
        }
    }
}
