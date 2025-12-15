package com.example.tracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val categoryId: Long,
    val startTime: Date,
    val endTime: Date? = null,
    val description: String? = null
) {
    val duration: Long
        get() = if (endTime != null) (endTime.time - startTime.time) / 1000 else 0
}
