package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mindset_logs")
data class MindsetLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String, // YYYY-MM-DD
    val moodRating: Int = 8, // 1 - 10
    val energyRating: Int = 8, // 1 - 10
    val stressRating: Int = 3, // 1 - 10 (lower is better/calmer)
    val confidenceRating: Int = 8, // 1 - 10
    val focusRating: Int = 8, // 1 - 10
    val moodLabel: String = "Good", // "Unstoppable", "Sharp", "Good", "Neutral", "Tired", "Stressed"
    val loggedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "journal_entries")
data class JournalEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String, // YYYY-MM-DD
    val accomplishedText: String = "",
    val obstaclesText: String = "",
    val distractionsText: String = "",
    val learningsText: String = "",
    val improvementsText: String = "",
    val freeformText: String = "",
    val tags: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)
