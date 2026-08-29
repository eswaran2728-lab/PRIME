package com.example.ui.screens.body

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entities.BodyMeasurementEntity
import com.example.data.local.entities.BodyPhotoEntity
import com.example.ui.theme.PrimeBlue
import com.example.ui.theme.PrimeCyan
import com.example.ui.theme.PrimeGold
import com.example.ui.theme.PrimeGreen
import com.example.ui.theme.PrimeOrange
import com.example.ui.theme.PrimePurple

@Composable
fun BodyScreen(
    latestMeasurement: BodyMeasurementEntity?,
    allMeasurements: List<BodyMeasurementEntity>,
    photos: List<BodyPhotoEntity>,
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
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "BODY COMPOSITION",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.5.sp
                        ),
                        color = PrimeGold
                    )
                    Text(
                        text = "Physical conditioning metrics & US Navy body fat.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                FilledTonalButton(
                    onClick = { showLogDialog = true },
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = PrimeGold, contentColor = Color.Black)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("LOG", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                }
            }
        }

        // Hero Metric Cards
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "${currentWeight} kg",
                                style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Black),
                                color = PrimeGold
                            )
                            Text(
                                text = "Current Scale Weight",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = PrimeGreen.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "BF: ${String.format("%.1f", currentBf)}%",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = PrimeGreen,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "BMI: ${String.format("%.1f", currentBmi)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Girth Measurements Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        BodyGirthPill(label = "Waist", value = latestMeasurement?.waistCm, unit = "cm", color = PrimeOrange)
                        BodyGirthPill(label = "Chest", value = latestMeasurement?.chestCm, unit = "cm", color = PrimeBlue)
                        BodyGirthPill(label = "Arms", value = latestMeasurement?.armsCm, unit = "cm", color = PrimePurple)
                        BodyGirthPill(label = "Thighs", value = latestMeasurement?.thighsCm, unit = "cm", color = PrimeCyan)
                    }
                }
            }
        }

        // Physique Timeline & Photos
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "PHYSIQUE TIMELINE (FRONT / SIDE / BACK)",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Take consistent weekly progress photos in standardized lighting.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Front Pose", "Side Profile", "Back Lat Spread").forEach { pose ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(90.dp)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(pose, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Historical Log Table
        item {
            Text(
                text = "LOGGED MEASUREMENT HISTORY",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (allMeasurements.isEmpty()) {
            item {
                Text("No measurements logged yet.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            items(allMeasurements) { m ->
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
                        Column {
                            Text(
                                text = "${m.date} — ${m.weightKg} kg",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Waist: ${m.waistCm ?: "-"}cm · Chest: ${m.chestCm ?: "-"}cm · Arms: ${m.armsCm ?: "-"}cm",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (m.bodyFatEstimate != null) {
                            Text(
                                text = "${String.format("%.1f", m.bodyFatEstimate)}% BF",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = PrimeGreen
                            )
                        }
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

@Composable
fun BodyGirthPill(
    label: String,
    value: Float?,
    unit: String,
    color: Color
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                text = if (value != null) "${value.toInt()}$unit" else "--",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = color
            )
        }
    }
}

@Composable
fun LogMeasurementDialog(
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
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
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
                    Text("LOG BODY METRICS", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black), color = PrimeGold)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
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

                FilledTonalButton(
                    onClick = {
                        val w = weight.toFloatOrNull() ?: 80f
                        val h = height.toFloatOrNull() ?: 180f
                        onSave(w, h, waist.toFloatOrNull(), chest.toFloatOrNull(), arms.toFloatOrNull(), thighs.toFloatOrNull(), neck.toFloatOrNull(), "Manual entry")
                    },
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = PrimeGold, contentColor = Color.Black),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("SAVE MEASUREMENT", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                }
            }
        }
    }
}
