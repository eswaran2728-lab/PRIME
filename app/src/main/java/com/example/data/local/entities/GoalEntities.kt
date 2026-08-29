package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val level: String = "monthly", // "long_term", "yearly", "quarterly", "monthly", "weekly", "daily"
    val category: String = "Fitness", // "Fitness", "Career", "Learning", "Finance", "Health", "Mindset", "Personal"
    val deadline: String = "", // YYYY-MM-DD
    val progress: Float = 0f, // 0.0f - 1.0f (or 0 - 100%)
    val priority: String = "medium", // "low", "medium", "high", "urgent"
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "goal_milestones")
data class GoalMilestoneEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val goalId: Long,
    val title: String,
    val isCompleted: Boolean = false,
    val orderIndex: Int = 0
)
