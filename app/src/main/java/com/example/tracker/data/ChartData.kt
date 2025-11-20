package com.example.tracker.data

data class ExpenseByCategory(
    val categoryName: String,
    val amount: Double
)

data class HabitByCategory(
    val categoryName: String,
    val duration: Long
)
