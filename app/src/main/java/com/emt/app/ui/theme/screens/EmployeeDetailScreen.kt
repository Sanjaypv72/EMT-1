package com.emt.app.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emt.app.model.Employee
import com.emt.app.model.Task
import com.emt.app.model.Performance
import com.emt.app.ui.components.DarkMetricBar
import com.emt.app.ui.theme.InterFamily
import com.emt.app.ui.theme.PoppinsFamily
import com.emt.app.viewmodel.TaskViewModel
import com.emt.app.viewmodel.PerformanceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeDetailScreen(
    employee     : Employee,
    taskVM       : TaskViewModel,
    performanceVM: PerformanceViewModel,
    onBack       : () -> Unit,
    onEditClick  : () -> Unit
) {
    // ✅ Load real Firebase data for this employee
    LaunchedEffect(employee.id) {
        taskVM.loadTasksForEmployee(employee.id)
        performanceVM.loadForEmployee(employee.id)
    }

    val empTasks   by taskVM.empTasks.collectAsState()
    val perfList   by performanceVM.empPerformance.collectAsState()
    val latestPerf = perfList.firstOrNull()

    val avatarColor = deptColor(employee.department)

    Scaffold(
        containerColor = Color(0xFF0D1117),
        topBar = {
            Box(
                modifier = Modifier.fillMaxWidth()
                    .background(Brush.horizontalGradient(
                        listOf(avatarColor.copy(0.8f), avatarColor.copy(0.4f))))
                    .padding(horizontal = 8.dp, vertical = 12.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                    }
                    Text(employee.name, fontSize = 18.sp, fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.weight(1f))
                    IconButton(onClick = onEditClick) {
                        Icon(Icons.Default.Edit, null, tint = Color.White)
                    }
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())) {

            // Hero
            Box(modifier = Modifier.fillMaxWidth()
                .background(Brush.verticalGradient(listOf(avatarColor.copy(0.2f), Color.Transparent)))
                .padding(24.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(18.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(68.dp).clip(CircleShape)
                            .background(Brush.linearGradient(listOf(avatarColor, avatarColor.copy(0.6f)))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(employee.name.take(1).uppercase(), fontSize = 28.sp,
                            fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(employee.name, fontSize = 20.sp, fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Bold, color = Color.White)
                        Text(employee.role, fontSize = 13.sp, fontFamily = InterFamily,
                            color = Color(0xFF9CA3AF))
                        Box(modifier = Modifier.clip(RoundedCornerShape(6.dp))
                            .background(avatarColor.copy(0.2f))
                            .padding(horizontal = 10.dp, vertical = 3.dp)) {
                            Text(employee.department, fontSize = 11.sp, fontFamily = InterFamily,
                                color = avatarColor)
                        }
                    }
                }
            }

            Column(modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)) {

                // Contact
                DetailSectionLabel("Contact Info")
                Column(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF161B22))
                        .border(1.dp, Color.White.copy(0.05f), RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    DetailInfoRow(Icons.Default.Email,        "Email",  employee.email,       avatarColor)
                    DetailInfoRow(Icons.Default.Phone,        "Phone",  employee.contact,     avatarColor)
                    DetailInfoRow(Icons.Default.CalendarToday,"Joined", employee.joiningDate, avatarColor)
                }

                // Tasks — from Firebase
                DetailSectionLabel("Tasks (${empTasks.size})")
                if (empTasks.isEmpty()) {
                    Text("No tasks assigned", fontFamily = InterFamily, fontSize = 14.sp,
                        color = Color(0xFF4B5563), modifier = Modifier.padding(vertical = 4.dp))
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        empTasks.forEach { task ->
                            val pCol = when (task.priority) {
                                "High"   -> Color(0xFFF43F5E)
                                "Medium" -> Color(0xFFF59E0B)
                                else     -> Color(0xFF10B981)
                            }
                            val sCol = when (task.status) {
                                "Completed","Reviewed" -> Color(0xFF10B981)
                                "In Progress"          -> Color(0xFFF59E0B)
                                else                   -> Color(0xFF6B7280)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF161B22))
                                    .border(1.dp, Color.White.copy(0.05f), RoundedCornerShape(12.dp))
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment     = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(task.title, fontFamily = PoppinsFamily,
                                        fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color.White)
                                    Text("Due ${task.deadline}", fontFamily = InterFamily,
                                        fontSize = 12.sp, color = Color(0xFF4B5563))
                                }
                                Spacer(Modifier.width(10.dp))
                                Column(horizontalAlignment = Alignment.End,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Box(modifier = Modifier.clip(RoundedCornerShape(5.dp))
                                        .background(sCol.copy(0.12f))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)) {
                                        Text(task.status, fontSize = 10.sp, fontFamily = InterFamily, color = sCol)
                                    }
                                    Box(modifier = Modifier.clip(RoundedCornerShape(5.dp))
                                        .background(pCol.copy(0.12f))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)) {
                                        Text(task.priority, fontSize = 10.sp, fontFamily = InterFamily, color = pCol)
                                    }
                                }
                            }
                        }
                    }
                }

                // Performance — from Firebase
                DetailSectionLabel("Latest Performance")
                if (latestPerf == null) {
                    Text("No reviews yet", fontFamily = InterFamily, fontSize = 14.sp, color = Color(0xFF4B5563))
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF161B22))
                            .border(1.dp, Color.White.copy(0.05f), RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment     = Alignment.CenterVertically) {
                            Text(latestPerf.month, fontFamily = PoppinsFamily,
                                fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color.White)
                            Box(modifier = Modifier.clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFD97706).copy(0.15f))
                                .padding(horizontal = 12.dp, vertical = 5.dp)) {
                                Text("★ ${"%.1f".format(latestPerf.overallRating)}/5",
                                    fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp, color = Color(0xFFFBBF24))
                            }
                        }
                        listOf(
                            "Quality"       to latestPerf.qualityScore,
                            "Timeliness"    to latestPerf.timelinessScore,
                            "Attendance"    to latestPerf.attendanceScore,
                            "Communication" to latestPerf.communicationScore,
                            "Innovation"    to latestPerf.innovationScore
                        ).forEach { (l, v) -> DarkMetricBar(l, v) }
                    }
                }

                Spacer(Modifier.height(28.dp))
            }
        }
    }
}

@Composable fun DetailSectionLabel(text: String) {
    Text(text, fontSize = 12.sp, fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold,
        color = Color(0xFF4B5563), letterSpacing = 1.sp)
}

@Composable fun DetailInfoRow(icon: ImageVector, label: String, value: String, accent: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(accent.copy(0.12f)),
            contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = accent, modifier = Modifier.size(16.dp))
        }
        Column {
            Text(label, fontSize = 11.sp, fontFamily = InterFamily, color = Color(0xFF4B5563))
            Text(value.ifBlank { "—" }, fontSize = 14.sp, fontFamily = InterFamily,
                fontWeight = FontWeight.Medium, color = Color(0xFFD1D5DB))
        }
    }
}