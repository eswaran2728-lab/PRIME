package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "achievement_badges")
data class AchievementBadgeEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val iconName: String,
    val category: String, // "Fitness", "Habits", "Focus", "Mindset", "Finance", "Learning", "Mastery"
    val xpReward: Int = 100,
    val isUnlocked: Boolean = false,
    val unlockedDate: String = "",
    val progress: Float = 0f,
    val targetRequirement: String = ""
)

@Entity(tableName = "xp_transactions")
data class XpTransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val source: String, // "Workout", "Habit", "Deep Work", "Journal", "Weekly Goal", "Course", "Finance"
    val xpEarned: Int,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
