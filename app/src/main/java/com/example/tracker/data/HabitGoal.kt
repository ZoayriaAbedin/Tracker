package com.example.tracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habit_goals")
data class HabitGoal(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val categoryId: Long,
    val targetTime: Long,
    val timePeriod: String,
    val trackStreak: Boolean
)
