package com.example.ui.screens.nutrition

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.DailyEntryEntity
import com.example.data.local.entities.FoodItemEntity
import com.example.data.local.entities.NutritionLogEntity
import com.example.data.local.entities.ProfileEntity
import com.example.ui.components.BlueprintFrame
import com.example.ui.theme.PrimeBackground
import com.example.ui.theme.PrimePrimary
import com.example.ui.theme.PrimeSurface
import com.example.ui.theme.PrimeSurfaceVariant
import com.example.ui.theme.PrimeTextPrimary
import com.example.ui.theme.PrimeTextSecondary

@Composable
fun NutritionScreen(
    profile: ProfileEntity?,
    dailyEntry: DailyEntryEntity?,
    nutritionLogs: List<NutritionLogEntity>,
    availableFoods: List<FoodItemEntity>,
    onLogFood: (String, String, Float, Int, Float, Float, Float) -> Unit,
    onAddCustomFood: (String, String, Int, Float, Float, Float) -> Unit,
    onQuickWater: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var showLogFoodDialog by remember { mutableStateOf(false) }
    var showAddCustomFoodDialog by remember { mutableStateOf(false) }
    var selectedMealForLog by remember { mutableStateOf("Breakfast") }

    val targetCalories = profile?.targetCalories ?: 2400
    val targetProtein = profile?.targetProteinGrams ?: 160f
    val targetCarbs = profile?.targetCarbsGrams ?: 250f
    val targetFat = profile?.targetFatGrams ?: 65f
    val targetWater = profile?.targetWaterMl ?: 3000

    val loggedCalories = dailyEntry?.caloriesLogged ?: 0
    val loggedProtein = dailyEntry?.proteinGrams ?: 0f
    val loggedCarbs = dailyEntry?.carbsGrams ?: 0f
    val loggedFat = dailyEntry?.fatGrams ?: 0f
    val loggedWater = dailyEntry?.waterTotalMl ?: 0

    val mealTypes = listOf("Breakfast", "Lunch", "Dinner", "Snack")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "NUTRITION",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black, letterSpacing = 1.5.sp),
                color = PrimePrimary
            )
        }

        // Calorie ring + macros — blueprint frame.
        item {
            BlueprintFrame(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                    CalorieRing(consumed = loggedCalories, goal = targetCalories, sizeDp = 96.dp)

                    Spacer(modifier = Modifier.width(18.dp))

                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        MacroBar(label = "Protein", logged = loggedProtein, target = targetProtein, unit = "g")
                        MacroBar(label = "Carbs", logged = loggedCarbs, target = targetCarbs, unit = "g")
                        MacroBar(label = "Fat", logged = loggedFat, target = targetFat, unit = "g")
                    }
                }
            }
        }

        // Hydration
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PrimeSurface)
                    .padding(14.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "WATER",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp),
                        color = PrimeTextSecondary
                    )
                    Text("$loggedWater / $targetWater ml", style = MaterialTheme.typography.labelMedium, color = PrimePrimary)
                }
                Spacer(modifier = Modifier.height(10.dp))
                val cupMl = (targetWater / 8f).coerceAtLeast(1f)
                val filledCups = (loggedWater / cupMl).toInt().coerceIn(0, 8)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    repeat(8) { i ->
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = "Cup ${i + 1}",
                            tint = if (i < filledCups) PrimePrimary else PrimeTextSecondary.copy(alpha = 0.3f),
                            modifier = Modifier
                                .weight(1f)
                                .size(20.dp)
                                .clickable { onQuickWater(cupMl.toInt()) }
                        )
                    }
                }
            }
        }

        // Action buttons
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilledTonalButton(
                    onClick = {
                        selectedMealForLog = "Breakfast"
                        showLogFoodDialog = true
                    },
                    shape = RoundedCornerShape(0.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = PrimePrimary, contentColor = PrimeBackground),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("LOG FOOD", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                }

                OutlinedButton(
                    onClick = { showAddCustomFoodDialog = true },
                    shape = RoundedCornerShape(0.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("+ CUSTOM FOOD", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = PrimePrimary)
                }
            }
        }

        // Today's meals
        item {
            Text(
                text = "TODAY'S MEALS",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = PrimeTextSecondary
            )
        }

        items(mealTypes) { mealType ->
            val mealsForType = nutritionLogs.filter { it.mealType.equals(mealType, ignoreCase = true) }
            val mealCalories = mealsForType.sumOf { it.calories }
            val detail = if (mealsForType.isEmpty()) "Not logged yet" else mealsForType.joinToString(", ") { it.foodName }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PrimeSurface)
                    .clickable {
                        selectedMealForLog = mealType
                        showLogFoodDialog = true
                    }
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(mealType, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 13.5.sp), color = PrimeTextPrimary)
                    Text(detail, style = MaterialTheme.typography.labelSmall, color = PrimeTextSecondary, maxLines = 1)
                }
                Text(
                    text = if (mealCalories > 0) "$mealCalories kcal" else "—",
                    style = MaterialTheme.typography.labelMedium,
                    color = PrimePrimary
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showLogFoodDialog) {
        LogFoodDialog(
            defaultMealType = selectedMealForLog,
            availableFoods = availableFoods,
            onDismiss = { showLogFoodDialog = false },
            onLogFood = { mealType, foodName, portions, cals, p, c, f ->
                onLogFood(mealType, foodName, portions, cals, p, c, f)
                showLogFoodDialog = false
            }
        )
    }

    if (showAddCustomFoodDialog) {
        AddCustomFoodDialog(
            onDismiss = { showAddCustomFoodDialog = false },
            onSave = { name, size, cals, p, c, f ->
                onAddCustomFood(name, size, cals, p, c, f)
                showAddCustomFoodDialog = false
            }
        )
    }
}

