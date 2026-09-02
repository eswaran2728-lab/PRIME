package com.example.ui.screens.habits

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entities.HabitEntity
import com.example.data.local.entities.HabitLogEntity
import com.example.ui.components.SquareTag
import com.example.ui.components.SquareTagVariant
import com.example.ui.theme.PrimeBackground
import com.example.ui.theme.PrimePrimary
import com.example.ui.theme.PrimeSurface
import com.example.ui.theme.PrimeTextPrimary
import com.example.ui.theme.PrimeTextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HabitsScreen(
    habits: List<HabitEntity>,
    habitLogs: List<HabitLogEntity>,
    weekDates: List<String>,
    weekHabitLogs: List<HabitLogEntity>,
    onToggleHabit: (Long, Boolean) -> Unit,
    onAddHabit: (String, String, String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    val completedTodayIds = habitLogs.filter { it.isCompleted }.map { it.habitId }.toSet()
    val today = weekDates.firstOrNull()
    val orderedDates = weekDates.asReversed() // oldest -> newest (today last)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "HABITS",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black, letterSpacing = 1.5.sp),
                    color = PrimePrimary
                )
                Row(
                    modifier = Modifier
                        .clickable { showAddDialog = true }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = PrimePrimary, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("ADD", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = PrimePrimary)
                }
            }
        }

        if (habits.isEmpty()) {
            item {
                Text(
                    text = "No habits defined. Tap + ADD to create your first habit.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = PrimeTextSecondary
                )
            }
        } else {
            items(habits, key = { it.id }) { habit ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PrimeSurface)
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = habit.name,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontSize = 15.sp),
                            color = PrimeTextPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        SquareTag(text = "${habit.currentStreak} DAY STREAK", variant = SquareTagVariant.Accent)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        orderedDates.forEach { date ->
                            val isToday = date == today
                            val completed = if (isToday) {
                                habit.id in completedTodayIds
                            } else {
                                weekHabitLogs.any { it.habitId == habit.id && it.date == date && it.isCompleted }
                            }
                            DayCell(
                                label = dayLetter(date),
                                filled = completed,
                                clickable = isToday,
                                onClick = { onToggleHabit(habit.id, completed) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }

    if (showAddDialog) {
        AddHabitDialog(
            onDismiss = { showAddDialog = false },
            onSave = { name, cat, freq, reminder ->
                onAddHabit(name, "repeat", cat, freq, reminder)
                showAddDialog = false
            }
        )
    }
}

private fun dayLetter(dateStr: String): String {
    return try {
        val date: Date = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(dateStr) ?: return "?"
        SimpleDateFormat("EEE", Locale.US).format(date).take(1).uppercase(Locale.US)
    } catch (e: Exception) {
        "?"
    }
}

@Composable
private fun DayCell(
    label: String,
    filled: Boolean,
    clickable: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .then(if (clickable) Modifier.clickable { onClick() } else Modifier),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = PrimeTextSecondary)
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(22.dp)
                .background(if (filled) PrimePrimary else PrimeBackground)
        )
    }
}

@Composable
private fun AddHabitDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Health") }
    var reminder by remember { mutableStateOf("08:00") }
    val categories = listOf("Health", "Fitness", "Focus")

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
                    Text("CREATE HABIT", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black), color = PrimePrimary)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = PrimeTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Habit Name") },
                    placeholder = { Text("e.g. Cold shower, Read 20 mins") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text("Category:", style = MaterialTheme.typography.labelSmall, color = PrimeTextSecondary)
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    categories.forEach { cat ->
                        Text(
                            text = cat,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (category == cat) PrimeBackground else PrimeTextPrimary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .weight(1f)
                                .background(if (category == cat) PrimePrimary else PrimeBackground)
                                .clickable { category = cat }
                                .padding(vertical = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = reminder,
                    onValueChange = { reminder = it },
                    label = { Text("Reminder Time (HH:MM)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PrimePrimary)
                        .clickable {
                            if (name.isNotBlank()) {
                                onSave(name, category, "Daily", reminder)
                            }
                        }
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("ADD HABIT", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = PrimeBackground)
                }
            }
        }
    }
}
