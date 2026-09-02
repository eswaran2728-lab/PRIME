package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.BodyMeasurementEntity
import com.example.data.local.entities.BodyPhotoEntity
import com.example.data.local.entities.DailyEntryEntity
import com.example.data.local.entities.DailyScoreRecordEntity
import com.example.data.local.entities.ExerciseEntity
import com.example.data.local.entities.FoodItemEntity
import com.example.data.local.entities.HabitEntity
import com.example.data.local.entities.HabitLogEntity
import com.example.data.local.entities.HydrationLogEntity
import com.example.data.local.entities.NutritionLogEntity
import com.example.data.local.entities.ProfileEntity
import com.example.data.local.entities.TaskEntity
import com.example.data.local.entities.TimeBlockEntity
import com.example.data.local.entities.WorkoutSessionEntity
import com.example.data.local.entities.WorkoutSetEntity
import com.example.data.local.entities.WorkoutTemplateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyEntryDao {
    @Query("SELECT * FROM daily_entries WHERE date = :date")
    fun getEntryForDate(date: String): Flow<DailyEntryEntity?>

    @Query("SELECT * FROM daily_entries WHERE date = :date")
    suspend fun getEntryForDateSync(date: String): DailyEntryEntity?

    @Query("SELECT * FROM daily_entries ORDER BY date DESC")
    fun getAllEntries(): Flow<List<DailyEntryEntity>>

    @Query("SELECT * FROM daily_entries ORDER BY date DESC")
    suspend fun getAllEntriesSync(): List<DailyEntryEntity>

    @Query("SELECT * FROM daily_entries ORDER BY date DESC LIMIT 30")
    fun getRecentEntries(): Flow<List<DailyEntryEntity>>

    @Query("SELECT * FROM daily_entries ORDER BY date DESC LIMIT :limit")
    suspend fun getRecentEntriesSync(limit: Int): List<DailyEntryEntity>

    @Query("SELECT COUNT(*) FROM daily_entries")
    fun getTotalLoggedDaysCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM daily_entries")
    suspend fun getTotalLoggedDaysCountSync(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(entry: DailyEntryEntity)

    @Query("DELETE FROM daily_entries")
    suspend fun deleteAll()
}

@Dao
interface ProfileDao {
    @Query("SELECT * FROM profiles WHERE id = 'primary_user'")
    fun getProfile(): Flow<ProfileEntity?>

    @Query("SELECT * FROM profiles WHERE id = 'primary_user'")
    suspend fun getProfileSync(): ProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(profile: ProfileEntity)
}

@Dao
interface BodyTrackingDao {
    @Query("SELECT * FROM body_measurements ORDER BY date DESC, timestamp DESC")
    fun getAllMeasurements(): Flow<List<BodyMeasurementEntity>>

    @Query("SELECT * FROM body_measurements ORDER BY date DESC LIMIT 1")
    fun getLatestMeasurement(): Flow<BodyMeasurementEntity?>

    @Query("SELECT * FROM body_measurements ORDER BY date DESC, timestamp DESC")
    suspend fun getAllMeasurementsSync(): List<BodyMeasurementEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeasurement(measurement: BodyMeasurementEntity): Long

    @Delete
    suspend fun deleteMeasurement(measurement: BodyMeasurementEntity)

    @Query("SELECT * FROM body_photos ORDER BY date DESC, timestamp DESC")
    fun getAllPhotos(): Flow<List<BodyPhotoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: BodyPhotoEntity): Long

    @Delete
    suspend fun deletePhoto(photo: BodyPhotoEntity)

    @Query("DELETE FROM body_measurements")
    suspend fun deleteAllMeasurements()

    @Query("DELETE FROM body_photos")
    suspend fun deleteAllPhotos()
}

@Dao
interface WorkoutDao {
    @Query("SELECT * FROM workout_templates ORDER BY isStarter DESC, name ASC")
    fun getAllTemplates(): Flow<List<WorkoutTemplateEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplate(template: WorkoutTemplateEntity): Long

