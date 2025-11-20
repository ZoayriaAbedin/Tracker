package com.example.tracker.data

import androidx.room.Embedded

data class BudgetWithExpenses(
    @Embedded val budget: Budget,
    val spent: Double
)
