package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.AiChatMessageEntity
import com.example.data.local.entities.AiUsageStatsEntity
import com.example.data.local.entities.FocusDistractionEntity
import com.example.data.local.entities.FocusSessionEntity
import com.example.data.local.entities.GoalEntity
import com.example.data.local.entities.GoalMilestoneEntity
import com.example.data.local.entities.GroomingLogEntity
import com.example.data.local.entities.GroomingRoutineItemEntity
import com.example.data.local.entities.JournalEntryEntity
import com.example.data.local.entities.MindsetLogEntity
import com.example.data.local.entities.SkincareLogEntity
import com.example.data.local.entities.SkincareProductEntity
import com.example.data.local.entities.SleepLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GroomingDao {
    @Query("SELECT * FROM grooming_routine_items WHERE isArchived = 0 ORDER BY routineType ASC, orderIndex ASC")
    fun getAllRoutineItems(): Flow<List<GroomingRoutineItemEntity>>

    @Query("SELECT * FROM grooming_routine_items WHERE routineType = :routineType AND isArchived = 0 ORDER BY orderIndex ASC")
    fun getItemsForRoutine(routineType: String): Flow<List<GroomingRoutineItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: GroomingRoutineItemEntity): Long

    @Update
    suspend fun updateItem(item: GroomingRoutineItemEntity)

    @Delete
    suspend fun deleteItem(item: GroomingRoutineItemEntity)

    @Query("SELECT * FROM grooming_logs WHERE date = :date")
    fun getLogsForDate(date: String): Flow<List<GroomingLogEntity>>

    @Query("SELECT * FROM grooming_logs WHERE date = :date")
    suspend fun getLogsForDateSync(date: String): List<GroomingLogEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: GroomingLogEntity): Long

    @Query("DELETE FROM grooming_logs WHERE itemId = :itemId AND date = :date")
    suspend fun deleteLog(itemId: Long, date: String)

    @Query("DELETE FROM grooming_routine_items")
    suspend fun deleteAllItems()

    @Query("DELETE FROM grooming_logs")
    suspend fun deleteAllLogs()
}

@Dao
interface SkincareDao {
    @Query("SELECT * FROM skincare_products WHERE isArchived = 0 ORDER BY routineTime ASC, orderIndex ASC")
    fun getAllProducts(): Flow<List<SkincareProductEntity>>

    @Query("SELECT * FROM skincare_products WHERE (routineTime = :routineTime OR routineTime = 'BOTH') AND isArchived = 0 ORDER BY orderIndex ASC")
    fun getProductsForRoutine(routineTime: String): Flow<List<SkincareProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: SkincareProductEntity): Long

    @Update
    suspend fun updateProduct(product: SkincareProductEntity)

    @Delete
    suspend fun deleteProduct(product: SkincareProductEntity)

    @Query("SELECT * FROM skincare_logs WHERE date = :date")
    fun getLogsForDate(date: String): Flow<List<SkincareLogEntity>>

    @Query("SELECT * FROM skincare_logs WHERE date = :date")
    suspend fun getLogsForDateSync(date: String): List<SkincareLogEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: SkincareLogEntity): Long

    @Query("DELETE FROM skincare_logs WHERE productId = :productId AND date = :date AND routineTime = :routineTime")
    suspend fun deleteLog(productId: Long, date: String, routineTime: String)

    @Query("DELETE FROM skincare_products")
    suspend fun deleteAllProducts()

    @Query("DELETE FROM skincare_logs")
    suspend fun deleteAllLogs()
}

@Dao
interface SleepDao {
    @Query("SELECT * FROM sleep_logs WHERE date = :date LIMIT 1")
    fun getSleepLogForDate(date: String): Flow<SleepLogEntity?>

    @Query("SELECT * FROM sleep_logs WHERE date = :date LIMIT 1")
    suspend fun getSleepLogForDateSync(date: String): SleepLogEntity?

    @Query("SELECT * FROM sleep_logs ORDER BY date DESC LIMIT 30")
    fun getRecentSleepLogs(): Flow<List<SleepLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSleepLog(log: SleepLogEntity): Long

    @Delete
    suspend fun deleteSleepLog(log: SleepLogEntity)

    @Query("DELETE FROM sleep_logs")
    suspend fun deleteAllSleepLogs()
}

@Dao
interface MindsetDao {
    @Query("SELECT * FROM mindset_logs WHERE date = :date LIMIT 1")
    fun getMindsetLogForDate(date: String): Flow<MindsetLogEntity?>

    @Query("SELECT * FROM mindset_logs WHERE date = :date LIMIT 1")
    suspend fun getMindsetLogForDateSync(date: String): MindsetLogEntity?

    @Query("SELECT * FROM mindset_logs ORDER BY date DESC LIMIT 30")
    fun getRecentMindsetLogs(): Flow<List<MindsetLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMindsetLog(log: MindsetLogEntity): Long

    @Query("SELECT * FROM journal_entries WHERE date = :date LIMIT 1")
    fun getJournalForDate(date: String): Flow<JournalEntryEntity?>

