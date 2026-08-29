package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "focus_sessions")
data class FocusSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String, // YYYY-MM-DD
    val taskName: String = "Deep Work Block",
    val mode: String = "pomodoro", // "pomodoro", "deep_work", "custom"
    val durationMinutes: Int = 25,
    val focusRating: Int = 5, // 1 to 5
    val distractionsCount: Int = 0,
    val notes: String = "",
    val isCompleted: Boolean = true,
    val startTime: Long = System.currentTimeMillis(),
    val endTime: Long = System.currentTimeMillis()
)

@Entity(tableName = "focus_distractions")
data class FocusDistractionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
)
