package com.example.tracker

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tracker.viewmodel.BudgetViewModel
import com.example.tracker.viewmodel.ExpenseViewModel

@Composable
fun BudgetHistoryScreen(budgetViewModel: BudgetViewModel, expenseViewModel: ExpenseViewModel) {
    val budgetsWithExpenses by budgetViewModel.budgetsWithExpenses.collectAsState()
    val expenseCategories by expenseViewModel.expenseCategories.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Budget History", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        LazyColumn {
            items(budgetsWithExpenses) { budgetWithExpenses ->
                BudgetListItem(
                    budgetWithExpenses = budgetWithExpenses, 
                    categoryName = expenseCategories.find { it.id == budgetWithExpenses.budget.categoryId }?.name ?: "",
                    onEdit = {},
                    onDelete = {}
                )
            }
        }
    }
}
