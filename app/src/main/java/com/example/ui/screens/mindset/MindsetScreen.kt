package com.example.ui.screens.mindset

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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.SentimentSatisfiedAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import com.example.data.local.entities.JournalEntryEntity
import com.example.data.local.entities.MindsetLogEntity
import com.example.ui.theme.PrimeBorder
import com.example.ui.theme.PrimeCardBg
import com.example.ui.theme.PrimeOnPrimaryDark
import com.example.ui.theme.PrimePrimary
import com.example.ui.theme.PrimeSurface
import com.example.ui.theme.PrimeTextMuted
import com.example.ui.theme.PrimeTextSecondary

@Composable
fun MindsetScreen(
    todayMindset: MindsetLogEntity?,
    recentMindsetLogs: List<MindsetLogEntity>,
    todayJournal: JournalEntryEntity?,
    recentJournalEntries: List<JournalEntryEntity>,
    onLogMindset: (mood: Int, energy: Int, stress: Int, confidence: Int, focus: Int, label: String) -> Unit,
    onSaveJournal: (accomplished: String, obstacles: String, distractions: String, learnings: String, improvements: String, freeform: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Calibration Sliders, 1: Journal Prompts, 2: History

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
                    text = "PSYCHOLOGICAL FORTITUDE",
                    style = MaterialTheme.typography.labelMedium,
                    color = PrimePrimary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Mindset & Daily Reflection",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Navigation Tab
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = PrimeSurface,
                contentColor = PrimePrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = PrimePrimary
                    )
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, PrimeBorder, RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Daily Scales", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Stoic Journal", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Reflections", fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal) }
                )
            }
        }

        if (selectedTab == 0) {
            // Daily Scales Calibration
            item {
                MindsetCalibrationSection(
                    initialLog = todayMindset,
                    onSave = onLogMindset
                )
            }
        } else if (selectedTab == 1) {
            // Stoic 5-Prompt Journaling
            item {
                JournalPromptsSection(
                    initialJournal = todayJournal,
                    onSave = onSaveJournal
                )
            }
        } else {
            // History
            item {
                Text(
                    text = "Recent Journal Archives",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            if (recentJournalEntries.isEmpty()) {
                item {
                    Text("No journal archives recorded yet.", style = MaterialTheme.typography.bodyMedium, color = PrimeTextMuted)
                }
            } else {
                items(recentJournalEntries, key = { it.id }) { entry ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = PrimeCardBg),
                        shape = RoundedCornerShape(12.dp),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(PrimeBorder))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(entry.date, style = MaterialTheme.typography.titleSmall, color = PrimePrimary, fontWeight = FontWeight.Bold)
                                if (entry.tags.isNotBlank()) {
                                    Text(entry.tags, style = MaterialTheme.typography.labelSmall, color = PrimeTextSecondary)
                                }
                            }
                            if (entry.accomplishedText.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("🎯 Accomplished:", style = MaterialTheme.typography.labelMedium, color = Color.White, fontWeight = FontWeight.SemiBold)
                                Text(entry.accomplishedText, style = MaterialTheme.typography.bodySmall, color = PrimeTextSecondary)
                            }
                            if (entry.learningsText.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("💡 Key Learning:", style = MaterialTheme.typography.labelMedium, color = Color.White, fontWeight = FontWeight.SemiBold)
                                Text(entry.learningsText, style = MaterialTheme.typography.bodySmall, color = PrimeTextSecondary)
                            }
                            if (entry.freeformText.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("✍️ Note:", style = MaterialTheme.typography.labelMedium, color = Color.White, fontWeight = FontWeight.SemiBold)
                                Text(entry.freeformText, style = MaterialTheme.typography.bodySmall, color = PrimeTextSecondary)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MindsetCalibrationSection(
    initialLog: MindsetLogEntity?,
    onSave: (mood: Int, energy: Int, stress: Int, confidence: Int, focus: Int, label: String) -> Unit
) {
    var mood by remember(initialLog) { mutableFloatStateOf(initialLog?.moodRating?.toFloat() ?: 8f) }
    var energy by remember(initialLog) { mutableFloatStateOf(initialLog?.energyRating?.toFloat() ?: 8f) }
    var stress by remember(initialLog) { mutableFloatStateOf(initialLog?.stressRating?.toFloat() ?: 3f) }
    var confidence by remember(initialLog) { mutableFloatStateOf(initialLog?.confidenceRating?.toFloat() ?: 9f) }
    var focus by remember(initialLog) { mutableFloatStateOf(initialLog?.focusRating?.toFloat() ?: 8f) }
    var label by remember(initialLog) { mutableStateOf(initialLog?.moodLabel ?: "Focused & Resolute") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("mindset_calibration_card"),
        colors = CardDefaults.cardColors(containerColor = PrimeCardBg),
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(PrimeBorder))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Daily State Scales (1-10)", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            // Mood Slider
            SliderMetric(label = "Mood Rating", value = mood, onValueChange = { mood = it }, valueColor = PrimePrimary)
            // Energy Slider
            SliderMetric(label = "Physical Energy", value = energy, onValueChange = { energy = it }, valueColor = Color(0xFFF59E0B))
            // Stress Slider
            SliderMetric(label = "Stress Level", value = stress, onValueChange = { stress = it }, valueColor = if (stress > 6) Color(0xFFEF4444) else PrimePrimary)
            // Confidence Slider
            SliderMetric(label = "Confidence & Drive", value = confidence, onValueChange = { confidence = it }, valueColor = Color(0xFF10B981))
            // Focus Slider
            SliderMetric(label = "Mental Clarity & Focus", value = focus, onValueChange = { focus = it }, valueColor = Color(0xFF8B5CF6))

            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = label,
                onValueChange = { label = it },
                label = { Text("State Label (e.g. Unstoppable, Calm, In The Flow)") },
                singleLine = true,
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
                    onSave(mood.toInt(), energy.toInt(), stress.toInt(), confidence.toInt(), focus.toInt(), label)
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimePrimary, contentColor = PrimeOnPrimaryDark),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Calibrate Mindset (+30 XP)")
            }
        }
    }
}

