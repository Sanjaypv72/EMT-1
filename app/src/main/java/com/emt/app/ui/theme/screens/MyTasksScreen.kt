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
import com.emt.app.ui.components.*
import com.emt.app.ui.theme.InterFamily
import com.emt.app.ui.theme.PoppinsFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTasksScreen(onSettingsClick: () -> Unit) {
    val loggedInEmployee = dummyEmployees.first()
    var tasks by remember { mutableStateOf(dummyTasks.filter { it.assignedTo == loggedInEmployee.name }) }

    val total   = tasks.size
    val done    = tasks.count { it.status == "Completed" || it.status == "Reviewed" }
    val pending = tasks.count { it.status == "Pending" }
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
                        Text(loggedInEmployee.name, fontSize = 12.sp, fontFamily = InterFamily, color = Color.White.copy(0.65f))
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
                            tasks = tasks.map { if (it.id == task.id) it.copy(status = newStatus) else it }
                        })
                    }
                    item { Spacer(Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
fun TaskStatBox(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.clip(RoundedCornerShape(12.dp))
            .background(color.copy(0.1f)).border(1.dp, color.copy(0.2f), RoundedCornerShape(12.dp))
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = color)
            Text(label, fontFamily = InterFamily, fontSize = 11.sp, color = Color(0xFF6B7280))
        }
    }
}

@Composable
fun DarkTaskCard(task: TaskUI, onStatusUpdate: (String) -> Unit) {
    val priorityColor = when (task.priority) { "High" -> Color(0xFFF43F5E); "Medium" -> Color(0xFFF59E0B); else -> Color(0xFF10B981) }
    val statusColor   = when (task.status)   { "Completed", "Reviewed" -> Color(0xFF10B981); "In Progress" -> Color(0xFFF59E0B); else -> Color(0xFF6B7280) }

    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF161B22)).border(1.dp, Color.White.copy(0.06f), RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Left accent bar + title row
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
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(statusColor.copy(0.12f)).padding(horizontal = 10.dp, vertical = 4.dp)) {
                    Text(task.status, fontSize = 11.sp, fontFamily = InterFamily, color = statusColor)
                }
                val next = when (task.status) { "Pending" -> "In Progress"; "In Progress" -> "Completed"; else -> null }
                if (next != null) {
                    val btnCol = if (next == "In Progress") Color(0xFFF59E0B) else Color(0xFF10B981)
                    Box(
                        modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(btnCol)
                            .border(0.dp, Color.Transparent, RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(if (next == "In Progress") "Start" else "Done", fontSize = 12.sp, fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun TaskSummaryCard(label: String, count: String, color: Color, modifier: Modifier = Modifier) {
    TaskStatBox(label, count, color, modifier)
}
