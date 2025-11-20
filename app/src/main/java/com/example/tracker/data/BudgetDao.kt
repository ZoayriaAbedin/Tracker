package com.example.tracker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    @Insert
    suspend fun insert(budget: Budget)

    @Update
    suspend fun update(budget: Budget)

    @Delete
    suspend fun delete(budget: Budget)

    @Query("SELECT * FROM budgets")
    fun getAllBudgets(): Flow<List<Budget>>

    @Query("""
        SELECT b.*, COALESCE(SUM(e.amount), 0.0) as spent 
        FROM budgets b 
        LEFT JOIN expenses e ON b.categoryId = e.categoryId AND e.date BETWEEN b.startDate AND b.endDate
        GROUP BY b.id, b.startDate, b.endDate
    """)
    fun getBudgetsWithExpenses(): Flow<List<BudgetWithExpenses>>
}
