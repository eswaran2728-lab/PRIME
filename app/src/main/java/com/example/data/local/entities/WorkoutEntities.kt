package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_templates")
data class WorkoutTemplateEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String, // "Strength", "Hypertrophy", "PPL", "Upper/Lower", "Conditioning", "Custom"
    val description: String,
    val estimatedDurationMinutes: Int = 45,
    val exerciseNamesJson: String = "[]", // List of exercise names or IDs
    val isStarter: Boolean = false,
    val source: String = "manual"
)

@Entity(tableName = "exercises")
data class ExerciseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val muscleGroup: String, // "Chest", "Back", "Legs", "Shoulders", "Arms", "Core", "Cardio"
    val equipment: String = "Barbell", // "Barbell", "Dumbbell", "Cable", "Bodyweight", "Machine"
    val defaultRestSeconds: Int = 90,
    val personalRecordWeightKg: Float = 0f,
    val personalRecordReps: Int = 0,
    val isCustom: Boolean = false,
    val source: String = "manual"
)

@Entity(tableName = "workout_sessions")
data class WorkoutSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val templateId: Long? = null,
    val templateName: String = "Freestyle Workout",
    val date: String, // YYYY-MM-DD
    val startTime: Long = System.currentTimeMillis(),
    val endTime: Long? = null,
    val durationMinutes: Int = 0,
    val totalVolumeKg: Float = 0f,
    val totalSetsCompleted: Int = 0,
    val rpe: Int = 8, // Rate of Perceived Exertion (1 - 10)
    val notes: String = "",
    val isCompleted: Boolean = false,
    val source: String = "manual"
)

@Entity(tableName = "workout_sets")
data class WorkoutSetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val exerciseName: String,
    val muscleGroup: String = "Full Body",
    val setNumber: Int,
    val weightKg: Float = 0f,
    val reps: Int = 0,
    val durationSeconds: Int = 0,
    val isCompleted: Boolean = false,
    val rpe: Int? = null,
    val isPr: Boolean = false,
    val source: String = "manual"
)
