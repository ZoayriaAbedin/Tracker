package com.example.tracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tracker.data.Budget
import com.example.tracker.data.TrackerDatabase
import com.example.tracker.data.BudgetWithExpenses
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class BudgetViewModel(application: Application) : AndroidViewModel(application) {

    private val budgetDao = TrackerDatabase.getDatabase(application).budgetDao()

    val budgetsWithExpenses = budgetDao.getBudgetsWithExpenses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val totalBudget = budgetsWithExpenses.map { budgets ->
        budgets.sumOf { it.budget.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0.0)

    val totalSpent = budgetsWithExpenses.map { budgets ->
        budgets.sumOf { it.spent }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0.0)

    val totalRemaining = totalBudget.combine(totalSpent) { budget, spent ->
        budget - spent
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0.0)

    fun addBudget(categoryId: Long, amount: Double, startDate: Date, endDate: Date) {
        viewModelScope.launch {
            budgetDao.insert(Budget(categoryId = categoryId, amount = amount, startDate = startDate, endDate = endDate))
        }
    }

    fun updateBudget(budget: Budget) {
        viewModelScope.launch {
            budgetDao.update(budget)
        }
    }

    fun deleteBudget(budget: Budget) {
        viewModelScope.launch {
            budgetDao.delete(budget)
        }
    }
}
