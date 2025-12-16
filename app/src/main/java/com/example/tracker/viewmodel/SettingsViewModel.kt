package com.example.tracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tracker.data.Category
import com.example.tracker.data.CategoryType
import com.example.tracker.data.TrackerDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme

    private val habitDao = TrackerDatabase.getDatabase(application).habitDao()
    private val expenseDao = TrackerDatabase.getDatabase(application).expenseDao()
    private val categoryDao = TrackerDatabase.getDatabase(application).categoryDao()

    val habitCategories: StateFlow<List<Category>> = categoryDao.getCategoriesByType(CategoryType.HABIT)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val expenseCategories: StateFlow<List<Category>> = categoryDao.getCategoriesByType(CategoryType.EXPENSE)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    fun deleteHistory(historyType: String, timeSpan: String, categoryId: Long?) {
        viewModelScope.launch {
            val cal = Calendar.getInstance()
            val endDate = cal.time
            val startDate = when (timeSpan) {
                "Last 7 Days" -> {
                    cal.add(Calendar.DAY_OF_YEAR, -7)
                    cal.time
                }
                "Last 30 Days" -> {
                    cal.add(Calendar.DAY_OF_YEAR, -30)
                    cal.time
                }
                else -> Date(0) // All Time
            }

            if (historyType == "Habits") {
                if (categoryId != null) {
                    if (timeSpan == "All Time") {
                        habitDao.deleteByCategoryId(categoryId)
                    } else {
                        habitDao.deleteByCategoryIdAndDateRange(categoryId, startDate, endDate)
                    }
                } else {
                    if (timeSpan == "All Time") {
                        habitDao.deleteAll()
                    } else {
                        habitDao.deleteByDateRange(startDate, endDate)
                    }
                }
            } else { // Expenses
                if (categoryId != null) {
                    if (timeSpan == "All Time") {
                        expenseDao.deleteByCategoryId(categoryId)
                    } else {
                        expenseDao.deleteByCategoryIdAndDateRange(categoryId, startDate, endDate)
                    }
                } else {
                    if (timeSpan == "All Time") {
                        expenseDao.deleteAll()
                    } else {
                        expenseDao.deleteByDateRange(startDate, endDate)
                    }
                }
            }
        }
    }
}
