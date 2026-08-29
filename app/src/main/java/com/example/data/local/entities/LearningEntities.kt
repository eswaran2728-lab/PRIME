package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "learning_courses")
data class CourseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val platform: String,
    val instructor: String = "",
    val totalModules: Int = 10,
    val completedModules: Int = 0,
    val status: String = "In Progress", // Not Started, In Progress, Completed
    val certificateUrl: String = "",
    val notes: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "learning_books")
data class BookEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val author: String,
    val format: String = "Physical", // Physical, Kindle, Audiobook
    val totalPages: Int = 300,
    val currentPage: Int = 0,
    val rating: Int = 5,
    val keyTakeaways: String = "",
    val status: String = "Reading", // To Read, Reading, Finished
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "study_sessions")
data class StudySessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val topic: String,
    val category: String = "Deep Study",
    val durationMinutes: Int = 45,
    val keyInsights: String = "",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
