package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weekly_review_records")
data class WeeklyReviewRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val weekLabel: String,
    val startDate: String = "",
    val endDate: String = "",
    val overallScorePercent: Int = 85,
    val strongestCategory: String = "Discipline",
    val weakestCategory: String = "Sleep",
    val winsJson: String = "", // newline or delimiter-separated
    val weaknessesJson: String = "",
    val recommendationsJson: String = "",
    val categoryScoresJson: String = "",
    val isAiGenerated: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "vision_items")
data class VisionItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val section: String = "roadmap_milestone", // "who_i_am", "who_i_want_to_become", "vision_1yr", "vision_3yr", "vision_5yr", "roadmap_milestone"
    val title: String,
    val content: String = "",
    val targetHorizon: String = "1 Year",
    val orderIndex: Int = 0,
    val isCompleted: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)
