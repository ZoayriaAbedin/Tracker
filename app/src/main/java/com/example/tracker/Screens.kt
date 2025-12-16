package com.example.tracker

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Budget : Screen("budget")
    object Habits : Screen("habits")
    object ExpenseCharts : Screen("expense_charts")
    object HabitCharts : Screen("habit_charts")
    object History : Screen("history")
    object Settings : Screen("settings")
}
