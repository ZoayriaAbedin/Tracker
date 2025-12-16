package com.example.tracker

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

sealed class NavigationItem(val route: String, val icon: Int, val title: String) {
    object Home : NavigationItem(Screen.Home.route, R.drawable.ic_home, "Home")
    object Budget : NavigationItem(Screen.Budget.route, R.drawable.ic_budget, "Budget")
    object Habits : NavigationItem(Screen.Habits.route, R.drawable.ic_habits, "Habits")
    object ExpenseCharts : NavigationItem(Screen.ExpenseCharts.route, R.drawable.ic_charts, "Expense Charts")
    object HabitCharts : NavigationItem(Screen.HabitCharts.route, R.drawable.ic_charts, "Habit Charts")
    object History : NavigationItem(Screen.History.route, R.drawable.ic_history, "History")
    object Settings : NavigationItem(Screen.Settings.route, R.drawable.ic_settings, "Settings")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerContent(
    items: List<NavigationItem>,
    onItemClick: (NavigationItem) -> Unit
) {
    Spacer(Modifier.height(12.dp))
    items.forEach { item ->
        NavigationDrawerItem(
            icon = { Icon(painter = painterResource(id = item.icon), contentDescription = item.title) },
            label = { Text(item.title) },
            selected = false,
            onClick = { onItemClick(item) },
            modifier = Modifier.padding(horizontal = 12.dp)
        )
    }
}
