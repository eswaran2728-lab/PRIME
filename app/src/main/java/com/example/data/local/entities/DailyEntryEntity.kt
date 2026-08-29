package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Central daily rollup entry keyed by date (YYYY-MM-DD).
 * Rolls up activity across workout, nutrition, water, habits, focus, and PRIME score.
 */
@Entity(tableName = "daily_entries")
data class DailyEntryEntity(
    @PrimaryKey val date: String, // YYYY-MM-DD
    val workoutDone: Boolean = false,
    val workoutDurationMinutes: Int = 0,
    val caloriesLogged: Int = 0,
    val proteinGrams: Float = 0f,
    val carbsGrams: Float = 0f,
    val fatGrams: Float = 0f,
    val waterTotalMl: Int = 0,
    val mood: String = "Good", // "Unstoppable", "Great", "Good", "Tired", "Challenging"
    val sleepHours: Float = 7.5f,
    val habitsCompletedCount: Int = 0,
    val habitsTotalCount: Int = 0,
    val focusMinutes: Int = 0,
    val primeScore: Int = 0, // 0 - 100
    val notes: String = "",
    val source: String = "manual", // "ai" | "manual" | "ai_edited"
    val updatedAt: Long = System.currentTimeMillis()
)
