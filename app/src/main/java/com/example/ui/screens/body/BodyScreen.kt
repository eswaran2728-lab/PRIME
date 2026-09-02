package com.example.ui.screens.body

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entities.BodyMeasurementEntity
import com.example.data.local.entities.BodyPhotoEntity
import com.example.data.local.entities.WorkoutSessionEntity
import com.example.data.local.entities.WorkoutSetEntity
import com.example.ui.components.BlueprintFrame
import com.example.ui.components.SquareTag
import com.example.ui.components.SquareTagVariant
import com.example.ui.theme.PrimeBackground
import com.example.ui.theme.PrimePrimary
import com.example.ui.theme.PrimeSurface
import com.example.ui.theme.PrimeSurfaceVariant
import com.example.ui.theme.PrimeTextPrimary
import com.example.ui.theme.PrimeTextSecondary

@Composable
fun BodyScreen(
    latestMeasurement: BodyMeasurementEntity?,
    allMeasurements: List<BodyMeasurementEntity>,
    photos: List<BodyPhotoEntity>,
    todayWorkoutSession: WorkoutSessionEntity?,
    todayWorkoutSets: List<WorkoutSetEntity>,
    onLogMeasurement: (Float, Float, Float?, Float?, Float?, Float?, Float?, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showLogDialog by remember { mutableStateOf(false) }

    val currentWeight = latestMeasurement?.weightKg ?: 82.5f
    val currentBmi = latestMeasurement?.bmi ?: 24.8f
    val currentBf = latestMeasurement?.bodyFatEstimate ?: 13.8f

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "BODY COMPOSITION",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black, letterSpacing = 1.5.sp),
                    color = PrimePrimary
                )
                Row(
                    modifier = Modifier
                        .clickable { showLogDialog = true }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = PrimePrimary, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("LOG", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = PrimePrimary)
                }
            }
        }

        // Hero: weight, BF, BMI, girths — blueprint frame.
        item {
            BlueprintFrame(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "$currentWeight",
                                    style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
                                    color = PrimePrimary
                                )
                                Text("kg", style = MaterialTheme.typography.titleMedium, color = PrimePrimary, modifier = Modifier.padding(bottom = 4.dp))
                            }
                            Text(
                                text = "−0.6kg this week",
                                style = MaterialTheme.typography.bodySmall,
                                color = PrimeTextSecondary
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            SquareTag(text = "BF ${fmt1(currentBf)}%", variant = SquareTagVariant.Accent)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("BMI ${fmt1(currentBmi)}", style = MaterialTheme.typography.bodySmall, color = PrimeTextSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        GirthCell(label = "Waist", value = latestMeasurement?.waistCm, unit = "cm", modifier = Modifier.weight(1f))
                        GirthCell(label = "Chest", value = latestMeasurement?.chestCm, unit = "cm", modifier = Modifier.weight(1f))
                        GirthCell(label = "Arms", value = latestMeasurement?.armsCm, unit = "cm", modifier = Modifier.weight(1f))
                        GirthCell(label = "Thighs", value = latestMeasurement?.thighsCm, unit = "cm", modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // TODAY'S WORKOUT — real data from today's session, if any.
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PrimeSurface)
                    .padding(14.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "TODAY'S WORKOUT",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp),
                        color = PrimeTextSecondary
                    )
                    if (todayWorkoutSession != null) {
                        SquareTag(text = todayWorkoutSession.templateName, variant = SquareTagVariant.Outline)
                    }
                }

                val grouped = todayWorkoutSets.groupBy { it.exerciseName }
                if (todayWorkoutSession == null || grouped.isEmpty()) {
                    Text(
                        text = "No workout logged today.",
                        style = MaterialTheme.typography.bodySmall,
                        color = PrimeTextSecondary,
                        modifier = Modifier.padding(top = 10.dp)
                    )
                } else {
                    grouped.forEach { (name, sets) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(name, style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.5.sp), color = PrimeTextPrimary)
                            Text(
                                text = "${sets.size}×${sets.last().reps} · ${sets.maxOf { it.weightKg }} kg",
                                style = MaterialTheme.typography.labelMedium,
                                color = PrimePrimary
                            )
                        }
                    }
                }
            }
        }

        // Physique timeline placeholders
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PrimeSurface)
                    .padding(14.dp)
            ) {
                Text(
                    text = "PHYSIQUE TIMELINE",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp),
                    color = PrimeTextSecondary
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Front", "Side", "Back").forEach { pose ->
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .height(84.dp)
                                .background(PrimeBackground),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, tint = PrimeTextSecondary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(pose, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = PrimeTextSecondary)
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "LOGGED MEASUREMENT HISTORY",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = PrimeTextSecondary
            )
        }

        if (allMeasurements.isEmpty()) {
            item {
                Text("No measurements logged yet.", style = MaterialTheme.typography.bodyMedium, color = PrimeTextSecondary)
            }
        } else {
            items(allMeasurements) { m ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PrimeSurface)
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${m.date} — ${m.weightKg} kg",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = PrimeTextPrimary
                        )
                        Text(
                            text = "Waist: ${m.waistCm ?: "-"}cm · Chest: ${m.chestCm ?: "-"}cm · Arms: ${m.armsCm ?: "-"}cm",
                            style = MaterialTheme.typography.bodySmall,
                            color = PrimeTextSecondary
                        )
                    }
                    if (m.bodyFatEstimate != null) {
                        Text(
                            text = "${fmt1(m.bodyFatEstimate)}% BF",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = PrimePrimary
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }

    if (showLogDialog) {
        LogMeasurementDialog(
            latest = latestMeasurement,
            onDismiss = { showLogDialog = false },
            onSave = { w, h, waist, chest, arms, thighs, neck, notes ->
                onLogMeasurement(w, h, waist, chest, arms, thighs, neck, notes)
                showLogDialog = false
            }
        )
    }
}

