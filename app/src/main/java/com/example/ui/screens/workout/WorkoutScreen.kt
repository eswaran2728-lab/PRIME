package com.example.ui.screens.workout

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.ExerciseEntity
import com.example.data.local.entities.WorkoutSessionEntity
import com.example.data.local.entities.WorkoutSetEntity
import com.example.data.local.entities.WorkoutTemplateEntity
import com.example.ui.theme.PrimeBlue
import com.example.ui.theme.PrimeGold
import com.example.ui.theme.PrimeGoldLight
import com.example.ui.theme.PrimeGreen
import com.example.ui.theme.PrimeOrange
import com.example.ui.theme.PrimeRed

@Composable
fun WorkoutScreen(
    templates: List<WorkoutTemplateEntity>,
    exercises: List<ExerciseEntity>,
    sessionsHistory: List<WorkoutSessionEntity>,
    activeSessionId: Long?,
    activeSessionSets: List<WorkoutSetEntity>,
    restTimerSeconds: Int,
    onStartWorkout: (String, Long?) -> Unit,
    onAddSet: (String, Int, Float, Int, Int?) -> Unit,
    onCompleteWorkout: (Int, Int, String) -> Unit,
    onCancelWorkout: () -> Unit,
    onStartRestTimer: (Int) -> Unit,
    onCancelRestTimer: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) } // 0 = Plans/Templates, 1 = Exercises, 2 = History
    val tabs = listOf("Templates", "Exercise Library", "History")

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "WORKOUT TRACKING",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 1.5.sp
            ),
            color = PrimeGold
        )
        Text(
            text = "Heavy volume overload and progressive strength tracking.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Active Session Banner if in progress
        if (activeSessionId != null) {
            ActiveWorkoutBanner(
                sets = activeSessionSets,
                restTimerSeconds = restTimerSeconds,
                onAddSet = onAddSet,
                onCompleteWorkout = onCompleteWorkout,
                onCancelWorkout = onCancelWorkout,
                onStartRestTimer = onStartRestTimer,
                onCancelRestTimer = onCancelRestTimer
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = PrimeGold,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = PrimeGold
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (selectedTab == index) PrimeGold else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (selectedTab) {
            0 -> TemplatesView(templates = templates, onStartTemplate = { onStartWorkout(it.name, it.id) })
            1 -> ExercisesView(exercises = exercises)
            2 -> HistoryView(sessions = sessionsHistory)
        }
    }
}

@Composable
fun ActiveWorkoutBanner(
    sets: List<WorkoutSetEntity>,
    restTimerSeconds: Int,
    onAddSet: (String, Int, Float, Int, Int?) -> Unit,
    onCompleteWorkout: (Int, Int, String) -> Unit,
    onCancelWorkout: () -> Unit,
    onStartRestTimer: (Int) -> Unit,
    onCancelRestTimer: () -> Unit
) {
    var selectedExercise by remember { mutableStateOf("Barbell Bench Press") }
    var weightInput by remember { mutableStateOf("80") }
    var repsInput by remember { mutableStateOf("8") }
    var rpeInput by remember { mutableStateOf("8") }
    var workoutDurationInput by remember { mutableStateOf("50") }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = CircleShape, color = PrimeRed.copy(alpha = 0.15f), modifier = Modifier.size(10.dp)) {}
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ACTIVE WORKOUT SESSION",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black),
                        color = PrimeRed
                    )
                }

                // Rest Timer Pill
                if (restTimerSeconds > 0) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = PrimeGold.copy(alpha = 0.2f),
                        modifier = Modifier.clickable { onCancelRestTimer() }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.Timer, contentDescription = null, tint = PrimeGold, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Rest: ${restTimerSeconds}s",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = PrimeGold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quick rest presets
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Rest Timer:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                listOf(30, 60, 90, 120).forEach { s ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onStartRestTimer(s) }
                    ) {
                        Text(
                            text = "${s}s",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = PrimeGold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sets logged list
            if (sets.isNotEmpty()) {
                Text("Logged Sets (${sets.size}):", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(4.dp))
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    sets.forEach { set ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Set ${set.setNumber}: ${set.exerciseName}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${set.weightKg.toInt()} kg × ${set.reps} reps (RPE ${set.rpe ?: 8})",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = PrimeGreen
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Quick Add Set Form
            Text("Log Next Set:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = weightInput,
                    onValueChange = { weightInput = it },
                    label = { Text("Weight (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = repsInput,
                    onValueChange = { repsInput = it },
                    label = { Text("Reps") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = rpeInput,
                    onValueChange = { rpeInput = it },
                    label = { Text("RPE (1-10)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            FilledTonalButton(
                onClick = {
                    val weight = weightInput.toFloatOrNull() ?: 0f
                    val reps = repsInput.toIntOrNull() ?: 0
                    val rpe = rpeInput.toIntOrNull() ?: 8
                    val nextSetNum = sets.size + 1
                    onAddSet(selectedExercise, nextSetNum, weight, reps, rpe)
                },
                colors = ButtonDefaults.filledTonalButtonColors(containerColor = PrimeGold, contentColor = Color.Black),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("LOG COMPLETED SET", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilledTonalButton(
                    onClick = {
                        val duration = workoutDurationInput.toIntOrNull() ?: 45
                        val avgRpe = rpeInput.toIntOrNull() ?: 8
                        onCompleteWorkout(duration, avgRpe, "Session finished strong.")
                    },
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = PrimeGreen, contentColor = Color.Black),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("FINISH WORKOUT", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                }

                OutlinedButton(
                    onClick = onCancelWorkout,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("CANCEL", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = PrimeRed)
                }
            }
        }
    }
}

@Composable
fun TemplatesView(
    templates: List<WorkoutTemplateEntity>,
    onStartTemplate: (WorkoutTemplateEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "STARTER & CUSTOM TEMPLATES",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (templates.isEmpty()) {
            item {
                Text(
                    text = "No workout templates found.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(templates) { template ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = template.name,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = PrimeGold.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = template.category,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = PrimeGold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = template.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Est. ${template.estimatedDurationMinutes} mins",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            FilledTonalButton(
                                onClick = { onStartTemplate(template) },
                                colors = ButtonDefaults.filledTonalButtonColors(containerColor = PrimeGold, contentColor = Color.Black)
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("START", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                            }
                        }
                    }
                }
            }
        }
        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
fun ExercisesView(
    exercises: List<ExerciseEntity>
) {
    var selectedMuscleGroup by remember { mutableStateOf("All") }
    val muscleGroups = listOf("All", "Chest", "Back", "Legs", "Shoulders", "Arms", "Core")

    Column(modifier = Modifier.fillMaxSize()) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(muscleGroups) { group ->
                FilterChip(
                    selected = selectedMuscleGroup == group,
                    onClick = { selectedMuscleGroup = group },
                    label = { Text(group) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimeGold,
                        selectedLabelColor = Color.Black
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        val filtered = if (selectedMuscleGroup == "All") exercises else exercises.filter { it.muscleGroup == selectedMuscleGroup }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(filtered) { exercise ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = exercise.name,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${exercise.muscleGroup} · ${exercise.equipment}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (exercise.personalRecordWeightKg > 0) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = PrimeGold.copy(alpha = 0.15f)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(Icons.Default.EmojiEvents, contentDescription = "PR", tint = PrimeGold, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${exercise.personalRecordWeightKg.toInt()} kg",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = PrimeGold
                                    )
                                }
                            }
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
fun HistoryView(
    sessions: List<WorkoutSessionEntity>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (sessions.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No workouts logged yet.",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Start and finish your first session to build your training history.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(sessions) { session ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = session.templateName,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${session.date} · ${session.durationMinutes} mins · ${session.totalSetsCompleted} sets",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${session.totalVolumeKg.toInt()} kg",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = PrimeGold
                            )
                            Text(
                                text = "RPE ${session.rpe}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}
