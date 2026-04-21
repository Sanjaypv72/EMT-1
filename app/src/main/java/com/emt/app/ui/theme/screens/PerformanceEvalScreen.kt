package com.emt.app.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.emt.app.model.Performance
import com.emt.app.ui.components.SectionLabel
import com.emt.app.ui.theme.InterFamily
import com.emt.app.ui.theme.PoppinsFamily
import com.emt.app.viewmodel.EmployeeViewModel
import com.emt.app.viewmodel.PerformanceViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerformanceEvalScreen(
    onBack: () -> Unit,
    employeeViewModel: EmployeeViewModel = viewModel(),
    performanceViewModel: PerformanceViewModel = viewModel()
) {
    var selectedEmpId   by remember { mutableStateOf("") }
    var selectedEmpName by remember { mutableStateOf("") }
    var empExpanded     by remember { mutableStateOf(false) }
    var remarks         by remember { mutableStateOf("") }
    var showSuccess     by remember { mutableStateOf(false) }
    var errorMsg        by remember { mutableStateOf<String?>(null) }

    var quality       by remember { mutableStateOf(3) }
    var timeliness    by remember { mutableStateOf(3) }
    var attendance    by remember { mutableStateOf(3) }
    var communication by remember { mutableStateOf(3) }
    var innovation    by remember { mutableStateOf(3) }

    val overall   = (quality + timeliness + attendance + communication + innovation) / 5f
    val employees by employeeViewModel.employees.collectAsState()
    val isLoading by performanceViewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Performance Review", fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF00796B), titleContentColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding)
                .background(Color(0xFF0D1117))
                .verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SectionLabel("Select Employee")
            // ✅ Live employees from Firebase
            ExposedDropdownMenuBox(expanded = empExpanded, onExpandedChange = { empExpanded = it }) {
                OutlinedTextField(
                    value = selectedEmpName, onValueChange = {}, readOnly = true,
                    label = { Text("Employee *", fontFamily = InterFamily, fontSize = 14.sp) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = empExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00796B), unfocusedBorderColor = Color(0xFF21262D),
                        focusedContainerColor = Color.White, unfocusedContainerColor = Color(0xFF161B22)
                    )
                )
                ExposedDropdownMenu(expanded = empExpanded, onDismissRequest = { empExpanded = false }) {
                    employees.forEach { emp ->
                        DropdownMenuItem(
                            text = { Text(emp.name, fontFamily = InterFamily, fontSize = 14.sp) },
                            onClick = {
                                selectedEmpId   = emp.id    // ✅ real Firebase ID
                                selectedEmpName = emp.name
                                empExpanded     = false
                            }
                        )
                    }
                }
            }

            // Overall Rating Card
            Card(shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF00796B)),
                modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Overall Rating", fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.SemiBold, fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f))
                    Text("${"%.1f".format(overall)} / 5.0", fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Bold, fontSize = 36.sp, color = Color.White)
                    Row {
                        repeat(5) { index ->
                            Icon(
                                if (index < overall.toInt()) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = null, tint = Color(0xFFFFD700),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            SectionLabel("Rate Each Metric (1–5)")
            MetricSlider("Quality",       quality)       { quality = it }
            MetricSlider("Timeliness",    timeliness)    { timeliness = it }
            MetricSlider("Attendance",    attendance)    { attendance = it }
            MetricSlider("Communication", communication) { communication = it }
            MetricSlider("Innovation",    innovation)    { innovation = it }

            SectionLabel("Remarks")
            OutlinedTextField(
                value = remarks, onValueChange = { remarks = it },
                label = { Text("Add remarks (optional)", fontFamily = InterFamily, fontSize = 14.sp) },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                shape = RoundedCornerShape(12.dp), maxLines = 4,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF00796B), unfocusedBorderColor = Color(0xFF21262D),
                    focusedContainerColor = Color.White, unfocusedContainerColor = Color(0xFF161B22)
                )
            )

            if (showSuccess) {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    shape = RoundedCornerShape(10.dp)) {
                    Text("✓ Review saved to Firebase!", color = Color(0xFF2E7D32),
                        modifier = Modifier.padding(12.dp), fontFamily = InterFamily, fontSize = 13.sp)
                }
            }
            errorMsg?.let {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                    shape = RoundedCornerShape(10.dp)) {
                    Text("✗ $it", color = Color(0xFFC62828),
                        modifier = Modifier.padding(12.dp), fontSize = 13.sp)
                }
            }

            // ✅ Save to Firebase
            Button(
                onClick = {
                    if (selectedEmpId.isBlank()) {
                        errorMsg = "Please select an employee"; return@Button
                    }
                    errorMsg = null
                    val today = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                    val perf  = Performance(
                        employeeId         = selectedEmpId,
                        date               = today,
                        qualityScore       = quality.toFloat(),
                        timelinessScore    = timeliness.toFloat(),
                        attendanceScore    = attendance.toFloat(),
                        communicationScore = communication.toFloat(),
                        innovationScore    = innovation.toFloat(),
                        overallRating      = overall,
                        remarks            = remarks
                    )
                    performanceViewModel.submitReview(perf,
                        onSuccess = {
                            showSuccess = true
                            selectedEmpId = ""; selectedEmpName = ""; remarks = ""
                            quality = 3; timeliness = 3; attendance = 3
                            communication = 3; innovation = 3
                        },
                        onError = { errorMsg = it }
                    )
                },
                enabled  = !isLoading,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape    = RoundedCornerShape(12.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B))
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White,
                        modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                } else {
                    Text("Submit Review", fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun MetricSlider(label: String, value: Int, onValueChange: (Int) -> Unit) {
    Card(shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
        modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Text(label, fontFamily = InterFamily, fontWeight = FontWeight.Medium,
                    fontSize = 14.sp, color = Color(0xFFD1D5DB))
                Row {
                    repeat(5) { index ->
                        Icon(
                            if (index < value) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = null,
                            tint = if (index < value) Color(0xFFFFB300) else Color(0xFFBDBDBD),
                            modifier = Modifier.size(28.dp).clickable { onValueChange(index + 1) }
                        )
                    }
                }
            }
        }
    }
}