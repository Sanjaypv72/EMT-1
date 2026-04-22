package com.emt.app.ui.theme.screens
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
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
import com.emt.app.model.Employee
import com.emt.app.ui.theme.InterFamily
import com.emt.app.ui.theme.PoppinsFamily
import com.emt.app.viewmodel.AttendanceViewModel
import com.emt.app.viewmodel.EmployeeViewModel
import com.emt.app.viewmodel.PerformanceViewModel
import com.emt.app.viewmodel.TaskViewModel

val chartColors = listOf(
    Color(0xFF00796B), Color(0xFFFFB300), Color(0xFFE65100),
    Color(0xFF7B1FA2), Color(0xFF00838F), Color(0xFFC62828),
    Color(0xFF43A047), Color(0xFF37474F)
)

fun deptColor(dept: String): Color {
    val idx = dept.hashCode().and(0x7FFFFFFF) % chartColors.size
    return chartColors[idx]
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsDashboard(
    employeeVM       : EmployeeViewModel,
    taskVM           : TaskViewModel,
    performanceVM    : PerformanceViewModel,
    attendanceVM     : AttendanceViewModel,          // ✅ NEW
    onSettingsClick  : () -> Unit,
    onAttendanceClick: () -> Unit,                   // ✅ NEW
    onReportsClick   : () -> Unit                    // ✅ NEW
) {
    val employees by employeeVM.employees.collectAsState()
    val tasks by taskVM.tasks.collectAsState()
    val performance by performanceVM.allPerformance.collectAsState()
    val allAttendance by attendanceVM.allAttendance.collectAsState() // ✅ NEW

    val totalEmployees = employees.size
    val completedTasks = tasks.count { it.status == "Completed" || it.status == "Reviewed" }
    val avgRating = if (performance.isEmpty()) 0.0
    else performance.map { it.overallRating.toDouble() }.average()

    // Attendance stats for today
    val todayPresent = allAttendance.count { it.status == "Present" || it.status == "Late" }
    val todayAbsent = allAttendance.count { it.status == "Absent" }

    // Per-employee avg rating for bar chart
    val empRatings = employees.map { emp ->
        val reviews = performance.filter { it.employeeId == emp.id }
        val avg =
            if (reviews.isEmpty()) 0f else reviews.map { it.overallRating }.average().toFloat()
        emp to avg
    }

    // Per-dept avg rating
    val deptRatings = employees.groupBy { it.department }.map { (dept, emps) ->
        val empIds = emps.map { it.id }
        val reviews = performance.filter { it.employeeId in empIds }
        val avg =
            if (reviews.isEmpty()) 0f else reviews.map { it.overallRating }.average().toFloat()
        dept to avg
    }.filter { it.second > 0f }

    // Top 3 performers
    val topPerformers = empRatings.sortedByDescending { it.second }.take(3)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Analytics & Insights", fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Bold, fontSize = 20.sp
                    )
                },
                actions = {
                    // ✅ Reports button
                    IconButton(onClick = onReportsClick) {
                        Icon(
                            Icons.Default.Assessment,
                            contentDescription = "Reports",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF00796B), titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).background(Color(0xFFFAFAFA)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── KPI Cards ──────────────────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    KpiCard(
                        "Employees", totalEmployees.toString(),
                        Icons.Default.Group, Color(0xFF00796B), Modifier.weight(1f)
                    )
                    KpiCard(
                        "Tasks Done", completedTasks.toString(),
                        Icons.Default.CheckCircle, Color(0xFFFFB300), Modifier.weight(1f)
                    )
                    KpiCard(
                        "Avg Rating", "${"%.1f".format(avgRating)}★",
                        Icons.Default.Star, Color(0xFFE65100), Modifier.weight(1f)
                    )
                }
            }

            // ── Attendance Quick Panel (clickable → goes to AttendanceScreen) ──
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp),
                    modifier = Modifier.fillMaxWidth().clickable { onAttendanceClick() }
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xFF00796B).copy(0.07f),
                                        Color(0xFF004D40).copy(0.04f)
                                    )
                                )
                            )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        Icons.Default.HowToReg, null,
                                        tint = Color(0xFF00796B), modifier = Modifier.size(22.dp)
                                    )
                                    Text(
                                        "Attendance Overview", fontFamily = PoppinsFamily,
                                        fontWeight = FontWeight.SemiBold, fontSize = 15.sp,
                                        color = Color(0xFF212121)
                                    )
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        "View All", fontFamily = InterFamily, fontSize = 12.sp,
                                        color = Color(0xFF00796B)
                                    )
                                    Icon(
                                        Icons.Default.ArrowForwardIos, null,
                                        tint = Color(0xFF00796B), modifier = Modifier.size(12.dp)
                                    )
                                }
                            }

                            Spacer(Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                AttendanceStatPill(
                                    label = "Present",
                                    count = allAttendance.count { it.status == "Present" },
                                    color = Color(0xFF10B981),
                                    modifier = Modifier.weight(1f)
                                )
                                AttendanceStatPill(
                                    label = "Late",
                                    count = allAttendance.count { it.status == "Late" },
                                    color = Color(0xFFF97316),
                                    modifier = Modifier.weight(1f)
                                )
                                AttendanceStatPill(
                                    label = "Absent",
                                    count = allAttendance.count { it.status == "Absent" },
                                    color = Color(0xFFF43F5E),
                                    modifier = Modifier.weight(1f)
                                )
                                AttendanceStatPill(
                                    label = "Half Day",
                                    count = allAttendance.count { it.status == "Half Day" },
                                    color = Color(0xFFF59E0B),
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            if (allAttendance.isNotEmpty()) {
                                Spacer(Modifier.height(10.dp))
                                val presentFraction =
                                    (todayPresent.toFloat() / totalEmployees.coerceAtLeast(1)).coerceIn(
                                        0f,
                                        1f
                                    )
                                Text(
                                    "${todayPresent} of $totalEmployees employees present",
                                    fontFamily = InterFamily, fontSize = 11.sp,
                                    color = Color(0xFF9E9E9E)
                                )
                                Spacer(Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { presentFraction },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }




            }
        }
    }
}
@Composable
fun KpiCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Icon(icon, contentDescription = null, tint = color)
            Text(title)
            Text(value, fontWeight = FontWeight.Bold)
        }
    }
}
@Composable
fun AttendanceStatPill(
    label: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Text(
        text = "$label: $count",
        modifier = modifier
            .background(color.copy(0.1f))
            .padding(6.dp)
    )
}