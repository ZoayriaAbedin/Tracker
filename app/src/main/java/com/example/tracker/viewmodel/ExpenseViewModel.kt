package com.example.tracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tracker.data.Category
import com.example.tracker.data.CategoryType
import com.example.tracker.data.Expense
import com.example.tracker.data.ExpenseByCategory
import com.example.tracker.data.TrackerDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class ExpenseViewModel(application: Application) : AndroidViewModel(application) {

    private val categoryDao = TrackerDatabase.getDatabase(application).categoryDao()
    private val expenseDao = TrackerDatabase.getDatabase(application).expenseDao()

    private val _timePeriod = MutableStateFlow("Last 30 Days")
    val timePeriod: StateFlow<String> = _timePeriod

    val expenseCategories = categoryDao.getCategoriesByType(CategoryType.EXPENSE)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val allExpenses = _timePeriod.flatMapLatest { period ->
        getExpensesByTimePeriod(period)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val expensesByCategory: StateFlow<List<ExpenseByCategory>> = _timePeriod.flatMapLatest { period ->
        getExpensesByCategory(period)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun setTimePeriod(period: String) {
        _timePeriod.value = period
    }

    private fun getExpensesByTimePeriod(period: String): StateFlow<List<Expense>> {
        val cal = Calendar.getInstance()
        val endDate = cal.time
        when (period) {
            "Last 7 Days" -> cal.add(Calendar.DAY_OF_YEAR, -7)
            "Last 30 Days" -> cal.add(Calendar.DAY_OF_YEAR, -30)
            "All Time" -> return expenseDao.getAllExpenses().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
        }
        return expenseDao.getExpensesByDateRange(cal.time, endDate).stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    }

    private fun getExpensesByCategory(period: String): StateFlow<List<ExpenseByCategory>> {
        val cal = Calendar.getInstance()
        val endDate = cal.time
        when (period) {
            "Last 7 Days" -> cal.add(Calendar.DAY_OF_YEAR, -7)
            "Last 30 Days" -> cal.add(Calendar.DAY_OF_YEAR, -30)
            "All Time" -> return expenseDao.getAllExpensesByCategory().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
        }
        return expenseDao.getExpensesByCategory(cal.time, endDate).stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    }

    fun addExpense(amount: Double, categoryId: Long, description: String?) {
        viewModelScope.launch {
            expenseDao.insert(Expense(amount = amount, categoryId = categoryId, description = description, date = Date()))
        }
    }

    fun addCategory(name: String) {
        viewModelScope.launch {
            categoryDao.insert(Category(name = name, type = CategoryType.EXPENSE))
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            categoryDao.delete(category)
        }
    }
}
