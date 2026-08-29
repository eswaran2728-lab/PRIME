package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "foods")
data class FoodItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val brand: String = "",
    val servingSize: String = "100g",
    val caloriesPerServing: Int,
    val proteinGrams: Float,
    val carbsGrams: Float,
    val fatGrams: Float,
    val isCustom: Boolean = false,
    val isFavorite: Boolean = false,
    val source: String = "manual" // "ai" | "manual" | "ai_edited"
)

@Entity(tableName = "nutrition_logs")
data class NutritionLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String, // YYYY-MM-DD
    val mealType: String, // "Breakfast", "Lunch", "Dinner", "Snack"
    val foodName: String,
    val portions: Float = 1.0f,
    val calories: Int,
    val proteinGrams: Float,
    val carbsGrams: Float,
    val fatGrams: Float,
    val loggedAt: Long = System.currentTimeMillis(),
    val source: String = "manual"
)

@Entity(tableName = "hydration_logs")
data class HydrationLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String, // YYYY-MM-DD
    val amountMl: Int = 250,
    val loggedAt: Long = System.currentTimeMillis(),
    val source: String = "manual"
)