@Composable
private fun CalorieRing(consumed: Int, goal: Int, sizeDp: androidx.compose.ui.unit.Dp) {
    Box(modifier = Modifier.size(sizeDp), contentAlignment = Alignment.Center) {
        val progress = if (goal > 0) (consumed.toFloat() / goal.toFloat()).coerceIn(0f, 1f) else 0f
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 7.dp.toPx()
            drawArc(color = PrimeSurfaceVariant, startAngle = -90f, sweepAngle = 360f, useCenter = false, style = Stroke(width = strokeWidth))
            drawArc(color = PrimePrimary, startAngle = -90f, sweepAngle = 360f * progress, useCenter = false, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("$consumed", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp), color = PrimeTextPrimary)
            Text("of $goal", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = PrimeTextSecondary)
        }
    }
}

@Composable
private fun MacroBar(label: String, logged: Float, target: Float, unit: String) {
    val ratio = if (target > 0) (logged / target).coerceIn(0f, 1f) else 0f
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp), color = PrimeTextPrimary)
            Text("${logged.toInt()}/${target.toInt()}$unit", style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp), color = PrimeTextSecondary)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(PrimeTextPrimary.copy(alpha = 0.14f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = ratio)
                    .fillMaxSize()
                    .background(PrimePrimary)
            )
        }
    }
}

@Composable
private fun LogFoodDialog(
    defaultMealType: String,
    availableFoods: List<FoodItemEntity>,
    onDismiss: () -> Unit,
    onLogFood: (String, String, Float, Int, Float, Float, Float) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedMeal by remember { mutableStateOf(defaultMealType) }
    var selectedFood by remember { mutableStateOf<FoodItemEntity?>(availableFoods.firstOrNull()) }
    var portionInput by remember { mutableStateOf("1.0") }

    val filteredFoods = if (searchQuery.isBlank()) availableFoods else availableFoods.filter {
        it.name.contains(searchQuery, ignoreCase = true)
    }

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
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
                    Text("LOG FOOD", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black), color = PrimePrimary)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = PrimeTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search food or ingredient...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(filteredFoods) { food ->
                        val isSelected = selectedFood?.id == food.id
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (isSelected) PrimePrimary.copy(alpha = 0.2f) else PrimeSurfaceVariant)
                                .clickable { selectedFood = food }
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(food.name, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold), color = PrimeTextPrimary)
                                Text(food.servingSize, style = MaterialTheme.typography.labelSmall, color = PrimeTextSecondary)
                            }
                            Text("${food.caloriesPerServing} kcal", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = PrimePrimary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = portionInput,
                    onValueChange = { portionInput = it },
                    label = { Text("Portions") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PrimePrimary)
                        .clickable {
                            val food = selectedFood ?: return@clickable
                            val portions = portionInput.toFloatOrNull() ?: 1.0f
                            onLogFood(selectedMeal, food.name, portions, food.caloriesPerServing, food.proteinGrams, food.carbsGrams, food.fatGrams)
                        }
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("LOG MEAL", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = PrimeBackground)
                }
            }
        }
    }
}

@Composable
private fun AddCustomFoodDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, Int, Float, Float, Float) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var servingSize by remember { mutableStateOf("100g") }
    var calories by remember { mutableStateOf("200") }
    var protein by remember { mutableStateOf("25") }
    var carbs by remember { mutableStateOf("15") }
    var fat by remember { mutableStateOf("5") }

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
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
                    Text("ADD CUSTOM FOOD", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black), color = PrimePrimary)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = PrimeTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Food Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(value = servingSize, onValueChange = { servingSize = it }, label = { Text("Serving Size (e.g. 150g, 1 cup)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(value = calories, onValueChange = { calories = it }, label = { Text("Calories") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f), singleLine = true)
                    OutlinedTextField(value = protein, onValueChange = { protein = it }, label = { Text("Protein (g)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f), singleLine = true)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(value = carbs, onValueChange = { carbs = it }, label = { Text("Carbs (g)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f), singleLine = true)
                    OutlinedTextField(value = fat, onValueChange = { fat = it }, label = { Text("Fat (g)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f), singleLine = true)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PrimePrimary)
                        .clickable {
                            if (name.isNotBlank()) {
                                onSave(name, servingSize, calories.toIntOrNull() ?: 0, protein.toFloatOrNull() ?: 0f, carbs.toFloatOrNull() ?: 0f, fat.toFloatOrNull() ?: 0f)
                            }
                        }
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("SAVE CUSTOM FOOD", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = PrimeBackground)
                }
            }
        }
    }
}
