package com.example.tracker

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tracker.data.Category
import com.example.tracker.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteHistoryScreen(viewModel: SettingsViewModel) {
    val habitCategories by viewModel.habitCategories.collectAsState()
    val expenseCategories by viewModel.expenseCategories.collectAsState()
    var historyType by remember { mutableStateOf("Habits") }
    var isHistoryTypeExpanded by remember { mutableStateOf(false) }
    var timeSpan by remember { mutableStateOf("All Time") }
    var isTimeSpanExpanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var isCategoryExpanded by remember { mutableStateOf(false) }

    val historyTypes = listOf("Habits", "Expenses")
    val timeSpans = listOf("Last 7 Days", "Last 30 Days", "All Time")

    Column(modifier = Modifier.padding(16.dp)) {
        ExposedDropdownMenuBox(expanded = isHistoryTypeExpanded, onExpandedChange = { isHistoryTypeExpanded = !isHistoryTypeExpanded }) {
            OutlinedTextField(
                value = historyType,
                onValueChange = {},
                readOnly = true,
                label = { Text("History Type") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isHistoryTypeExpanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(expanded = isHistoryTypeExpanded, onDismissRequest = { isHistoryTypeExpanded = false }) {
                historyTypes.forEach { type ->
                    DropdownMenuItem(text = { Text(type) }, onClick = {
                        historyType = type
                        isHistoryTypeExpanded = false
                        selectedCategory = null // Reset category on type change
                    })
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(expanded = isTimeSpanExpanded, onExpandedChange = { isTimeSpanExpanded = !isTimeSpanExpanded }) {
            OutlinedTextField(
                value = timeSpan,
                onValueChange = {},
                readOnly = true,
                label = { Text("Time Span") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isTimeSpanExpanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(expanded = isTimeSpanExpanded, onDismissRequest = { isTimeSpanExpanded = false }) {
                timeSpans.forEach { span ->
                    DropdownMenuItem(text = { Text(span) }, onClick = {
                        timeSpan = span
                        isTimeSpanExpanded = false
                    })
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val categories = if (historyType == "Habits") habitCategories else expenseCategories

        ExposedDropdownMenuBox(expanded = isCategoryExpanded, onExpandedChange = { isCategoryExpanded = !isCategoryExpanded }) {
            OutlinedTextField(
                value = selectedCategory?.name ?: "All Categories",
                onValueChange = {},
                readOnly = true,
                label = { Text("Category") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCategoryExpanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(expanded = isCategoryExpanded, onDismissRequest = { isCategoryExpanded = false }) {
                DropdownMenuItem(text = { Text("All Categories") }, onClick = {
                    selectedCategory = null
                    isCategoryExpanded = false
                })
                categories.forEach { category ->
                    DropdownMenuItem(text = { Text(category.name) }, onClick = {
                        selectedCategory = category
                        isCategoryExpanded = false
                    })
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { viewModel.deleteHistory(historyType, timeSpan, selectedCategory?.id) }) {
            Text("Delete History")
        }
    }
}
