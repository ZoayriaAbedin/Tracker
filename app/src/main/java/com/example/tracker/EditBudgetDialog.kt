package com.example.tracker

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tracker.data.Budget

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBudgetDialog(
    budget: Budget,
    onConfirm: (amount: Double, timePeriod: String) -> Unit,
    onDismiss: () -> Unit
) {
    var amount by remember { mutableStateOf(budget.amount.toString()) }
    var selectedTimePeriod by remember { mutableStateOf("Monthly") } // This should be based on the budget's current time period

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Budget") },
        text = {
            Column {
                OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Budget Amount") })
                Spacer(modifier = Modifier.height(8.dp))
                // Time period dropdown will go here
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(amount.toDouble(), selectedTimePeriod)
                onDismiss()
            }) {
                Text("Save")
            }
        }
    )
}
