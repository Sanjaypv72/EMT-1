package com.emt.app.ui.theme.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- ADDED COROUTINE IMPORTS ---
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// --- CHECK THESE PATHS (THEY MUST MATCH YOUR FOLDER STRUCTURE) ---
import com.emt.app.ui.theme.InterFamily
import com.emt.app.ui.theme.PoppinsFamily
import com.emt.app.utils.ExcelReportHelper
import com.emt.app.utils.PdfReportHelper
import com.emt.app.viewmodel.AttendanceViewModel
import com.emt.app.viewmodel.EmployeeViewModel
import com.emt.app.viewmodel.PerformanceViewModel
import com.emt.app.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    employeeVM: EmployeeViewModel,
    taskVM: TaskViewModel,
    performanceVM: PerformanceViewModel,
    attendanceVM: AttendanceViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Observe state from ViewModels
    val employees   by employeeVM.employees.collectAsState(initial = emptyList())
    val tasks       by taskVM.tasks.collectAsState(initial = emptyList())
    // Fixed: changed performanceList to allPerformance to match your ViewModel
    val performance by performanceVM.allPerformance.collectAsState(initial = emptyList())
    val attendance  by attendanceVM.allAttendance.collectAsState(initial = emptyList())

    var pdfLoading by remember { mutableStateOf(false) }
    var excelLoading by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color(0xFF0D1117),
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.horizontalGradient(listOf(Color(0xFF00796B), Color(0xFF004D40))))
                    .padding(horizontal = 8.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                    }
                    Column {
                        Text(
                            "Reports", fontSize = 22.sp, fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Bold, color = Color.White
                        )
                        Text(
                            "Download PDF or Excel report", fontSize = 12.sp,
                            fontFamily = InterFamily, color = Color.White.copy(0.7f)
                        )
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    "Data Overview", fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatBox("Employees", "${employees.size}", Color(0xFF00796B), Modifier.weight(1f))
                    StatBox("Tasks", "${tasks.size}", Color(0xFFF59E0B), Modifier.weight(1f))
                    StatBox("Performance", "${performance.size}", Color(0xFF6366F1), Modifier.weight(1f))
                    StatBox("Attendance", "${attendance.size}", Color(0xFF10B981), Modifier.weight(1f))
                }
            }

            item {
                ReportCard(
                    title = "PDF Report",
                    description = "Generates a multi-page PDF with Summary, Employees, Tasks, Performance, and Attendance.",
                    icon = Icons.Default.PictureAsPdf,
                    color = Color(0xFFF43F5E),
                    loading = pdfLoading,
                    buttonText = "Download PDF"
                ) {
                    pdfLoading = true
                    scope.launch {
                        try {
                            val file = withContext(Dispatchers.IO) {
                                PdfReportHelper.generateReport(context, employees, tasks, performance, attendance)
                            }
                            PdfReportHelper.shareFile(context, file)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                        } finally {
                            pdfLoading = false
                        }
                    }
                }
            }

            item {
                ReportCard(
                    title = "Excel Report",
                    description = "Generates an .xlsx file with 5 sheets: Summary, Employees, Tasks, Performance, and Attendance.",
                    icon = Icons.Default.TableChart,
                    color = Color(0xFF10B981),
                    loading = excelLoading,
                    buttonText = "Download Excel"
                ) {
                    excelLoading = true
                    scope.launch {
                        try {
                            val file = withContext(Dispatchers.IO) {
                                ExcelReportHelper.generateReport(context, employees, tasks, performance, attendance)
                            }
                            ExcelReportHelper.shareFile(context, file)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                        } finally {
                            excelLoading = false
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReportCard(
    title: String, description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color, loading: Boolean, buttonText: String,
    onClick: () -> Unit
) {
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22))) {
        Column(modifier = Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(icon, null, tint = color, modifier = Modifier.size(32.dp))
                Text(title, fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
            }
            Text(description, color = Color(0xFF9CA3AF), fontSize = 13.sp)
            Button(onClick = onClick, enabled = !loading, colors = ButtonDefaults.buttonColors(containerColor = color)) {
                Text(buttonText)
            }
        }
    }
}

@Composable
fun StatBox(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = color.copy(0.1f))) {
        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, fontWeight = FontWeight.Bold, color = color)
            Text(label, fontSize = 10.sp, color = Color.Gray)
        }
    }
}
// ... (Keep ReportCard and StatBox functions from previous code)