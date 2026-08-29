package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "body_measurements")
data class BodyMeasurementEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String, // YYYY-MM-DD
    val weightKg: Float,
    val heightCm: Float = 180f,
    val waistCm: Float? = null,
    val chestCm: Float? = null,
    val armsCm: Float? = null,
    val thighsCm: Float? = null,
    val neckCm: Float? = null,
    val bodyFatEstimate: Float? = null, // percentage
    val bmi: Float = 22.5f,
    val notes: String = "",
    val source: String = "manual", // "ai" | "manual" | "ai_edited"
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "body_photos")
data class BodyPhotoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String, // YYYY-MM-DD
    val photoUri: String,
    val pose: String = "front", // "front", "side", "back"
    val weightAtPhotoKg: Float? = null,
    val notes: String = "",
    val source: String = "manual",
    val timestamp: Long = System.currentTimeMillis()
)