    @Delete
    suspend fun deleteTemplate(template: WorkoutTemplateEntity)

    @Query("SELECT * FROM exercises ORDER BY muscleGroup ASC, name ASC")
    fun getAllExercises(): Flow<List<ExerciseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: ExerciseEntity): Long

    @Update
    suspend fun updateExercise(exercise: ExerciseEntity)

    @Query("SELECT * FROM workout_sessions ORDER BY date DESC, startTime DESC")
    fun getAllSessions(): Flow<List<WorkoutSessionEntity>>

    @Query("SELECT * FROM workout_sessions ORDER BY date DESC, startTime DESC")
    suspend fun getAllSessionsSync(): List<WorkoutSessionEntity>

    @Query("SELECT * FROM workout_sessions WHERE date = :date ORDER BY startTime DESC")
    fun getSessionsForDate(date: String): Flow<List<WorkoutSessionEntity>>

    @Query("SELECT * FROM workout_sessions WHERE date = :date ORDER BY startTime DESC")
    suspend fun getSessionsForDateSync(date: String): List<WorkoutSessionEntity>

    @Query("SELECT * FROM workout_sessions WHERE id = :sessionId")
    fun getSessionById(sessionId: Long): Flow<WorkoutSessionEntity?>

    @Query("SELECT * FROM workout_sessions WHERE id = :sessionId")
    suspend fun getSessionByIdSync(sessionId: Long): WorkoutSessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: WorkoutSessionEntity): Long

    @Update
    suspend fun updateSession(session: WorkoutSessionEntity)

    @Delete
    suspend fun deleteSession(session: WorkoutSessionEntity)

    @Query("SELECT * FROM workout_sets WHERE sessionId = :sessionId ORDER BY setNumber ASC")
    fun getSetsForSession(sessionId: Long): Flow<List<WorkoutSetEntity>>

    @Query("SELECT * FROM workout_sets WHERE sessionId = :sessionId ORDER BY setNumber ASC")
    suspend fun getSetsForSessionSync(sessionId: Long): List<WorkoutSetEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSet(set: WorkoutSetEntity): Long

    @Update
    suspend fun updateSet(set: WorkoutSetEntity)

    @Delete
    suspend fun deleteSet(set: WorkoutSetEntity)

    @Query("DELETE FROM workout_sets WHERE sessionId = :sessionId")
    suspend fun deleteSetsForSession(sessionId: Long)

    @Query("DELETE FROM workout_sessions")
    suspend fun deleteAllSessions()

    @Query("DELETE FROM workout_sets")
    suspend fun deleteAllSets()
}

@Dao
interface NutritionDao {
    @Query("SELECT * FROM foods ORDER BY isFavorite DESC, name ASC")
    fun getAllFoods(): Flow<List<FoodItemEntity>>

    @Query("SELECT * FROM foods WHERE isFavorite = 1 ORDER BY name ASC")
    fun getFavoriteFoods(): Flow<List<FoodItemEntity>>

