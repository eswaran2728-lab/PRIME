package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "skincare_products")
data class SkincareProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String, // "Cleanser", "Toner", "Serum", "Moisturizer", "Sunscreen (SPF)", "Exfoliant", "Eye Cream", "Treatment", "Mask", "Other"
    val brand: String = "",
    val routineTime: String = "AM", // "AM", "PM", "BOTH"
    val orderIndex: Int = 0,
    val frequency: String = "Daily", // "Daily", "2-3x / week", "Weekly", "As Needed"
    val startDate: String = "",
    val expiryDate: String = "",
    val notes: String = "",
    val isArchived: Boolean = false
)

@Entity(tableName = "skincare_logs")
data class SkincareLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val routineTime: String, // "AM", "PM"
    val productId: Long,
    val date: String, // YYYY-MM-DD
    val isCompleted: Boolean = true,
    val completedAt: Long = System.currentTimeMillis()
)
