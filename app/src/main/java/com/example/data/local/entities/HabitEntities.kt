package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val iconName: String = "fitness_center", // "fitness_center", "water_drop", "menu_book", "self_improvement", "bedtime", "code", "sun", "shower"
    val category: String = "Discipline", // "Discipline", "Health", "Mindset", "Fitness", "Career", "Learning"
    val frequency: String = "Daily", // "Daily", "Weekdays", "3x / week"
    val targetCountDaily: Int = 1,
    val reminderTime: String = "08:00",
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val isArchived: Boolean = false,
    val source: String = "manual",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "habit_logs")
data class HabitLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val habitId: Long,
    val date: String, // YYYY-MM-DD
    val isCompleted: Boolean = true,
    val completedCount: Int = 1,
    val timestamp: Long = System.currentTimeMillis(),
    val source: String = "manual"
)
