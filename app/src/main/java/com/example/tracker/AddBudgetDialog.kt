package com.example.tracker

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tracker.data.Category
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBudgetDialog(
    categories: List<Category>,
    onAddBudget: (categoryId: Long, amount: Double, startDate: Date, endDate: Date) -> Unit,
    onDismiss: () -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var isCategoryExpanded by remember { mutableStateOf(false) }
    val timePeriods = listOf("Daily", "Weekly", "Monthly", "Yearly", "Custom")
    var selectedTimePeriod by remember { mutableStateOf(timePeriods[0]) }
    var isTimePeriodExpanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    val dateRangePickerState = rememberDateRangePickerState()
    var startDate by remember { mutableStateOf<Date?>(null) }
    var endDate by remember { mutableStateOf<Date?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Budget") },
        text = {
            Column {
                ExposedDropdownMenuBox(expanded = isCategoryExpanded, onExpandedChange = { isCategoryExpanded = !isCategoryExpanded }) {
                    OutlinedTextField(
                        value = selectedCategory?.name ?: "",
                        onValueChange = {},
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
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Budget Amount") })
                Spacer(modifier = Modifier.height(8.dp))
                ExposedDropdownMenuBox(expanded = isTimePeriodExpanded, onExpandedChange = { isTimePeriodExpanded = !isTimePeriodExpanded }) {
                    OutlinedTextField(
                        value = selectedTimePeriod,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Time Period") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isTimePeriodExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(expanded = isTimePeriodExpanded, onDismissRequest = { isTimePeriodExpanded = false }) {
                        timePeriods.forEach { timePeriod ->
                            DropdownMenuItem(text = { Text(timePeriod) }, onClick = {
                                selectedTimePeriod = timePeriod
                                if (timePeriod == "Custom") {
                                    showDatePicker = true
                                }
                                isTimePeriodExpanded = false
                            })
                        }
                    }
                }
                if (selectedTimePeriod == "Custom") {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedButton(onClick = { showDatePicker = true }) {
                            Text(text = if(startDate != null && endDate != null) "${SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(startDate!!)} - ${SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(endDate!!)}" else "Select Date Range")
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val amountValue = amount.toDoubleOrNull()
                val calendar = Calendar.getInstance()
                val finalStartDate: Date
                val finalEndDate: Date

                if (selectedTimePeriod == "Custom") {
                    finalStartDate = startDate!!
                    finalEndDate = endDate!!
                } else {
                    finalStartDate = calendar.time
                    when (selectedTimePeriod) {
                        "Daily" -> calendar.add(Calendar.DAY_OF_YEAR, 1)
                        "Weekly" -> calendar.add(Calendar.WEEK_OF_YEAR, 1)
                        "Monthly" -> calendar.add(Calendar.MONTH, 1)
                        "Yearly" -> calendar.add(Calendar.YEAR, 1)
                    }
                    finalEndDate = calendar.time
                }

                if (selectedCategory != null && amountValue != null) {
                    onAddBudget(selectedCategory!!.id, amountValue, finalStartDate, finalEndDate)
                    onDismiss()
                }
            }) {
                Text("Create Budget")
            }
        }
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                Button(onClick = { 
                    showDatePicker = false 
                    startDate = dateRangePickerState.selectedStartDateMillis?.let { Date(it) }
                    endDate = dateRangePickerState.selectedEndDateMillis?.let { Date(it) }
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DateRangePicker(state = dateRangePickerState)
        }
    }
}
