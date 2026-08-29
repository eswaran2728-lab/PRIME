package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String = "Priority", // "Priority", "Work", "Personal", "Health", "Learning"
    val date: String, // YYYY-MM-DD
    val isCompleted: Boolean = false,
    val priorityLevel: Int = 1, // 1 = High/Apex, 2 = Medium, 3 = Low
    val xpReward: Int = 50,
    val estimatedMinutes: Int = 30,
    val source: String = "manual",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "time_blocks")
data class TimeBlockEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val date: String, // YYYY-MM-DD
    val startTime: String, // "08:00"
    val endTime: String, // "09:30"
    val category: String, // "Work", "Appointments", "Tasks", "Workout", "Meals", "Sleep", "Learning", "Business", "Social"
    val colorHex: String = "#3B82F6",
    val isCompleted: Boolean = false,
    val notes: String = "",
    val source: String = "manual"
)

@Entity(tableName = "daily_scores")
data class DailyScoreRecordEntity(
    @PrimaryKey val date: String, // YYYY-MM-DD
    val totalScore: Int, // 0 - 100
    val bodyScore: Float, // 15%
    val fitnessScore: Float, // 10%
    val nutritionScore: Float, // 10%
    val healthScore: Float, // 10%
    val appearanceScore: Float, // 10%
    val mindsetScore: Float, // 10%
    val focusScore: Float, // 10%
    val disciplineScore: Float, // 10%
    val careerScore: Float, // 5%
    val learningScore: Float, // 5%
    val financeScore: Float, // 5%
    val isBaselineCalibrated: Boolean = false,
    val explanationsJson: String = "[]",
    val calculatedAt: Long = System.currentTimeMillis()
)
