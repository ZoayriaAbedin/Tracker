package com.example.tracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class CategoryType {
    EXPENSE,
    HABIT
}

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: CategoryType
)
