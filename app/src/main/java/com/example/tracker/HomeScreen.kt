package com.example.tracker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tracker.data.Category
import com.example.tracker.data.CategoryType
import com.example.tracker.viewmodel.ExpenseViewModel
import com.example.tracker.viewmodel.HabitViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    expenseViewModel: ExpenseViewModel = viewModel(),
    habitViewModel: HabitViewModel = viewModel()
) {
    var showManageCategoriesDialog by remember { mutableStateOf<CategoryType?>(null) }

    if (showManageCategoriesDialog != null) {
        if (showManageCategoriesDialog == CategoryType.EXPENSE) {
            val categories by expenseViewModel.expenseCategories.collectAsState()
            ManageCategoriesDialog(
                categories = categories,
                onAddCategory = { expenseViewModel.addCategory(it) },
                onDeleteCategory = { expenseViewModel.deleteCategory(it) },
                onDismiss = { showManageCategoriesDialog = null }
            )
        } else {
            val categories by habitViewModel.habitCategories.collectAsState()
            ManageCategoriesDialog(
                categories = categories,
                onAddCategory = { habitViewModel.addCategory(it) },
                onDeleteCategory = { habitViewModel.deleteCategory(it) },
                onDismiss = { showManageCategoriesDialog = null }
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            HabitTracker(viewModel = habitViewModel) { showManageCategoriesDialog = CategoryType.HABIT }
            ExpenseTracker(viewModel = expenseViewModel) { showManageCategoriesDialog = CategoryType.EXPENSE }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseTracker(modifier: Modifier = Modifier, viewModel: ExpenseViewModel, onManageCategories: () -> Unit) {
    val categories by viewModel.expenseCategories.collectAsState()
    var amount by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var description by remember { mutableStateOf("") }
    var isCategoryExpanded by remember { mutableStateOf(false) }

    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Expense Tracker", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Amount") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            ExposedDropdownMenuBox(expanded = isCategoryExpanded, onExpandedChange = { isCategoryExpanded = !isCategoryExpanded }) {
                OutlinedTextField(
                    value = selectedCategory?.name ?: "",
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCategoryExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(expanded = isCategoryExpanded, onDismissRequest = { isCategoryExpanded = false }) {
                    categories.forEach { category ->
                        DropdownMenuItem(text = { Text(category.name) }, onClick = {
                            selectedCategory = category
                            isCategoryExpanded = false
                        })
                    }
                    DropdownMenuItem(text = { Text("+ Manage") }, onClick = onManageCategories)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description (Optional)") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val amountValue = amount.toDoubleOrNull()
                    if (amountValue != null && selectedCategory != null) {
                        viewModel.addExpense(amountValue, selectedCategory!!.id, description)
                        amount = ""
                        selectedCategory = null
                        description = ""
                    }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Add Expense")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitTracker(modifier: Modifier = Modifier, viewModel: HabitViewModel, onManageCategories: () -> Unit) {
    val categories by viewModel.habitCategories.collectAsState()
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var description by remember { mutableStateOf("") }
    var isCategoryExpanded by remember { mutableStateOf(false) }
    var isTracking by remember { mutableStateOf(false) }

    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Habit Tracker", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            ExposedDropdownMenuBox(expanded = isCategoryExpanded, onExpandedChange = { isCategoryExpanded = !isCategoryExpanded }) {
                OutlinedTextField(
                    value = selectedCategory?.name ?: "",
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCategoryExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(expanded = isCategoryExpanded, onDismissRequest = { isCategoryExpanded = false }) {
                    categories.forEach { category ->
                        DropdownMenuItem(text = { Text(category.name) }, onClick = {
                            selectedCategory = category
                            isCategoryExpanded = false
                        })
                    }
                    DropdownMenuItem(text = { Text("+ Manage") }, onClick = onManageCategories)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description (Optional)") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (isTracking) {
                        viewModel.stopTracking()
                        isTracking = false
                        selectedCategory = null
                        description = ""
                    } else {
                        if (selectedCategory != null) {
                            viewModel.startTracking(selectedCategory!!.id, description)
                            isTracking = true
                        }
                    }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(if (isTracking) "Stop Timer" else "Start Timer")
            }
        }
    }
}
