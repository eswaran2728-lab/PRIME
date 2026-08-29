package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey val id: String = "primary_user",
    val name: String = "Eswaran",
    val title: String = "Apex Operator",
    val level: Int = 1,
    val xp: Int = 450,
    val xpToNextLevel: Int = 1000,
    val currentStreak: Int = 1,
    val longestStreak: Int = 1,
    val primaryGoal: String = "Muscle Gain & Recomp", // "Fat Loss", "Maintenance", "Muscle Gain", "Recomp", "Custom"
    val targetCalories: Int = 2400,
    val targetProteinGrams: Float = 160f,
    val targetCarbsGrams: Float = 250f,
    val targetFatGrams: Float = 65f,
    val targetWaterMl: Int = 3000,
    val targetSleepHours: Float = 8.0f,
    val targetFocusMinutesDaily: Int = 120,
    val units: String = "metric", // "metric" | "imperial"
    val themeMode: String = "system", // "dark" | "light" | "system"
    val notificationsEnabled: Boolean = true,
    val privacyPinEnabled: Boolean = false,
    val aiProviderName: String = "Gemini 2.5 Flash",
    val currency: String = "MYR",
    val isOnboardingCompleted: Boolean = true,
    val streakShieldAvailable: Boolean = true,
    val bio: String = "BUILD YOURSELF. EVERY DAY.",
    val source: String = "manual",
    val updatedAt: Long = System.currentTimeMillis()
)
