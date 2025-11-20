package com.example.tracker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface HabitDao {
    @Insert
    suspend fun insert(habit: Habit): Long

    @Update
    suspend fun update(habit: Habit)

    @Delete
    suspend fun delete(habit: Habit)

    @Query("SELECT * FROM habits WHERE startTime BETWEEN :startDate AND :endDate")
    fun getHabitsByDateRange(startDate: Date, endDate: Date): Flow<List<Habit>>

    @Query("SELECT * FROM habits ORDER BY startTime DESC")
    fun getAllHabits(): Flow<List<Habit>>

    @Query("SELECT c.name as categoryName, SUM(h.endTime - h.startTime) as duration FROM habits h JOIN categories c ON h.categoryId = c.id WHERE h.endTime IS NOT NULL AND h.startTime BETWEEN :startDate AND :endDate GROUP BY c.name")
    fun getHabitsByCategory(startDate: Date, endDate: Date): Flow<List<HabitByCategory>>

    @Query("SELECT c.name as categoryName, SUM(h.endTime - h.startTime) as duration FROM habits h JOIN categories c ON h.categoryId = c.id WHERE h.endTime IS NOT NULL GROUP BY c.name")
    fun getAllHabitsByCategory(): Flow<List<HabitByCategory>>
}
