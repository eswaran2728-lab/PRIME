package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "grooming_routine_items")
data class GroomingRoutineItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val routineType: String, // "morning", "night", "weekly", "custom"
    val title: String,
    val iconName: String = "face",
    val orderIndex: Int = 0,
    val isDefault: Boolean = false,
    val isArchived: Boolean = false
)

@Entity(tableName = "grooming_logs")
data class GroomingLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val routineType: String, // "morning", "night", "weekly", "custom"
    val itemId: Long,
    val date: String, // YYYY-MM-DD
    val isCompleted: Boolean = true,
    val completedAt: Long = System.currentTimeMillis()
)
