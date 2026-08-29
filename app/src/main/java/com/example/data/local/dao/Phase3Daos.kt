package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.AchievementBadgeEntity
import com.example.data.local.entities.BookEntity
import com.example.data.local.entities.CareerAchievementEntity
import com.example.data.local.entities.CareerGoalEntity
import com.example.data.local.entities.CareerProjectEntity
import com.example.data.local.entities.CareerSkillEntity
import com.example.data.local.entities.CertificationEntity
import com.example.data.local.entities.CourseEntity
import com.example.data.local.entities.FinanceTransactionEntity
import com.example.data.local.entities.FinancialGoalEntity
import com.example.data.local.entities.ResumeItemEntity
import com.example.data.local.entities.StudySessionEntity
import com.example.data.local.entities.VisionItemEntity
import com.example.data.local.entities.WeeklyReviewRecordEntity
import com.example.data.local.entities.XpTransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CareerDao {
    @Query("SELECT * FROM career_goals ORDER BY createdAt DESC")
    fun getAllCareerGoals(): Flow<List<CareerGoalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCareerGoal(goal: CareerGoalEntity): Long

    @Update
    suspend fun updateCareerGoal(goal: CareerGoalEntity)

    @Delete
    suspend fun deleteCareerGoal(goal: CareerGoalEntity)

    @Query("SELECT * FROM career_skills ORDER BY proficiencyPercent DESC")
    fun getAllCareerSkills(): Flow<List<CareerSkillEntity>>

    @Query("SELECT * FROM career_skills")
    suspend fun getAllSkillsSync(): List<CareerSkillEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCareerSkill(skill: CareerSkillEntity): Long

    @Update
    suspend fun updateCareerSkill(skill: CareerSkillEntity)

    @Delete
    suspend fun deleteCareerSkill(skill: CareerSkillEntity)

    @Query("SELECT * FROM certifications ORDER BY issueDate DESC")
    fun getAllCertifications(): Flow<List<CertificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCertification(cert: CertificationEntity): Long

    @Update
    suspend fun updateCertification(cert: CertificationEntity)

    @Delete
    suspend fun deleteCertification(cert: CertificationEntity)

    @Query("SELECT * FROM career_projects ORDER BY createdAt DESC")
    fun getAllCareerProjects(): Flow<List<CareerProjectEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCareerProject(project: CareerProjectEntity): Long

    @Update
    suspend fun updateCareerProject(project: CareerProjectEntity)

    @Delete
    suspend fun deleteCareerProject(project: CareerProjectEntity)

    @Query("SELECT * FROM career_achievements ORDER BY date DESC")
    fun getAllCareerAchievements(): Flow<List<CareerAchievementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCareerAchievement(achievement: CareerAchievementEntity): Long

    @Delete
    suspend fun deleteCareerAchievement(achievement: CareerAchievementEntity)

    @Query("SELECT * FROM resume_items ORDER BY orderIndex ASC")
    fun getAllResumeItems(): Flow<List<ResumeItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResumeItem(item: ResumeItemEntity): Long

    @Delete
    suspend fun deleteResumeItem(item: ResumeItemEntity)

    @Query("DELETE FROM career_goals")
    suspend fun deleteAllGoals()

    @Query("DELETE FROM career_skills")
    suspend fun deleteAllSkills()

    @Query("DELETE FROM certifications")
    suspend fun deleteAllCerts()

    @Query("DELETE FROM career_projects")
    suspend fun deleteAllProjects()

    @Query("DELETE FROM career_achievements")
    suspend fun deleteAllAchievements()

    @Query("DELETE FROM resume_items")
    suspend fun deleteAllResumeItems()
}

@Dao
interface LearningDao {
    @Query("SELECT * FROM learning_courses ORDER BY updatedAt DESC")
    fun getAllCourses(): Flow<List<CourseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: CourseEntity): Long

    @Update
    suspend fun updateCourse(course: CourseEntity)

    @Delete
    suspend fun deleteCourse(course: CourseEntity)

    @Query("SELECT * FROM learning_books ORDER BY updatedAt DESC")
    fun getAllBooks(): Flow<List<BookEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: BookEntity): Long

    @Update
    suspend fun updateBook(book: BookEntity)

    @Delete
    suspend fun deleteBook(book: BookEntity)

    @Query("SELECT * FROM study_sessions ORDER BY createdAt DESC")
    fun getAllStudySessions(): Flow<List<StudySessionEntity>>

    @Query("SELECT * FROM study_sessions WHERE date = :date ORDER BY createdAt DESC")
    fun getStudySessionsForDate(date: String): Flow<List<StudySessionEntity>>

    @Query("SELECT * FROM study_sessions WHERE date = :date")
    suspend fun getStudySessionsForDateSync(date: String): List<StudySessionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudySession(session: StudySessionEntity): Long

    @Delete
    suspend fun deleteStudySession(session: StudySessionEntity)

    @Query("DELETE FROM learning_courses")
    suspend fun deleteAllCourses()

    @Query("DELETE FROM learning_books")
    suspend fun deleteAllBooks()

    @Query("DELETE FROM study_sessions")
    suspend fun deleteAllStudySessions()
}

