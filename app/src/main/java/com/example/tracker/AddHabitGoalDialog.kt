package com.example.tracker

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tracker.data.Category
import com.example.tracker.data.HabitGoal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHabitGoalDialog(
    categories: List<Category>,
    onAddGoal: (HabitGoal) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var isCategoryExpanded by remember { mutableStateOf(false) }
    var targetTime by remember { mutableStateOf("") }
    val timePeriods = listOf("Daily", "Weekly", "Monthly")
    var selectedTimePeriod by remember { mutableStateOf(timePeriods[0]) }
    var isTimePeriodExpanded by remember { mutableStateOf(false) }
    var trackStreak by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Habit Goal") },
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
                OutlinedTextField(value = targetTime, onValueChange = { targetTime = it }, label = { Text("Target Time (minutes)") })
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
                                isTimePeriodExpanded = false
                            })
                        }
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = trackStreak, onCheckedChange = { trackStreak = it })
                    Text("Track Streak")
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (selectedCategory != null && targetTime.isNotEmpty()) {
                    val goal = HabitGoal(
                        categoryId = selectedCategory!!.id,
                        targetTime = targetTime.toLong(),
                        timePeriod = selectedTimePeriod,
                        trackStreak = trackStreak
                    )
                    onAddGoal(goal)
                    onDismiss()
                }
            }) {
                Text("Create Goal")
            }
        }
    )
}
