package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "career_goals")
data class CareerGoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val targetRole: String,
    val targetSalaryOrRevenue: String = "",
    val industry: String = "Technology",
    val timeline: String = "1 Year",
    val progress: Float = 0.35f,
    val status: String = "active", // active, achieved, paused
    val keyMilestones: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "career_skills")
data class CareerSkillEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String = "Technical", // Technical, Leadership, Domain, Soft Skills
    val proficiencyPercent: Int = 50, // 0 - 100
    val targetProficiencyPercent: Int = 90,
    val notes: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "certifications")
data class CertificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val issuingOrganization: String,
    val issueDate: String,
    val expiryDate: String = "",
    val credentialUrl: String = "",
    val status: String = "In Progress", // In Progress, Achieved
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "career_projects")
data class CareerProjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val role: String,
    val description: String,
    val keyOutcomes: String,
    val techStack: String,
    val link: String = "",
    val status: String = "In Progress", // In Progress, Completed, Planned
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "career_achievements")
data class CareerAchievementEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val date: String,
    val impactMetric: String,
    val category: String = "Milestone",
    val notes: String = ""
)

@Entity(tableName = "resume_items")
data class ResumeItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val companyOrEntity: String,
    val roleTitle: String,
    val period: String,
    val highlights: String,
    val orderIndex: Int = 0
)
