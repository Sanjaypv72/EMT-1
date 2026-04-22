package com.emt.app.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// --- CRITICAL IMPORTS ---
import com.emt.app.ui.theme.InterFamily
import com.emt.app.ui.theme.PoppinsFamily
import com.emt.app.viewmodel.TaskViewModel
import com.emt.app.viewmodel.EmployeeViewModel
import com.emt.app.model.Task // Ensure you have a Task model
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTasksScreen(
    taskVM: TaskViewModel,
    employeeVM: EmployeeViewModel,
    onSettingsClick: () -> Unit
) {
    // Collect real state
    // Collect real state
    val employees by employeeVM.employees.collectAsState(initial = emptyList())
    val allTasks by taskVM.tasks.collectAsState(initial = emptyList()) // This is line 40

    val loggedInEmployee = employees.firstOrNull()

    // Line 44: Changed allPerformance to allTasks
    val tasks = allTasks.filter { it.employeeName == loggedInEmployee?.name }

    val total = tasks.size
    val done = tasks.count { it.status == "Completed" || it.status == "Reviewed" }
    val pending = tasks.count { it.status == "Pending" || it.status == "In Progress" }
    val progress = if (total > 0) done.toFloat() / total else 0f

    Scaffold(
        containerColor = Color(0xFF0D1117),
        topBar = {
            Box(
                modifier = Modifier.fillMaxWidth()
                    .background(Brush.horizontalGradient(listOf(Color(0xFF5C35CC), Color(0xFF7C3AED))))
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("My Tasks", fontSize = 24.sp, fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(loggedInEmployee?.name ?: "Employee", fontSize = 12.sp, fontFamily = InterFamily, color = Color.White.copy(0.65f))
                    }
                    IconButton(onClick = onSettingsClick) { Icon(Icons.Default.Settings, null, tint = Color.White) }
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {

            // Progress summary
            Box(
                modifier = Modifier.fillMaxWidth().background(Color(0xFF161B22))
                    .border(BorderStroke(1.dp, Color.White.copy(0.05f)), RoundedCornerShape(0.dp))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        TaskStatBox("Total", total.toString(), Color(0xFF6366F1), Modifier.weight(1f))
                        TaskStatBox("Done",  done.toString(),  Color(0xFF10B981), Modifier.weight(1f))
                        TaskStatBox("Open",  pending.toString(), Color(0xFFF59E0B), Modifier.weight(1f))
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Completion", fontSize = 12.sp, fontFamily = InterFamily, color = Color(0xFF6B7280))
                            Text("${(progress * 100).toInt()}%", fontSize = 12.sp, fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold, color = Color(0xFF10B981))
                        }
                        Box(modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(50)).background(Color(0xFF21262D))) {
                            Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(progress).clip(RoundedCornerShape(50))
                                .background(Brush.horizontalGradient(listOf(Color(0xFF6366F1), Color(0xFF10B981)))))
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            if (tasks.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(60.dp), tint = Color(0xFF10B981))
                        Text("All clear!", fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.White)
                        Text("No pending tasks", fontFamily = InterFamily, fontSize = 14.sp, color = Color(0xFF6B7280))
                    }
                }
            } else {
                LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(tasks, key = { it.id }) { task ->
                        DarkTaskCard(task = task, onStatusUpdate = { newStatus ->
                            // Optional: Add ViewModel call here to update status in Database
                            // taskVM.updateTaskStatus(task.id, newStatus)
                        })
                    }
                    item { Spacer(Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
fun DarkTaskCard(task: Task, onStatusUpdate: (String) -> Unit) {
    val priorityColor = when (task.priority) { "High" -> Color(0xFFF43F5E); "Medium" -> Color(0xFFF59E0B); else -> Color(0xFF10B981) }
    val statusColor   = when (task.status)   { "Completed", "Reviewed" -> Color(0xFF10B981); "In Progress" -> Color(0xFFF59E0B); else -> Color(0xFF6B7280) }

    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF161B22)).border(1.dp, Color.White.copy(0.06f), RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.width(4.dp).height(40.dp).clip(RoundedCornerShape(50)).background(priorityColor))
            Column(modifier = Modifier.weight(1f)) {
                Text(task.title, fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = Color.White)
                Text(task.description, fontFamily = InterFamily, fontSize = 12.sp, color = Color(0xFF6B7280), maxLines = 1)
            }
            Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(priorityColor.copy(0.15f)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                Text(task.priority, fontSize = 11.sp, fontFamily = InterFamily, fontWeight = FontWeight.SemiBold, color = priorityColor)
            }
        }

        HorizontalDivider(color = Color.White.copy(0.05f))

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Schedule, null, modifier = Modifier.size(13.dp), tint = Color(0xFF4B5563))
                Text(task.deadline, fontSize = 12.sp, fontFamily = InterFamily, color = Color(0xFF4B5563))
            }
            // Status and Button logic
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(statusColor.copy(0.12f)).padding(horizontal = 10.dp, vertical = 4.dp)) {
                    Text(task.status, fontSize = 11.sp, fontFamily = InterFamily, color = statusColor)
                }
            }
        }
    }
}
@Composable
fun TaskStatBox(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    // ... code here ...
}

// ... Keep TaskStatBox and TaskSummaryCard as they were