    @Query("SELECT * FROM journal_entries WHERE date = :date LIMIT 1")
    suspend fun getJournalForDateSync(date: String): JournalEntryEntity?

    @Query("SELECT * FROM journal_entries ORDER BY date DESC LIMIT 30")
    fun getRecentJournalEntries(): Flow<List<JournalEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJournalEntry(entry: JournalEntryEntity): Long

    @Delete
    suspend fun deleteJournalEntry(entry: JournalEntryEntity)

    @Query("DELETE FROM mindset_logs")
    suspend fun deleteAllMindsetLogs()

    @Query("DELETE FROM journal_entries")
    suspend fun deleteAllJournalEntries()
}

@Dao
interface FocusDao {
    @Query("SELECT * FROM focus_sessions WHERE date = :date ORDER BY startTime DESC")
    fun getSessionsForDate(date: String): Flow<List<FocusSessionEntity>>

    @Query("SELECT * FROM focus_sessions WHERE date = :date ORDER BY startTime DESC")
    suspend fun getSessionsForDateSync(date: String): List<FocusSessionEntity>

    @Query("SELECT * FROM focus_sessions ORDER BY startTime DESC LIMIT 50")
    fun getRecentSessions(): Flow<List<FocusSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: FocusSessionEntity): Long

    @Update
    suspend fun updateSession(session: FocusSessionEntity)

    @Delete
    suspend fun deleteSession(session: FocusSessionEntity)

    @Query("SELECT * FROM focus_distractions WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    fun getDistractionsForSession(sessionId: Long): Flow<List<FocusDistractionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDistraction(distraction: FocusDistractionEntity): Long

    @Query("DELETE FROM focus_sessions")
    suspend fun deleteAllSessions()

    @Query("DELETE FROM focus_distractions")
    suspend fun deleteAllDistractions()
}

@Dao
interface GoalDao {
    @Query("SELECT * FROM goals ORDER BY isCompleted ASC, deadline ASC, priority DESC")
    fun getAllGoals(): Flow<List<GoalEntity>>

    @Query("SELECT * FROM goals ORDER BY isCompleted ASC, deadline ASC, priority DESC")
    suspend fun getAllGoalsSync(): List<GoalEntity>

    @Query("SELECT * FROM goals WHERE level = :level ORDER BY isCompleted ASC, deadline ASC")
    fun getGoalsByLevel(level: String): Flow<List<GoalEntity>>

    @Query("SELECT * FROM goals WHERE id = :goalId")
    fun getGoalById(goalId: Long): Flow<GoalEntity?>

    @Query("SELECT * FROM goals WHERE id = :goalId")
    suspend fun getGoalByIdSync(goalId: Long): GoalEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: GoalEntity): Long

    @Update
    suspend fun updateGoal(goal: GoalEntity)

    @Delete
    suspend fun deleteGoal(goal: GoalEntity)

    @Query("SELECT * FROM goal_milestones WHERE goalId = :goalId ORDER BY orderIndex ASC")
    fun getMilestonesForGoal(goalId: Long): Flow<List<GoalMilestoneEntity>>

    @Query("SELECT * FROM goal_milestones WHERE goalId = :goalId ORDER BY orderIndex ASC")
    suspend fun getMilestonesForGoalSync(goalId: Long): List<GoalMilestoneEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMilestone(milestone: GoalMilestoneEntity): Long

    @Update
    suspend fun updateMilestone(milestone: GoalMilestoneEntity)

    @Delete
    suspend fun deleteMilestone(milestone: GoalMilestoneEntity)

    @Query("DELETE FROM goal_milestones WHERE goalId = :goalId")
    suspend fun deleteMilestonesForGoal(goalId: Long)

    @Query("DELETE FROM goals")
    suspend fun deleteAllGoals()

    @Query("DELETE FROM goal_milestones")
    suspend fun deleteAllMilestones()
}

@Dao
interface AiDao {
    @Query("SELECT * FROM ai_chat_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<AiChatMessageEntity>>

    @Query("SELECT * FROM ai_chat_messages WHERE assistantType = :assistantType ORDER BY timestamp ASC")
    fun getMessagesForAssistant(assistantType: String): Flow<List<AiChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: AiChatMessageEntity): Long

    @Query("DELETE FROM ai_chat_messages WHERE assistantType = :assistantType")
    suspend fun clearMessagesForAssistant(assistantType: String)

    @Query("DELETE FROM ai_chat_messages")
    suspend fun clearAllMessages()

    @Query("SELECT * FROM ai_usage_stats WHERE date = :date LIMIT 1")
    fun getUsageForDate(date: String): Flow<AiUsageStatsEntity?>

    @Query("SELECT * FROM ai_usage_stats WHERE date = :date LIMIT 1")
    suspend fun getUsageForDateSync(date: String): AiUsageStatsEntity?

    @Query("SELECT SUM(requestsCount) FROM ai_usage_stats")
    suspend fun getTotalRequestsCount(): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUsage(stats: AiUsageStatsEntity)
}
