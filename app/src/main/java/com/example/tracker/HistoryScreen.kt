package com.example.tracker

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tracker.viewmodel.ExpenseViewModel
import com.example.tracker.viewmodel.HabitViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun HistoryScreen(expenseViewModel: ExpenseViewModel, habitViewModel: HabitViewModel) {
    val expenses by expenseViewModel.allExpenses.collectAsState()
    val habits by habitViewModel.allHabits.collectAsState()
    val expenseCategories by expenseViewModel.expenseCategories.collectAsState()
    val habitCategories by habitViewModel.habitCategories.collectAsState()

    val dateFormatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Expense History", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(expenses) { expense ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Row(modifier = Modifier.padding(16.dp)) {
                        val category = expenseCategories.find { it.id == expense.categoryId }?.name ?: ""
                        Text(text = "$category: $${expense.amount} on ${dateFormatter.format(expense.date)}")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Habit History", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(habits) { habit ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Row(modifier = Modifier.padding(16.dp)) {
                        val category = habitCategories.find { it.id == habit.categoryId }?.name ?: ""
                        val duration = if (habit.endTime != null) {
                            "${habit.duration} seconds"
                        } else {
                            "In progress"
                        }
                        Text(text = "$category: $duration on ${dateFormatter.format(habit.startTime)}")
                    }
                }
            }
        }
    }
}
