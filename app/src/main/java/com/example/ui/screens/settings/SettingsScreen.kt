package com.example.ui.screens.settings

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.ProfileEntity
import com.example.ui.theme.PrimeGold
import com.example.ui.theme.PrimeGreen
import com.example.ui.theme.PrimeRed

@Composable
fun SettingsScreen(
    profile: ProfileEntity?,
    onUpdateProfile: (ProfileEntity) -> Unit,
    onExportData: () -> String,
    onWipeData: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var name by remember(profile) { mutableStateOf(profile?.name ?: "Eswaran") }
    var title by remember(profile) { mutableStateOf(profile?.title ?: "Apex Operator") }
    var goal by remember(profile) { mutableStateOf(profile?.primaryGoal ?: "Muscle Gain & Peak Performance") }
    var targetCalories by remember(profile) { mutableStateOf(profile?.targetCalories?.toString() ?: "2400") }
    var targetProtein by remember(profile) { mutableStateOf(profile?.targetProteinGrams?.toInt()?.toString() ?: "160") }
    var targetWater by remember(profile) { mutableStateOf(profile?.targetWaterMl?.toString() ?: "3000") }

    var showWipeConfirmDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "SYSTEM & OPERATING PROFILE",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp
                ),
                color = PrimeGold
            )
            Text(
                text = "Single-user private encryption & configuration parameters.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Profile Configuration
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "OPERATOR PROFILE",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Operator Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Rank / Designation") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = goal, onValueChange = { goal = it }, label = { Text("Primary Physical & Mental Objective") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Daily Targets:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = PrimeGold)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = targetCalories, onValueChange = { targetCalories = it }, label = { Text("Calories (kcal)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f), singleLine = true)
                        OutlinedTextField(value = targetProtein, onValueChange = { targetProtein = it }, label = { Text("Protein (g)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f), singleLine = true)
                        OutlinedTextField(value = targetWater, onValueChange = { targetWater = it }, label = { Text("Water (ml)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f), singleLine = true)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    FilledTonalButton(
                        onClick = {
                            val updated = (profile ?: ProfileEntity()).copy(
                                name = name,
                                title = title,
                                primaryGoal = goal,
                                targetCalories = targetCalories.toIntOrNull() ?: 2400,
                                targetProteinGrams = targetProtein.toFloatOrNull() ?: 160f,
                                targetWaterMl = targetWater.toIntOrNull() ?: 3000
                            )
                            onUpdateProfile(updated)
                        },
                        colors = ButtonDefaults.filledTonalButtonColors(containerColor = PrimeGold, contentColor = Color.Black),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("SAVE PROFILE & TARGETS", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }
        }

        // Data Management Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "DATA & PRIVACY VAULT",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "All data is securely persisted on-device with single-user isolation. You retain 100% data ownership.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedButton(
                        onClick = {
                            val json = onExportData()
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("PRIME Data Export", json)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Full PRIME data exported to clipboard (JSON)!", Toast.LENGTH_LONG).show()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, tint = PrimeGold, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("EXPORT ALL DATA (JSON)", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = PrimeGold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = { showWipeConfirmDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.DeleteForever, contentDescription = null, tint = PrimeRed, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("WIPE DATA & FACTORY RESET", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = PrimeRed)
                    }
                }
            }
        }

        // About & Version Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "PRIME OPERATING SYSTEM",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black, letterSpacing = 1.sp),
                        color = PrimeGold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Phase 1: High-Performance Single-User Architecture\nVersion 1.0.0 Alpha · Zero Latency Local Engine",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }

    if (showWipeConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showWipeConfirmDialog = false },
            title = {
                Text(
                    text = "Confirm Full Data Wipe",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = PrimeRed
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete all logged workouts, meals, body measurements, and habits? This will reset the app back to a clean baseline.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onWipeData()
                        showWipeConfirmDialog = false
                    }
                ) {
                    Text("YES, WIPE ALL DATA", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = PrimeRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showWipeConfirmDialog = false }) {
                    Text("CANCEL", style = MaterialTheme.typography.labelLarge)
                }
            }
        )
    }
}