@Dao
interface FinanceDao {
    @Query("SELECT * FROM finance_transactions ORDER BY date DESC, createdAt DESC")
    fun getAllTransactions(): Flow<List<FinanceTransactionEntity>>

    @Query("SELECT * FROM finance_transactions WHERE date LIKE :yearMonth || '%' ORDER BY date DESC")
    fun getTransactionsForMonth(yearMonth: String): Flow<List<FinanceTransactionEntity>>

    @Query("SELECT * FROM finance_transactions WHERE date = :date")
    suspend fun getTransactionsForDateSync(date: String): List<FinanceTransactionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(tx: FinanceTransactionEntity): Long

    @Delete
    suspend fun deleteTransaction(tx: FinanceTransactionEntity)

    @Query("SELECT * FROM financial_goals ORDER BY priority ASC, createdAt DESC")
    fun getAllFinancialGoals(): Flow<List<FinancialGoalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFinancialGoal(goal: FinancialGoalEntity): Long

    @Update
    suspend fun updateFinancialGoal(goal: FinancialGoalEntity)

    @Delete
    suspend fun deleteFinancialGoal(goal: FinancialGoalEntity)

    @Query("DELETE FROM finance_transactions")
    suspend fun deleteAllTransactions()

    @Query("DELETE FROM financial_goals")
    suspend fun deleteAllGoals()
}

@Dao
interface GamificationDao {
    @Query("SELECT * FROM achievement_badges ORDER BY isUnlocked DESC, category ASC")
    fun getAllBadges(): Flow<List<AchievementBadgeEntity>>

    @Query("SELECT * FROM achievement_badges WHERE isUnlocked = 1")
    fun getUnlockedBadges(): Flow<List<AchievementBadgeEntity>>

    @Query("SELECT * FROM achievement_badges WHERE id = :id LIMIT 1")
    suspend fun getBadgeById(id: String): AchievementBadgeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBadge(badge: AchievementBadgeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBadges(badges: List<AchievementBadgeEntity>)

    @Update
    suspend fun updateBadge(badge: AchievementBadgeEntity)

    @Query("SELECT * FROM xp_transactions ORDER BY timestamp DESC LIMIT 50")
    fun getRecentXpTransactions(): Flow<List<XpTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertXpTransaction(tx: XpTransactionEntity): Long

    @Query("DELETE FROM achievement_badges")
    suspend fun deleteAllBadges()

    @Query("DELETE FROM xp_transactions")
    suspend fun deleteAllXpTransactions()
}

@Dao
interface VisionAndReviewDao {
    @Query("SELECT * FROM weekly_review_records ORDER BY createdAt DESC")
    fun getAllWeeklyReviews(): Flow<List<WeeklyReviewRecordEntity>>

    @Query("SELECT * FROM weekly_review_records ORDER BY createdAt DESC LIMIT 1")
    fun getLatestWeeklyReview(): Flow<WeeklyReviewRecordEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeeklyReview(review: WeeklyReviewRecordEntity): Long

    @Query("SELECT * FROM vision_items ORDER BY orderIndex ASC, updatedAt DESC")
    fun getAllVisionItems(): Flow<List<VisionItemEntity>>

    @Query("SELECT * FROM vision_items WHERE section = :section ORDER BY orderIndex ASC")
    fun getVisionItemsBySection(section: String): Flow<List<VisionItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVisionItem(item: VisionItemEntity): Long

    @Update
    suspend fun updateVisionItem(item: VisionItemEntity)

    @Delete
    suspend fun deleteVisionItem(item: VisionItemEntity)

    @Query("DELETE FROM weekly_review_records")
    suspend fun deleteAllWeeklyReviews()

    @Query("DELETE FROM vision_items")
    suspend fun deleteAllVisionItems()
}
