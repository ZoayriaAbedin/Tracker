package com.example.tracker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitGoalDao {
    @Insert
    suspend fun insert(habitGoal: HabitGoal)

    @Update
    suspend fun update(habitGoal: HabitGoal)

    @Delete
    suspend fun delete(habitGoal: HabitGoal)

    @Query("SELECT * FROM habit_goals")
    fun getAllHabitGoals(): Flow<List<HabitGoal>>
}
