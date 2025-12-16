package com.example.tracker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface ExpenseDao {
    @Insert
    suspend fun insert(expense: Expense)

    @Update
    suspend fun update(expense: Expense)

    @Delete
    suspend fun delete(expense: Expense)

    @Query("DELETE FROM expenses WHERE date BETWEEN :startDate AND :endDate")
    suspend fun deleteByDateRange(startDate: Date, endDate: Date)

    @Query("DELETE FROM expenses WHERE categoryId = :categoryId AND date BETWEEN :startDate AND :endDate")
    suspend fun deleteByCategoryIdAndDateRange(categoryId: Long, startDate: Date, endDate: Date)

    @Query("DELETE FROM expenses")
    suspend fun deleteAll()

    @Query("DELETE FROM expenses WHERE categoryId = :categoryId")
    suspend fun deleteByCategoryId(categoryId: Long)

    @Query("SELECT * FROM expenses WHERE date BETWEEN :startDate AND :endDate")
    fun getExpensesByDateRange(startDate: Date, endDate: Date): Flow<List<Expense>>

    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpenses(): Flow<List<Expense>>

    @Query("SELECT c.name as categoryName, SUM(e.amount) as amount FROM expenses e JOIN categories c ON e.categoryId = c.id WHERE e.date BETWEEN :startDate AND :endDate GROUP BY c.name")
    fun getExpensesByCategory(startDate: Date, endDate: Date): Flow<List<ExpenseByCategory>>

    @Query("SELECT c.name as categoryName, SUM(e.amount) as amount FROM expenses e JOIN categories c ON e.categoryId = c.id GROUP BY c.name")
    fun getAllExpensesByCategory(): Flow<List<ExpenseByCategory>>
}
