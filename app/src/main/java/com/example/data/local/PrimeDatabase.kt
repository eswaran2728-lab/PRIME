package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.AiDao
import com.example.data.local.dao.BodyTrackingDao
import com.example.data.local.dao.CareerDao
import com.example.data.local.dao.DailyEntryDao
import com.example.data.local.dao.DailyScoreDao
import com.example.data.local.dao.FinanceDao
import com.example.data.local.dao.FocusDao
import com.example.data.local.dao.GamificationDao
import com.example.data.local.dao.GoalDao
import com.example.data.local.dao.GroomingDao
import com.example.data.local.dao.HabitDao
import com.example.data.local.dao.LearningDao
import com.example.data.local.dao.MindsetDao
import com.example.data.local.dao.NutritionDao
import com.example.data.local.dao.PlannerDao
import com.example.data.local.dao.ProfileDao
import com.example.data.local.dao.SkincareDao
import com.example.data.local.dao.SleepDao
import com.example.data.local.dao.VisionAndReviewDao
import com.example.data.local.dao.WorkoutDao
import com.example.data.local.entities.AchievementBadgeEntity
import com.example.data.local.entities.AiChatMessageEntity
import com.example.data.local.entities.AiUsageStatsEntity
import com.example.data.local.entities.BodyMeasurementEntity
import com.example.data.local.entities.BodyPhotoEntity
import com.example.data.local.entities.BookEntity
import com.example.data.local.entities.CareerAchievementEntity
import com.example.data.local.entities.CareerGoalEntity
import com.example.data.local.entities.CareerProjectEntity
import com.example.data.local.entities.CareerSkillEntity
import com.example.data.local.entities.CertificationEntity
import com.example.data.local.entities.CourseEntity
import com.example.data.local.entities.DailyEntryEntity
import com.example.data.local.entities.DailyScoreRecordEntity
import com.example.data.local.entities.ExerciseEntity
import com.example.data.local.entities.FinanceTransactionEntity
import com.example.data.local.entities.FinancialGoalEntity
import com.example.data.local.entities.FocusDistractionEntity
import com.example.data.local.entities.FocusSessionEntity
import com.example.data.local.entities.FoodItemEntity
import com.example.data.local.entities.GoalEntity
import com.example.data.local.entities.GoalMilestoneEntity
import com.example.data.local.entities.GroomingLogEntity
import com.example.data.local.entities.GroomingRoutineItemEntity
import com.example.data.local.entities.HabitEntity
import com.example.data.local.entities.HabitLogEntity
import com.example.data.local.entities.HydrationLogEntity
import com.example.data.local.entities.JournalEntryEntity
import com.example.data.local.entities.MindsetLogEntity
import com.example.data.local.entities.NutritionLogEntity
import com.example.data.local.entities.ProfileEntity
import com.example.data.local.entities.ResumeItemEntity
import com.example.data.local.entities.SkincareLogEntity
import com.example.data.local.entities.SkincareProductEntity
import com.example.data.local.entities.SleepLogEntity
import com.example.data.local.entities.StudySessionEntity
import com.example.data.local.entities.TaskEntity
import com.example.data.local.entities.TimeBlockEntity
import com.example.data.local.entities.VisionItemEntity
import com.example.data.local.entities.WeeklyReviewRecordEntity
import com.example.data.local.entities.WorkoutSessionEntity
import com.example.data.local.entities.WorkoutSetEntity
import com.example.data.local.entities.WorkoutTemplateEntity
import com.example.data.local.entities.XpTransactionEntity

@Database(
    entities = [
        DailyEntryEntity::class,
        ProfileEntity::class,
        BodyMeasurementEntity::class,
        BodyPhotoEntity::class,
        WorkoutTemplateEntity::class,
        ExerciseEntity::class,
        WorkoutSessionEntity::class,
        WorkoutSetEntity::class,
        FoodItemEntity::class,
        NutritionLogEntity::class,
        HydrationLogEntity::class,
        HabitEntity::class,
        HabitLogEntity::class,
        TaskEntity::class,
        TimeBlockEntity::class,
        DailyScoreRecordEntity::class,
        GroomingRoutineItemEntity::class,
        GroomingLogEntity::class,
        SkincareProductEntity::class,
        SkincareLogEntity::class,
        SleepLogEntity::class,
        MindsetLogEntity::class,
        JournalEntryEntity::class,
        FocusSessionEntity::class,
        FocusDistractionEntity::class,
        GoalEntity::class,
        GoalMilestoneEntity::class,
        AiChatMessageEntity::class,
        AiUsageStatsEntity::class,
        CareerGoalEntity::class,
        CareerSkillEntity::class,
        CertificationEntity::class,
        CareerProjectEntity::class,
        CareerAchievementEntity::class,
        ResumeItemEntity::class,
        CourseEntity::class,
        BookEntity::class,
        StudySessionEntity::class,
        FinanceTransactionEntity::class,
        FinancialGoalEntity::class,
        AchievementBadgeEntity::class,
        XpTransactionEntity::class,
        WeeklyReviewRecordEntity::class,
        VisionItemEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class PrimeDatabase : RoomDatabase() {
    abstract fun dailyEntryDao(): DailyEntryDao
    abstract fun profileDao(): ProfileDao
    abstract fun bodyTrackingDao(): BodyTrackingDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun nutritionDao(): NutritionDao
    abstract fun habitDao(): HabitDao
    abstract fun plannerDao(): PlannerDao
    abstract fun dailyScoreDao(): DailyScoreDao
    abstract fun groomingDao(): GroomingDao
    abstract fun skincareDao(): SkincareDao
    abstract fun sleepDao(): SleepDao
    abstract fun mindsetDao(): MindsetDao
    abstract fun focusDao(): FocusDao
    abstract fun goalDao(): GoalDao
    abstract fun aiDao(): AiDao
    abstract fun careerDao(): CareerDao
    abstract fun learningDao(): LearningDao
    abstract fun financeDao(): FinanceDao
    abstract fun gamificationDao(): GamificationDao
    abstract fun visionAndReviewDao(): VisionAndReviewDao

    companion object {
        @Volatile
        private var INSTANCE: PrimeDatabase? = null

        fun getInstance(context: Context): PrimeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PrimeDatabase::class.java,
                    "prime_os_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
