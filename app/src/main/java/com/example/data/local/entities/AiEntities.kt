package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ai_chat_messages")
data class AiChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sender: String, // "user", "assistant"
    val content: String,
    val assistantType: String = "general", // "coach", "nutrition", "workout", "schedule", "goal", "journal", "motivation"
    val suggestedActionTitle: String? = null,
    val suggestedActionPayload: String? = null,
    val suggestedActionType: String? = null, // "ADD_TASK", "SCHEDULE_BLOCK", "LOG_WATER", "LOG_FOOD"
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "ai_usage_stats")
data class AiUsageStatsEntity(
    @PrimaryKey val date: String, // YYYY-MM-DD
    val requestsCount: Int = 0,
    val errorsCount: Int = 0,
    val rateLimitHits: Int = 0,
    val tokensEstimated: Int = 0,
    val updatedAt: Long = System.currentTimeMillis()
)
