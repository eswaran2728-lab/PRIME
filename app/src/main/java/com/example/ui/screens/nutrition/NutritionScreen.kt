package com.example.ui.screens.nutrition

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WaterDrop
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.DailyEntryEntity
import com.example.data.local.entities.FoodItemEntity
import com.example.data.local.entities.NutritionLogEntity
import com.example.data.local.entities.ProfileEntity
import com.example.ui.theme.PrimeBlue
import com.example.ui.theme.PrimeCyan
import com.example.ui.theme.PrimeGold
import com.example.ui.theme.PrimeGreen
import com.example.ui.theme.PrimeOrange
import com.example.ui.theme.PrimePurple
import com.example.ui.theme.PrimeRed

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
                        text = "NUTRITION & MACROS",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.5.sp
                        ),
                        color = PrimeGold
                    )
                    Text(
                        text = "Precision caloric and macronutrient fuel tracking.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PrimeGold.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = profile?.primaryGoal ?: "Muscle Gain",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = PrimeGold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Calorie & Macros Hero Card
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
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "$loggedCalories",
                                style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Black),
                                color = PrimeGold
                            )
                            Text(
                                text = "of $targetCalories kcal goal",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${targetCalories - loggedCalories} kcal",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (targetCalories >= loggedCalories) PrimeGreen else PrimeRed
                            )
                            Text(
                                text = if (targetCalories >= loggedCalories) "remaining" else "over target",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Linear Progress for calories
                    LinearProgressIndicator(
                        progress = { (loggedCalories.toFloat() / targetCalories.toFloat()).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = PrimeGold,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Macro breakdown columns
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MacroColumn(
                            label = "Protein",
                            logged = loggedProtein,
                            target = targetProtein,
                            unit = "g",
                            color = PrimeBlue,
                            modifier = Modifier.weight(1f)
                        )
                        MacroColumn(
                            label = "Carbs",
                            logged = loggedCarbs,
                            target = targetCarbs,
                            unit = "g",
                            color = PrimeOrange,
                            modifier = Modifier.weight(1f)
                        )
                        MacroColumn(
                            label = "Fat",
                            logged = loggedFat,
                            target = targetFat,
                            unit = "g",
                            color = PrimePurple,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Hydration Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = CircleShape, color = PrimeCyan.copy(alpha = 0.15f), modifier = Modifier.size(36.dp)) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.WaterDrop, contentDescription = null, tint = PrimeCyan, modifier = Modifier.size(18.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "$loggedWater / $targetWater ml",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Daily Hydration Target",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = PrimeCyan.copy(alpha = 0.15f),
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onQuickWater(250) }
                        ) {
                            Text(
                                text = "+250ml",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = PrimeCyan,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = PrimeCyan.copy(alpha = 0.25f),
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onQuickWater(500) }
                        ) {
                            Text(
                                text = "+500ml",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = PrimeCyan,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // Action Buttons Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilledTonalButton(
                    onClick = {
                        selectedMealForLog = "Breakfast"
                        showLogFoodDialog = true
                    },
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = PrimeGreen, contentColor = Color.Black),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("LOG FOOD", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                }

                OutlinedButton(
                    onClick = { showAddCustomFoodDialog = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("+ CUSTOM FOOD", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = PrimeGold)
                }
            }
        }

        // Meals Breakdown
        item {
            Text(
                text = "MEALS BREAKDOWN",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        items(mealTypes) { mealType ->
            val mealsForType = nutritionLogs.filter { it.mealType.equals(mealType, ignoreCase = true) }
            val mealCalories = mealsForType.sumOf { it.calories }
            val mealProtein = mealsForType.sumOf { it.proteinGrams.toDouble() }.toFloat()

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = mealType,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "$mealCalories kcal · ${mealProtein.toInt()}g protein",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            IconButton(
                                onClick = {
                                    selectedMealForLog = mealType
                                    showLogFoodDialog = true
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add to $mealType", tint = PrimeGreen, modifier = Modifier.size(18.dp))
                            }
                        }
                    }

                    if (mealsForType.isEmpty()) {
                        Text(
                            text = "No $mealType logged.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            mealsForType.forEach { item ->
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
                                        Column {
                                            Text(
                                                text = item.foodName,
                                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "${item.portions} portion(s)",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        Text(
                                            text = "${item.calories} kcal · ${item.proteinGrams.toInt()}g P",
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                            color = PrimeGreen
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
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
fun MacroColumn(
    label: String,
    logged: Float,
    target: Float,
    unit: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    val ratio = if (target > 0) (logged / target).coerceIn(0f, 1f) else 0f
    Column(modifier = modifier.padding(horizontal = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("${logged.toInt()}/${target.toInt()}$unit", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = color)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { ratio },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
fun LogFoodDialog(
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
                    Text("LOG FOOD", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black), color = PrimeGreen)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
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
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) PrimeGreen.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedFood = food }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(food.name, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold), color = MaterialTheme.colorScheme.onSurface)
                                    Text(food.servingSize, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Text("${food.caloriesPerServing} kcal", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = PrimeGreen)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = portionInput,
                        onValueChange = { portionInput = it },
                        label = { Text("Portions") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                FilledTonalButton(
                    onClick = {
                        val food = selectedFood ?: return@FilledTonalButton
                        val portions = portionInput.toFloatOrNull() ?: 1.0f
                        onLogFood(selectedMeal, food.name, portions, food.caloriesPerServing, food.proteinGrams, food.carbsGrams, food.fatGrams)
                    },
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = PrimeGreen, contentColor = Color.Black),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("LOG MEAL", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                }
            }
        }
    }
}

@Composable
fun AddCustomFoodDialog(
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
                    Text("ADD CUSTOM FOOD", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black), color = PrimeGold)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
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

                FilledTonalButton(
                    onClick = {
                        if (name.isNotBlank()) {
                            onSave(name, servingSize, calories.toIntOrNull() ?: 0, protein.toFloatOrNull() ?: 0f, carbs.toFloatOrNull() ?: 0f, fat.toFloatOrNull() ?: 0f)
                        }
                    },
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = PrimeGold, contentColor = Color.Black),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("SAVE CUSTOM FOOD", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                }
            }
        }
    }
}
