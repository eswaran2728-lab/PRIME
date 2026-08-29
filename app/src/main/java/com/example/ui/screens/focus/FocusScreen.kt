package com.example.ui.screens.focus

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entities.FocusSessionEntity
import com.example.ui.theme.PrimeBorder
import com.example.ui.theme.PrimeCardBg
import com.example.ui.theme.PrimeOnPrimaryDark
import com.example.ui.theme.PrimePrimary
import com.example.ui.theme.PrimeSurface
import com.example.ui.theme.PrimeTextMuted
import com.example.ui.theme.PrimeTextSecondary

@Composable
fun FocusScreen(
    isTimerRunning: Boolean,
    remainingSeconds: Int,
    totalDurationMinutes: Int,
    mode: String,
    taskName: String,
    distractionsCount: Int,
    recentSessions: List<FocusSessionEntity>,
    onStartTimer: (mode: String, durationMinutes: Int, taskName: String) -> Unit,
    onPauseTimer: () -> Unit,
    onResumeTimer: () -> Unit,
    onCancelTimer: () -> Unit,
    onLogDistraction: (note: String) -> Unit,
    onCompleteSession: (taskName: String, rating: Int, notes: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedMode by remember { mutableStateOf(mode) }
    var customTaskName by remember { mutableStateOf(taskName) }
    var selectedDuration by remember { mutableIntStateOf(totalDurationMinutes) }
    var showDistractionDialog by remember { mutableStateOf(false) }
    var showCompletionDialog by remember { mutableStateOf(false) }

    val totalSeconds = totalDurationMinutes * 60
    val progress = if (totalSeconds > 0) (remainingSeconds.toFloat() / totalSeconds).coerceIn(0f, 1f) else 1f
    val displayMins = remainingSeconds / 60
    val displaySecs = remainingSeconds % 60
    val timeFormatted = "%02d:%02d".format(displayMins, displaySecs)

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
                    text = "DEEP FOCUS PROTOCOL",
                    style = MaterialTheme.typography.labelMedium,
                    color = PrimePrimary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Uninterrupted Execution Sprint",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Mode Preset Selector
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedDuration == 25,
                    onClick = {
                        selectedDuration = 25
                        selectedMode = "pomodoro"
                    },
                    label = { Text("25m Sprint") },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = PrimePrimary, selectedLabelColor = PrimeOnPrimaryDark)
                )
                FilterChip(
                    selected = selectedDuration == 50,
                    onClick = {
                        selectedDuration = 50
                        selectedMode = "extended"
                    },
                    label = { Text("50m Deep") },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = PrimePrimary, selectedLabelColor = PrimeOnPrimaryDark)
                )
                FilterChip(
                    selected = selectedDuration == 90,
                    onClick = {
                        selectedDuration = 90
                        selectedMode = "deep_work"
                    },
                    label = { Text("90m Ultra") },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = PrimePrimary, selectedLabelColor = PrimeOnPrimaryDark)
                )
            }
        }

        // Task Name Input
        item {
            OutlinedTextField(
                value = customTaskName,
                onValueChange = { customTaskName = it },
                label = { Text("Focus Objective / Deliverable") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimePrimary,
                    unfocusedBorderColor = PrimeBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Main Timer Card with Circular Ring
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("focus_timer_card"),
                colors = CardDefaults.cardColors(containerColor = PrimeCardBg),
                shape = RoundedCornerShape(20.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(PrimeBorder))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(220.dp)
                    ) {
                        Canvas(modifier = Modifier.size(200.dp)) {
                            // Background track
                            drawArc(
                                color = PrimeSurface,
                                startAngle = -90f,
                                sweepAngle = 360f,
                                useCenter = false,
                                style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                            )
                            // Foreground active arc
                            drawArc(
                                color = PrimePrimary,
                                startAngle = -90f,
                                sweepAngle = progress * 360f,
                                useCenter = false,
                                style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = timeFormatted,
                                style = MaterialTheme.typography.displayMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isTimerRunning) "FOCUS MODE ACTIVE" else "READY TO SPRINT",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isTimerRunning) PrimePrimary else PrimeTextMuted,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Controls Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (!isTimerRunning && remainingSeconds == totalSeconds) {
                            Button(
                                onClick = { onStartTimer(selectedMode, selectedDuration, customTaskName) },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimePrimary, contentColor = PrimeOnPrimaryDark),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("start_focus_button")
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Start Focus Session", fontWeight = FontWeight.Bold)
                            }
                        } else {
                            // Pause / Resume
                            IconButton(
                                onClick = { if (isTimerRunning) onPauseTimer() else onResumeTimer() },
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(PrimePrimary)
                            ) {
                                Icon(
                                    imageVector = if (isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Toggle Timer",
                                    tint = PrimeOnPrimaryDark
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            // Log Distraction
                            Button(
                                onClick = { showDistractionDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimeSurface, contentColor = Color(0xFFEF4444)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Flag, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Distraction ($distractionsCount)")
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            // Complete Early / Finish
                            IconButton(
                                onClick = { showCompletionDialog = true },
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF10B981))
                            ) {
                                Icon(Icons.Default.Check, contentDescription = "Complete Session", tint = Color.Black)
                            }
                        }
                    }
                }
            }
        }

        // Focus Session History
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Focus Sprints",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${recentSessions.sumOf { it.durationMinutes }} mins total",
                    style = MaterialTheme.typography.labelMedium,
                    color = PrimePrimary
                )
            }
        }

        if (recentSessions.isEmpty()) {
            item {
                Text("No focus sessions completed yet.", style = MaterialTheme.typography.bodyMedium, color = PrimeTextMuted)
            }
        } else {
            items(recentSessions, key = { it.id }) { session ->
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
                        Column(modifier = Modifier.weight(1f)) {
                            Text(session.taskName, style = MaterialTheme.typography.titleSmall, color = Color.White, fontWeight = FontWeight.Bold)
                            Text("${session.date} • ${session.mode}", style = MaterialTheme.typography.bodySmall, color = PrimeTextSecondary)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("${session.durationMinutes} mins", style = MaterialTheme.typography.titleMedium, color = PrimePrimary, fontWeight = FontWeight.Bold)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB800), modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text("${session.focusRating}/5", style = MaterialTheme.typography.labelSmall, color = PrimeTextSecondary)
                                if (session.distractionsCount > 0) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("(${session.distractionsCount} dist)", style = MaterialTheme.typography.labelSmall, color = Color(0xFFEF4444))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Log Distraction Dialog
    if (showDistractionDialog) {
        var note by remember { mutableStateOf("") }
        Dialog(onDismissRequest = { showDistractionDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = PrimeCardBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimeBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Log Distraction / Stray Thought", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
                    Text("Offload the distraction onto paper and immediately return to deep focus.", style = MaterialTheme.typography.bodySmall, color = PrimeTextSecondary)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        label = { Text("What pulled your attention?") },
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
                            onClick = { showDistractionDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = PrimeTextSecondary)
                        ) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                onLogDistraction(note)
                                showDistractionDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444), contentColor = Color.White)
                        ) {
                            Text("Log Distraction")
                        }
                    }
                }
            }
        }
    }

    // Session Completion Feedback Dialog
    if (showCompletionDialog) {
        var rating by remember { mutableIntStateOf(5) }
        var notes by remember { mutableStateOf("") }

        Dialog(onDismissRequest = { showCompletionDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = PrimeCardBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimeBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Focus Sprint Completed! 🎯", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Log your focus quality rating & output produced.", style = MaterialTheme.typography.bodySmall, color = PrimeTextSecondary)
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Focus Flow Quality: $rating / 5", style = MaterialTheme.typography.bodyMedium, color = Color.White)
                    Slider(
                        value = rating.toFloat(),
                        onValueChange = { rating = it.toInt() },
                        valueRange = 1f..5f,
                        steps = 3,
                        colors = SliderDefaults.colors(thumbColor = PrimePrimary, activeTrackColor = PrimePrimary)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Output / Key Win") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimePrimary,
                            unfocusedBorderColor = PrimeBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            onCompleteSession(customTaskName, rating, notes)
                            showCompletionDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimePrimary, contentColor = PrimeOnPrimaryDark),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Record Session & Claim XP")
                    }
                }
            }
        }
    }
}
