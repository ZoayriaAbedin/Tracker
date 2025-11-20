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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tracker.data.HabitGoal
import com.example.tracker.viewmodel.HabitViewModel

@Composable
fun HabitPlanningScreen(viewModel: HabitViewModel) {
    val habitGoals by viewModel.allHabitGoals.collectAsState()
    val habitCategories by viewModel.habitCategories.collectAsState()
    var showAddHabitGoalDialog by remember { mutableStateOf(false) }
    var showEditHabitGoalDialog by remember { mutableStateOf<HabitGoal?>(null) }
    var showDeleteHabitGoalDialog by remember { mutableStateOf<HabitGoal?>(null) }

    if (showAddHabitGoalDialog) {
        AddHabitGoalDialog(
            categories = habitCategories,
            onAddGoal = { viewModel.addHabitGoal(it) },
            onDismiss = { showAddHabitGoalDialog = false }
        )
    }

    if (showEditHabitGoalDialog != null) {
        // TODO: Create EditHabitGoalDialog
    }

    if (showDeleteHabitGoalDialog != null) {
        DeleteConfirmationDialog(
            onConfirm = {
                viewModel.deleteHabitGoal(showDeleteHabitGoalDialog!!)
                showDeleteHabitGoalDialog = null
            },
            onDismiss = { showDeleteHabitGoalDialog = null }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(text = "Habit Planning", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { showAddHabitGoalDialog = true }, modifier = Modifier.align(Alignment.End)) {
            Text("+ Add Habit Goal")
        }
        LazyColumn(
            modifier = Modifier.height(400.dp) // Avoid using fixed heights with LazyColumn, this is just for demonstration
        ) {
            items(habitGoals) { goal ->
                HabitGoalListItem(
                    habitGoal = goal,
                    categoryName = habitCategories.find { it.id == goal.categoryId }?.name ?: "",
                    onEdit = { showEditHabitGoalDialog = goal },
                    onDelete = { showDeleteHabitGoalDialog = goal }
                )
            }
        }
    }
}

@Composable
fun HabitGoalListItem(
    habitGoal: HabitGoal,
    categoryName: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = categoryName, modifier = Modifier.weight(1f), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Goal")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Goal")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(progress = 0.5f, modifier = Modifier.fillMaxWidth())
            Text("Target: ${habitGoal.targetTime} minutes, ${habitGoal.timePeriod}")
        }
    }
}
