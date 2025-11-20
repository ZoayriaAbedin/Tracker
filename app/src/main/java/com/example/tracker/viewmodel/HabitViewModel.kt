package com.example.tracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tracker.data.Category
import com.example.tracker.data.CategoryType
import com.example.tracker.data.Habit
import com.example.tracker.data.HabitByCategory
import com.example.tracker.data.HabitGoal
import com.example.tracker.data.TrackerDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class HabitViewModel(application: Application) : AndroidViewModel(application) {

    private val categoryDao = TrackerDatabase.getDatabase(application).categoryDao()
    private val habitDao = TrackerDatabase.getDatabase(application).habitDao()
    private val habitGoalDao = TrackerDatabase.getDatabase(application).habitGoalDao()

    private val _timePeriod = MutableStateFlow("Last 30 Days")
    val timePeriod: StateFlow<String> = _timePeriod

    val habitCategories: StateFlow<List<Category>> = categoryDao.getCategoriesByType(CategoryType.HABIT)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val allHabits: StateFlow<List<Habit>> = _timePeriod.flatMapLatest { period ->
        getHabitsByTimePeriod(period)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val allHabitGoals: StateFlow<List<HabitGoal>> = habitGoalDao.getAllHabitGoals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val habitsByCategory: StateFlow<List<HabitByCategory>> = _timePeriod.flatMapLatest { period ->
        getHabitsByCategory(period)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private var currentHabit: Habit? = null

    fun setTimePeriod(period: String) {
        _timePeriod.value = period
    }

    private fun getHabitsByTimePeriod(period: String): StateFlow<List<Habit>> {
        val cal = Calendar.getInstance()
        val endDate = cal.time
        when (period) {
            "Last 7 Days" -> cal.add(Calendar.DAY_OF_YEAR, -7)
            "Last 30 Days" -> cal.add(Calendar.DAY_OF_YEAR, -30)
            "All Time" -> return habitDao.getAllHabits().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
        }
        return habitDao.getHabitsByDateRange(cal.time, endDate).stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    }

    private fun getHabitsByCategory(period: String): StateFlow<List<HabitByCategory>> {
        val cal = Calendar.getInstance()
        val endDate = cal.time
        when (period) {
            "Last 7 Days" -> cal.add(Calendar.DAY_OF_YEAR, -7)
            "Last 30 Days" -> cal.add(Calendar.DAY_OF_YEAR, -30)
            "All Time" -> return habitDao.getAllHabitsByCategory().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
        }
        return habitDao.getHabitsByCategory(cal.time, endDate).stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    }

    fun startTracking(categoryId: Long, description: String?) {
        viewModelScope.launch {
            val habit = Habit(categoryId = categoryId, description = description, startTime = Date())
            val habitId = habitDao.insert(habit)
            currentHabit = habit.copy(id = habitId)
        }
    }

    fun stopTracking() {
        viewModelScope.launch {
            currentHabit?.let {
                habitDao.update(it.copy(endTime = Date()))
                currentHabit = null
            }
        }
    }

    fun addCategory(name: String) {
        viewModelScope.launch {
            categoryDao.insert(Category(name = name, type = CategoryType.HABIT))
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            categoryDao.delete(category)
        }
    }

    fun addHabitGoal(habitGoal: HabitGoal) {
        viewModelScope.launch {
            habitGoalDao.insert(habitGoal)
        }
    }

    fun updateHabitGoal(habitGoal: HabitGoal) {
        viewModelScope.launch {
            habitGoalDao.update(habitGoal)
        }
    }

    fun deleteHabitGoal(habitGoal: HabitGoal) {
        viewModelScope.launch {
            habitGoalDao.delete(habitGoal)
        }
    }
}