@Composable
private fun SliderMetric(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueColor: Color
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.bodyMedium, color = Color.White)
            Text("${value.toInt()}/10", style = MaterialTheme.typography.titleSmall, color = valueColor, fontWeight = FontWeight.Bold)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 1f..10f,
            steps = 8,
            colors = SliderDefaults.colors(thumbColor = valueColor, activeTrackColor = valueColor)
        )
    }
}

@Composable
private fun JournalPromptsSection(
    initialJournal: JournalEntryEntity?,
    onSave: (accomplished: String, obstacles: String, distractions: String, learnings: String, improvements: String, freeform: String) -> Unit
) {
    var accomplished by remember(initialJournal) { mutableStateOf(initialJournal?.accomplishedText ?: "") }
    var obstacles by remember(initialJournal) { mutableStateOf(initialJournal?.obstaclesText ?: "") }
    var distractions by remember(initialJournal) { mutableStateOf(initialJournal?.distractionsText ?: "") }
    var learnings by remember(initialJournal) { mutableStateOf(initialJournal?.learningsText ?: "") }
    var improvements by remember(initialJournal) { mutableStateOf(initialJournal?.improvementsText ?: "") }
    var freeform by remember(initialJournal) { mutableStateOf(initialJournal?.freeformText ?: "") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PrimeCardBg),
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(PrimeBorder))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("High-Signal Journal Prompts", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
            Text("Evening audit to cultivate self-mastery.", style = MaterialTheme.typography.bodySmall, color = PrimeTextSecondary)
            Spacer(modifier = Modifier.height(16.dp))

            JournalTextField(
                prompt = "1. What did I accomplish today?",
                value = accomplished,
                onValueChange = { accomplished = it }
            )
            Spacer(modifier = Modifier.height(12.dp))

            JournalTextField(
                prompt = "2. What were the major obstacles & resistance?",
                value = obstacles,
                onValueChange = { obstacles = it }
            )
            Spacer(modifier = Modifier.height(12.dp))

            JournalTextField(
                prompt = "3. What distracted me & how do I eliminate it?",
                value = distractions,
                onValueChange = { distractions = it }
            )
            Spacer(modifier = Modifier.height(12.dp))

            JournalTextField(
                prompt = "4. What did I learn today?",
                value = learnings,
                onValueChange = { learnings = it }
            )
            Spacer(modifier = Modifier.height(12.dp))

            JournalTextField(
                prompt = "5. How do I level up tomorrow?",
                value = improvements,
                onValueChange = { improvements = it }
            )
            Spacer(modifier = Modifier.height(12.dp))

            JournalTextField(
                prompt = "Free-form Reflection & Notes",
                value = freeform,
                onValueChange = { freeform = it }
            )

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    onSave(accomplished, obstacles, distractions, learnings, improvements, freeform)
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimePrimary, contentColor = PrimeOnPrimaryDark),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save Reflection (+50 XP)")
            }
        }
    }
}

@Composable
private fun JournalTextField(
    prompt: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Column {
        Text(prompt, style = MaterialTheme.typography.bodyMedium, color = PrimePrimary, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text("Write reflection...", color = PrimeTextMuted) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimePrimary,
                unfocusedBorderColor = PrimeBorder,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
