package com.example.tracker

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.tracker.viewmodel.BudgetViewModel
import com.example.tracker.viewmodel.ExpenseViewModel
import com.example.tracker.viewmodel.HabitViewModel
import com.example.tracker.viewmodel.SettingsViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    settingsViewModel: SettingsViewModel
) {
    val expenseViewModel: ExpenseViewModel = viewModel()
    val habitViewModel: HabitViewModel = viewModel()
    val budgetViewModel: BudgetViewModel = viewModel()

    NavHost(navController = navController, startDestination = Screen.Home.route, modifier = modifier) {
        composable(Screen.Home.route) {
            HomeScreen(navController = navController, expenseViewModel = expenseViewModel, habitViewModel = habitViewModel)
        }
        composable(Screen.Budget.route) {
            BudgetScreen(budgetViewModel = budgetViewModel, expenseViewModel = expenseViewModel, navController = navController)
        }
        composable(Screen.History.route) {
            HistoryScreen(expenseViewModel = expenseViewModel, habitViewModel = habitViewModel)
        }
        composable("budget_history") {
            BudgetHistoryScreen(budgetViewModel = budgetViewModel, expenseViewModel = expenseViewModel)
        }
        composable(Screen.Habits.route) {
            HabitPlanningScreen(viewModel = habitViewModel)
        }
        composable(Screen.ExpenseCharts.route) {
            ExpenseChartsScreen(viewModel = expenseViewModel)
        }
        composable(Screen.HabitCharts.route) {
            HabitChartsScreen(viewModel = habitViewModel)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(viewModel = settingsViewModel)
        }
    }
}
