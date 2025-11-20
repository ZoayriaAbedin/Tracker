package com.example.tracker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tracker.data.Budget
import com.example.tracker.data.BudgetWithExpenses
import com.example.tracker.viewmodel.BudgetViewModel
import com.example.tracker.viewmodel.ExpenseViewModel

@Composable
fun BudgetScreen(budgetViewModel: BudgetViewModel, expenseViewModel: ExpenseViewModel, navController: NavController) {
    val totalBudget by budgetViewModel.totalBudget.collectAsState()
    val totalSpent by budgetViewModel.totalSpent.collectAsState()
    val totalRemaining by budgetViewModel.totalRemaining.collectAsState()
    val budgetsWithExpenses by budgetViewModel.budgetsWithExpenses.collectAsState()
    val expenseCategories by expenseViewModel.expenseCategories.collectAsState()

    var showAddBudgetDialog by remember { mutableStateOf(false) }
    var showEditBudgetDialog by remember { mutableStateOf<Budget?>(null) }
    var showDeleteBudgetDialog by remember { mutableStateOf<Budget?>(null) }

    if (showAddBudgetDialog) {
        AddBudgetDialog(
            categories = expenseCategories,
            onAddBudget = { categoryId, amount, startDate, endDate ->
                budgetViewModel.addBudget(categoryId, amount, startDate, endDate)
            },
            onDismiss = { showAddBudgetDialog = false }
        )
    }

    if (showEditBudgetDialog != null) {
        EditBudgetDialog(
            budget = showEditBudgetDialog!!,
            onConfirm = { amount, timePeriod ->
                budgetViewModel.updateBudget(showEditBudgetDialog!!.copy(amount = amount))
            },
            onDismiss = { showEditBudgetDialog = null }
        )
    }

    if (showDeleteBudgetDialog != null) {
        DeleteConfirmationDialog(
            onConfirm = {
                budgetViewModel.deleteBudget(showDeleteBudgetDialog!!)
                showDeleteBudgetDialog = null
            },
            onDismiss = { showDeleteBudgetDialog = null }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Budget Planning", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Button(onClick = { navController.navigate("budget_history") }) {
                Text(text = "History")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            BudgetSummaryCard(title = "Total Budget", amount = totalBudget)
            BudgetSummaryCard(title = "Total Spent", amount = totalSpent)
            BudgetSummaryCard(title = "Remaining", amount = totalRemaining)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { showAddBudgetDialog = true }, modifier = Modifier.align(Alignment.End)) {
            Text("+ Add Budget")
        }
        LazyColumn(
            modifier = Modifier.height(400.dp) // Avoid using fixed heights with LazyColumn, this is just for demonstration
        ) {
            items(budgetsWithExpenses) { budgetWithExpenses ->
                BudgetListItem(
                    budgetWithExpenses = budgetWithExpenses, 
                    categoryName = expenseCategories.find { it.id == budgetWithExpenses.budget.categoryId }?.name ?: "",
                    onEdit = { showEditBudgetDialog = budgetWithExpenses.budget },
                    onDelete = { showDeleteBudgetDialog = budgetWithExpenses.budget }
                )
            }
        }
    }
}

@Composable
fun BudgetSummaryCard(title: String, amount: Double) {
    Card(modifier = Modifier.padding(8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(text = "$${String.format("%.2f", amount)}", fontSize = 24.sp)
        }
    }
}

@Composable
fun BudgetListItem(
    budgetWithExpenses: BudgetWithExpenses, 
    categoryName: String, 
    onEdit: () -> Unit, 
    onDelete: () -> Unit
) {
    val spent = budgetWithExpenses.spent
    val budget = budgetWithExpenses.budget.amount
    val progress = if (budget > 0) (spent / budget).toFloat() else 0f
    val remaining = budget - spent
    val isExceeded = spent > budget

    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = categoryName, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Budget")
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Budget")
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier.fillMaxWidth(),
                color = if (isExceeded) Color.Red else Color.Blue
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Spent: $${String.format("%.2f", spent)}")
                Text(text = "Remaining: $${String.format("%.2f", remaining)}")
            }
            if (isExceeded) {
                Text(
                    text = "Budget Exceeded by $${String.format("%.2f", spent - budget)}",
                    color = Color.Red,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
