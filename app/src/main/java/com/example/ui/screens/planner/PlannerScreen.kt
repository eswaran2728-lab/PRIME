package com.example.ui.screens.planner

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entities.GoalEntity
import com.example.data.local.entities.GoalMilestoneEntity
import com.example.data.local.entities.TaskEntity
import com.example.data.local.entities.TimeBlockEntity
import com.example.ui.theme.PrimeBorder
import com.example.ui.theme.PrimeCardBg
import com.example.ui.theme.PrimeOnPrimaryDark
import com.example.ui.theme.PrimePrimary
import com.example.ui.theme.PrimeSurface
import com.example.ui.theme.PrimeTextMuted
import com.example.ui.theme.PrimeTextPrimary
import com.example.ui.theme.PrimeTextSecondary

@Composable
fun PlannerScreen(
    tasks: List<TaskEntity>,
    timeBlocks: List<TimeBlockEntity>,
    goals: List<GoalEntity>,
    onToggleTask: (TaskEntity) -> Unit,
    onAddTask: (String, String, Int, Int) -> Unit,
    onAddTimeBlock: (String, String, String, String, String) -> Unit,
    onAddGoal: (title: String, description: String, level: String, category: String, deadline: String, priority: String) -> Unit,
    onAddMilestone: (goalId: Long, title: String) -> Unit,
    onToggleMilestone: (GoalMilestoneEntity, Long) -> Unit,
    onConvertGoalToTask: (GoalEntity) -> Unit,
    onDeleteGoal: (GoalEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Time Blocks, 1 = Priority Tasks, 2 = Strategic Goals
    var showAddTaskDialog by remember { mutableStateOf(false) }
    var showAddTimeBlockDialog by remember { mutableStateOf(false) }
    var showAddGoalDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "TIME & STRATEGY PLANNER",
                    style = MaterialTheme.typography.labelMedium,
                    color = PrimePrimary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "Eliminate drift. Execute your strategic schedule.",
                    style = MaterialTheme.typography.bodySmall,
                    color = PrimeTextSecondary
                )
            }
            FilledTonalButton(
                onClick = {
                    when (selectedTab) {
                        0 -> showAddTimeBlockDialog = true
                        1 -> showAddTaskDialog = true
                        2 -> showAddGoalDialog = true
                    }
                },
                colors = ButtonDefaults.filledTonalButtonColors(containerColor = PrimePrimary, contentColor = PrimeOnPrimaryDark),
                shape = RoundedCornerShape(0.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = when (selectedTab) {
                        0 -> "BLOCK"
                        1 -> "TASK"
                        else -> "GOAL"
                    },
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tab Navigation
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
                .clip(RoundedCornerShape(0.dp))
                .border(1.dp, PrimeBorder, RoundedCornerShape(0.dp))
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Schedule (${timeBlocks.size})", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Tasks (${tasks.count { it.isCompleted }}/${tasks.size})", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Goals (${goals.size})", fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedTab == 0) {
            // Schedule Timeline
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                if (timeBlocks.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = PrimeCardBg),
                            shape = RoundedCornerShape(0.dp)
                        ) {
                            Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("No scheduled time blocks today.", style = MaterialTheme.typography.bodyMedium, color = PrimeTextSecondary)
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { showAddTimeBlockDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimePrimary, contentColor = PrimeOnPrimaryDark)
                                ) {
                                    Text("Add Time Block")
                                }
                            }
                        }
                    }
                } else {
                    items(timeBlocks, key = { it.id }) { block ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(PrimeCardBg)
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = block.startTime,
                                style = MaterialTheme.typography.labelMedium,
                                color = PrimeTextSecondary,
                                modifier = Modifier.width(48.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .height(24.dp)
                                    .background(PrimePrimary)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = block.title,
                                style = MaterialTheme.typography.bodyMedium,
                                color = PrimeTextPrimary,
                                modifier = Modifier.weight(1f)
                            )
                            com.example.ui.components.SquareTag(
                                text = block.category,
                                variant = com.example.ui.components.SquareTagVariant.Outline
                            )
                        }
                    }
                }
            }
        } else if (selectedTab == 1) {
            // Priority Tasks
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                if (tasks.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = PrimeCardBg),
                            shape = RoundedCornerShape(0.dp)
                        ) {
                            Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("No tasks scheduled for today.", style = MaterialTheme.typography.bodyMedium, color = PrimeTextSecondary)
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { showAddTaskDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimePrimary, contentColor = PrimeOnPrimaryDark)
                                ) {
                                    Text("Add Priority Task")
                                }
                            }
                        }
                    }
                } else {
                    items(tasks, key = { it.id }) { task ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onToggleTask(task) }
                                .testTag("task_item_${task.id}"),
                            colors = CardDefaults.cardColors(containerColor = if (task.isCompleted) PrimeCardBg.copy(alpha = 0.6f) else PrimeCardBg),
                            shape = RoundedCornerShape(0.dp),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = androidx.compose.ui.graphics.SolidColor(if (task.isCompleted) PrimePrimary.copy(alpha = 0.5f) else PrimeBorder)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                    contentDescription = null,
                                    tint = if (task.isCompleted) PrimePrimary else PrimeTextMuted,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = task.title,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = if (task.isCompleted) PrimeTextMuted else Color.White,
                                        fontWeight = if (task.isCompleted) FontWeight.Normal else FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "${task.category} • +${task.xpReward} XP",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = PrimeTextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Multi-Level Strategic Goals Tab (Phase 2 - 16)
            var goalLevelFilter by remember { mutableStateOf("all") }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // Goal Level Filter Chips
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("all", "yearly", "quarterly", "monthly", "weekly").forEach { lvl ->
                            FilterChip(
                                selected = goalLevelFilter == lvl,
                                onClick = { goalLevelFilter = lvl },
                                label = { Text(lvl.replaceFirstChar { it.uppercase() }) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PrimePrimary,
                                    selectedLabelColor = PrimeOnPrimaryDark
                                )
                            )
                        }
                    }
                }

                val filteredGoals = if (goalLevelFilter == "all") goals else goals.filter { it.level.equals(goalLevelFilter, ignoreCase = true) }

                if (filteredGoals.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = PrimeCardBg),
                            shape = RoundedCornerShape(0.dp)
                        ) {
                            Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("No strategic goals set for this level.", style = MaterialTheme.typography.bodyMedium, color = PrimeTextSecondary)
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { showAddGoalDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimePrimary, contentColor = PrimeOnPrimaryDark)
                                ) {
                                    Text("Create Goal")
                                }
                            }
                        }
                    }
                } else {
                    items(filteredGoals, key = { it.id }) { goal ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = PrimeCardBg),
                            shape = RoundedCornerShape(0.dp),
                            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(PrimeBorder))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${goal.level.uppercase()} • ${goal.category}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = PrimePrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Due: ${goal.deadline}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = PrimeTextSecondary
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = goal.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                if (goal.description.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = goal.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = PrimeTextSecondary
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                                // Progress Bar
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    LinearProgressIndicator(
                                        progress = { goal.progress },
                                        color = PrimePrimary,
                                        trackColor = PrimeSurface,
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(8.dp)
                                            .clip(RoundedCornerShape(0.dp))
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = "${(goal.progress * 100).toInt()}%",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = PrimePrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                                // 1-Click Convert to Task Button
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Button(
                                        onClick = { onConvertGoalToTask(goal) },
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimeSurface, contentColor = PrimePrimary),
                                        shape = RoundedCornerShape(0.dp)
                                    ) {
                                        Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Convert to Today's Task")
                                    }
                                    IconButton(onClick = { onDeleteGoal(goal) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete Goal", tint = PrimeTextMuted)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Goal Dialog
    if (showAddGoalDialog) {
        var gTitle by remember { mutableStateOf("") }
        var gDesc by remember { mutableStateOf("") }
        var gLevel by remember { mutableStateOf("monthly") }
        var gCategory by remember { mutableStateOf("Fitness") }
        var gDeadline by remember { mutableStateOf("2026-12-31") }
        var gPriority by remember { mutableStateOf("high") }

        Dialog(onDismissRequest = { showAddGoalDialog = false }) {
            Surface(
                shape = RoundedCornerShape(0.dp),
                color = PrimeCardBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimeBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Create Strategic Goal", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = gTitle,
                        onValueChange = { gTitle = it },
                        label = { Text("Goal Title") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimePrimary,
                            unfocusedBorderColor = PrimeBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = gDesc,
                        onValueChange = { gDesc = it },
                        label = { Text("Action Strategy / Description") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimePrimary,
                            unfocusedBorderColor = PrimeBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = gLevel,
                        onValueChange = { gLevel = it },
                        label = { Text("Level (yearly, quarterly, monthly, weekly, daily)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimePrimary,
                            unfocusedBorderColor = PrimeBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = gCategory,
                        onValueChange = { gCategory = it },
                        label = { Text("Category (Fitness, Mindset, Career, Finance)") },
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
                            onClick = { showAddGoalDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = PrimeTextSecondary)
                        ) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (gTitle.isNotBlank()) {
                                    onAddGoal(gTitle, gDesc, gLevel, gCategory, gDeadline, gPriority)
                                    showAddGoalDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimePrimary, contentColor = PrimeOnPrimaryDark)
                        ) {
                            Text("Save Goal")
                        }
                    }
                }
            }
        }
    }

    // Add Task Dialog
    if (showAddTaskDialog) {
        var tTitle by remember { mutableStateOf("") }
        var tCategory by remember { mutableStateOf("Priority") }
        var tPriority by remember { mutableIntStateOf(1) }

        Dialog(onDismissRequest = { showAddTaskDialog = false }) {
            Surface(
                shape = RoundedCornerShape(0.dp),
                color = PrimeCardBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimeBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Add Priority Task", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = tTitle,
                        onValueChange = { tTitle = it },
                        label = { Text("Task Title") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimePrimary,
                            unfocusedBorderColor = PrimeBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = tCategory,
                        onValueChange = { tCategory = it },
                        label = { Text("Category (Priority, Work, Personal)") },
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
                            onClick = { showAddTaskDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = PrimeTextSecondary)
                        ) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (tTitle.isNotBlank()) {
                                    onAddTask(tTitle, tCategory, tPriority, 50)
                                    showAddTaskDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimePrimary, contentColor = PrimeOnPrimaryDark)
                        ) {
                            Text("Add Task")
                        }
                    }
                }
            }
        }
    }

    // Add Time Block Dialog
    if (showAddTimeBlockDialog) {
        var bTitle by remember { mutableStateOf("") }
        var bStart by remember { mutableStateOf("09:00") }
        var bEnd by remember { mutableStateOf("10:30") }
        var bCategory by remember { mutableStateOf("Work") }
        var bColor by remember { mutableStateOf("#3B82F6") }

        Dialog(onDismissRequest = { showAddTimeBlockDialog = false }) {
            Surface(
                shape = RoundedCornerShape(0.dp),
                color = PrimeCardBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimeBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Schedule Time Block", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = bTitle,
                        onValueChange = { bTitle = it },
                        label = { Text("Block Title (e.g. Deep Work Sprint)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimePrimary,
                            unfocusedBorderColor = PrimeBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = bStart,
                            onValueChange = { bStart = it },
                            label = { Text("Start Time") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimePrimary,
                                unfocusedBorderColor = PrimeBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = bEnd,
                            onValueChange = { bEnd = it },
                            label = { Text("End Time") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimePrimary,
                                unfocusedBorderColor = PrimeBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = bCategory,
                        onValueChange = { bCategory = it },
                        label = { Text("Category (Work, Workout, Health)") },
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
                            onClick = { showAddTimeBlockDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = PrimeTextSecondary)
                        ) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (bTitle.isNotBlank()) {
                                    onAddTimeBlock(bTitle, bStart, bEnd, bCategory, bColor)
                                    showAddTimeBlockDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimePrimary, contentColor = PrimeOnPrimaryDark)
                        ) {
                            Text("Schedule")
                        }
                    }
                }
            }
        }
    }
}
