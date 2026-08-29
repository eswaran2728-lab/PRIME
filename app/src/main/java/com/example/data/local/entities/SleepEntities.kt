package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sleep_logs")
data class SleepLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String, // YYYY-MM-DD (the day the user woke up)
    val bedtime: String = "22:30", // HH:mm
    val wakeTime: String = "06:30", // HH:mm
    val durationMinutes: Int = 480, // e.g. 8 hours = 480 mins
    val qualityRating: Int = 4, // 1 to 5 stars/rating
    val nextDayEnergy: Int = 4, // 1 to 5 rating
    val deepSleepMinutes: Int? = null,
    val remSleepMinutes: Int? = null,
    val lightSleepMinutes: Int? = null,
    val awakeMinutes: Int? = null,
    val restingHeartRate: Int? = null,
    val hrvScore: Float? = null,
    val source: String = "manual", // "manual" | "wearable" | "whoop" | "apple_health" | "oura"
    val notes: String = "",
    val loggedAt: Long = System.currentTimeMillis()
)