    @Query("SELECT * FROM foods WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchFoods(query: String): Flow<List<FoodItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFood(food: FoodItemEntity): Long

    @Update
    suspend fun updateFood(food: FoodItemEntity)

    @Delete
    suspend fun deleteFood(food: FoodItemEntity)

    @Query("SELECT * FROM nutrition_logs WHERE date = :date ORDER BY loggedAt ASC")
    fun getNutritionLogsForDate(date: String): Flow<List<NutritionLogEntity>>

    @Query("SELECT * FROM nutrition_logs WHERE date = :date ORDER BY loggedAt ASC")
    suspend fun getNutritionLogsForDateSync(date: String): List<NutritionLogEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNutritionLog(log: NutritionLogEntity): Long

    @Delete
    suspend fun deleteNutritionLog(log: NutritionLogEntity)

    @Query("SELECT * FROM hydration_logs WHERE date = :date ORDER BY loggedAt ASC")
    fun getHydrationLogsForDate(date: String): Flow<List<HydrationLogEntity>>

    @Query("SELECT * FROM hydration_logs WHERE date = :date ORDER BY loggedAt ASC")
    suspend fun getHydrationLogsForDateSync(date: String): List<HydrationLogEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHydrationLog(log: HydrationLogEntity): Long

    @Query("DELETE FROM hydration_logs WHERE id = (SELECT id FROM hydration_logs WHERE date = :date ORDER BY loggedAt DESC LIMIT 1)")
    suspend fun deleteLatestHydrationForDate(date: String)

    @Query("DELETE FROM nutrition_logs")
    suspend fun deleteAllNutritionLogs()

    @Query("DELETE FROM hydration_logs")
    suspend fun deleteAllHydrationLogs()
}

@Dao
interface HabitDao {
    @Query("SELECT * FROM habits WHERE isArchived = 0 ORDER BY category ASC, name ASC")
    fun getAllActiveHabits(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits WHERE isArchived = 0 ORDER BY category ASC, name ASC")
    suspend fun getAllActiveHabitsSync(): List<HabitEntity>

    @Query("SELECT * FROM habits WHERE id = :id")
    fun getHabitById(id: Long): Flow<HabitEntity?>

    @Query("SELECT * FROM habits WHERE id = :id")
    suspend fun getHabitByIdSync(id: Long): HabitEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitEntity): Long

    @Update
    suspend fun updateHabit(habit: HabitEntity)

    @Delete
    suspend fun deleteHabit(habit: HabitEntity)

    @Query("SELECT * FROM habit_logs WHERE date = :date")
    fun getHabitLogsForDate(date: String): Flow<List<HabitLogEntity>>

    @Query("SELECT * FROM habit_logs WHERE date = :date")
    suspend fun getHabitLogsForDateSync(date: String): List<HabitLogEntity>

    @Query("SELECT * FROM habit_logs WHERE date IN (:dates)")
    fun getHabitLogsForDates(dates: List<String>): Flow<List<HabitLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabitLog(log: HabitLogEntity): Long

    @Query("DELETE FROM habit_logs WHERE habitId = :habitId AND date = :date")
    suspend fun deleteHabitLog(habitId: Long, date: String)

    @Query("DELETE FROM habits")
    suspend fun deleteAllHabits()

    @Query("DELETE FROM habit_logs")
    suspend fun deleteAllHabitLogs()
}

@Dao
interface PlannerDao {
    @Query("SELECT * FROM tasks WHERE date = :date ORDER BY priorityLevel ASC, createdAt ASC")
    fun getTasksForDate(date: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE date = :date ORDER BY priorityLevel ASC, createdAt ASC")
    suspend fun getTasksForDateSync(date: String): List<TaskEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("SELECT * FROM time_blocks WHERE date = :date ORDER BY startTime ASC")
    fun getTimeBlocksForDate(date: String): Flow<List<TimeBlockEntity>>

    @Query("SELECT * FROM time_blocks WHERE date = :date ORDER BY startTime ASC")
    suspend fun getTimeBlocksForDateSync(date: String): List<TimeBlockEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimeBlock(block: TimeBlockEntity): Long

    @Update
    suspend fun updateTimeBlock(block: TimeBlockEntity)

    @Delete
    suspend fun deleteTimeBlock(block: TimeBlockEntity)

    @Query("DELETE FROM tasks")
    suspend fun deleteAllTasks()

    @Query("DELETE FROM time_blocks")
    suspend fun deleteAllTimeBlocks()
}

@Dao
interface DailyScoreDao {
    @Query("SELECT * FROM daily_scores WHERE date = :date")
    fun getScoreForDate(date: String): Flow<DailyScoreRecordEntity?>

    @Query("SELECT * FROM daily_scores ORDER BY date DESC LIMIT 30")
    fun getRecentScores(): Flow<List<DailyScoreRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScore(score: DailyScoreRecordEntity)

    @Query("DELETE FROM daily_scores")
    suspend fun deleteAllScores()
}
