package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "finance_transactions")
data class FinanceTransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val type: String, // "Income", "Expense", "Business Income", "Business Expense", "Savings"
    val category: String, // "Salary", "Freelance", "Business", "Investments", "Housing", "Food", "Transport", "Utilities", "Health & Fitness", "Tech & Tools", "Personal", "Other"
    val amount: Double,
    val currency: String = "MYR",
    val notes: String = "",
    val isRecurring: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "financial_goals")
data class FinancialGoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val goalType: String = "Savings", // "Emergency Fund", "Savings Goal", "Business Goal", "Major Purchase"
    val targetAmount: Double,
    val currentAmount: Double = 0.0,
    val currency: String = "MYR",
    val deadline: String = "",
    val priority: String = "High", // "High", "Medium", "Low"
    val status: String = "Active", // "Active", "Achieved", "Paused"
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