private fun fmt1(v: Float): String = String.format(java.util.Locale.US, "%.1f", v)

@Composable
private fun GirthCell(label: String, value: Float?, unit: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(PrimeSurfaceVariant)
            .padding(vertical = 8.dp, horizontal = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = PrimeTextSecondary)
        Text(
            text = if (value != null) "${value.toInt()}$unit" else "--",
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontSize = 13.sp),
            color = PrimeTextPrimary,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

@Composable
private fun LogMeasurementDialog(
    latest: BodyMeasurementEntity?,
    onDismiss: () -> Unit,
    onSave: (Float, Float, Float?, Float?, Float?, Float?, Float?, String) -> Unit
) {
    var weight by remember { mutableStateOf(latest?.weightKg?.toString() ?: "82.5") }
    var height by remember { mutableStateOf(latest?.heightCm?.toString() ?: "182") }
    var waist by remember { mutableStateOf(latest?.waistCm?.toString() ?: "81") }
    var chest by remember { mutableStateOf(latest?.chestCm?.toString() ?: "106") }
    var arms by remember { mutableStateOf(latest?.armsCm?.toString() ?: "39") }
    var thighs by remember { mutableStateOf(latest?.thighsCm?.toString() ?: "61") }
    var neck by remember { mutableStateOf(latest?.neckCm?.toString() ?: "40") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(0.dp),
            color = PrimeSurface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("LOG BODY METRICS", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black), color = PrimePrimary)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = PrimeTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = weight, onValueChange = { weight = it }, label = { Text("Weight (kg)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f), singleLine = true)
                    OutlinedTextField(value = height, onValueChange = { height = it }, label = { Text("Height (cm)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f), singleLine = true)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = waist, onValueChange = { waist = it }, label = { Text("Waist (cm)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f), singleLine = true)
                    OutlinedTextField(value = neck, onValueChange = { neck = it }, label = { Text("Neck (cm)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f), singleLine = true)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = chest, onValueChange = { chest = it }, label = { Text("Chest (cm)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f), singleLine = true)
                    OutlinedTextField(value = arms, onValueChange = { arms = it }, label = { Text("Arms (cm)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f), singleLine = true)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PrimePrimary)
                        .clickable {
                            val w = weight.toFloatOrNull() ?: 80f
                            val h = height.toFloatOrNull() ?: 180f
                            onSave(w, h, waist.toFloatOrNull(), chest.toFloatOrNull(), arms.toFloatOrNull(), thighs.toFloatOrNull(), neck.toFloatOrNull(), "Manual entry")
                        }
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("SAVE MEASUREMENT", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = PrimeBackground)
                }
            }
        }
    }
}
