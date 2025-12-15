package com.example.tracker

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.tracker.data.Habit
import com.example.tracker.data.HabitByCategory
import com.example.tracker.viewmodel.HabitViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitChartsScreen(viewModel: HabitViewModel) {
    val habitsByCategory by viewModel.habitsByCategory.collectAsState(initial = emptyList())
    val allHabits by viewModel.allHabits.collectAsState(initial = emptyList())
    val timePeriod by viewModel.timePeriod.collectAsState()
    val timePeriods = listOf("Last 7 Days", "Last 30 Days", "All Time")
    var isExpanded by remember { mutableStateOf(false) }
    val categories by viewModel.habitCategories.collectAsState()
    var selectedCategory by remember { mutableStateOf<com.example.tracker.data.Category?>(null) }
    var isCategoryExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        ExposedDropdownMenuBox(expanded = isExpanded, onExpandedChange = { isExpanded = !isExpanded }) {
            OutlinedTextField(
                value = timePeriod,
                onValueChange = {},
                readOnly = true,
                label = { Text("Time Period") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(expanded = isExpanded, onDismissRequest = { isExpanded = false }) {
                timePeriods.forEach { period ->
                    DropdownMenuItem(text = { Text(period) }, onClick = {
                        viewModel.setTimePeriod(period)
                        isExpanded = false
                    })
                }
            }
        }
        Text(text = "Habit Analytics", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        if (habitsByCategory.isNotEmpty()) {
            PieChart(habitsByCategory = habitsByCategory, modifier = Modifier.height(300.dp).fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
            BarChart(habitsByCategory = habitsByCategory, modifier = Modifier.height(300.dp).fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Daily Activity Trend", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            LineChart(habits = allHabits, modifier = Modifier.height(300.dp).fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
            ExposedDropdownMenuBox(expanded = isCategoryExpanded, onExpandedChange = { isCategoryExpanded = !isCategoryExpanded }) {
                OutlinedTextField(
                    value = selectedCategory?.name ?: "Select a category",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category Trend") },
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
            if (selectedCategory != null) {
                LineChart(habits = allHabits.filter { it.categoryId == selectedCategory!!.id }, modifier = Modifier.height(300.dp).fillMaxWidth())
            }
        }
    }
}

class SecondsValueFormatter : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        return "${value}s"
    }
}

@Composable
fun PieChart(habitsByCategory: List<HabitByCategory>, modifier: Modifier = Modifier) {
    val textColor = if (isSystemInDarkTheme()) android.graphics.Color.WHITE else android.graphics.Color.BLACK
    AndroidView(factory = { context ->
        PieChart(context).apply {
            val entries = habitsByCategory.map { PieEntry(it.duration.toFloat(), it.categoryName) }
            val dataSet = PieDataSet(entries, "Habits by Category")
            dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
            dataSet.valueTextColor = textColor
            dataSet.valueFormatter = SecondsValueFormatter()
            data = PieData(dataSet)
            legend.textColor = textColor
            setUsePercentValues(true)
            description.isEnabled = false
            invalidate()
        }
    }, modifier = modifier)
}

@Composable
fun BarChart(habitsByCategory: List<HabitByCategory>, modifier: Modifier = Modifier) {
    val textColor = if (isSystemInDarkTheme()) android.graphics.Color.WHITE else android.graphics.Color.BLACK
    AndroidView(factory = { context ->
        BarChart(context).apply {
            val entries = habitsByCategory.mapIndexed { index, it -> BarEntry(index.toFloat(), it.duration.toFloat()) }
            val dataSet = BarDataSet(entries, "Habits by Category")
            dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
            dataSet.valueTextColor = textColor
            dataSet.valueFormatter = SecondsValueFormatter()
            data = BarData(dataSet)
            xAxis.textColor = textColor
            xAxis.valueFormatter = IndexAxisValueFormatter(habitsByCategory.map { it.categoryName })
            axisLeft.textColor = textColor
            axisLeft.axisMinimum = 0f
            axisLeft.valueFormatter = SecondsValueFormatter()
            axisRight.textColor = textColor
            axisRight.axisMinimum = 0f
            axisRight.valueFormatter = SecondsValueFormatter()
            legend.textColor = textColor
            description.isEnabled = false
            isDragEnabled = true
            setScaleEnabled(true)
            setPinchZoom(true)
            invalidate()
        }
    }, modifier = modifier)
}

@Composable
fun LineChart(habits: List<Habit>, modifier: Modifier = Modifier) {
    val textColor = if (isSystemInDarkTheme()) android.graphics.Color.WHITE else android.graphics.Color.BLACK
    AndroidView(factory = { context ->
        LineChart(context).apply {
            val entries = habits.map { Entry(it.startTime.time.toFloat(), (it.endTime?.time?.minus(it.startTime.time))?.toFloat() ?: 0f) }
            val dataSet = LineDataSet(entries, "Daily Habits")
            dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
            dataSet.valueTextColor = textColor
            dataSet.valueFormatter = SecondsValueFormatter()
            data = LineData(dataSet)
            xAxis.textColor = textColor
            axisLeft.textColor = textColor
            axisLeft.axisMinimum = 0f
            axisLeft.valueFormatter = SecondsValueFormatter()
            axisRight.textColor = textColor
            axisRight.axisMinimum = 0f
            axisRight.valueFormatter = SecondsValueFormatter()
            legend.textColor = textColor
            description.isEnabled = false
            isDragEnabled = true
            setScaleEnabled(true)
            setPinchZoom(true)
            invalidate()
        }
    }, modifier = modifier)
}
