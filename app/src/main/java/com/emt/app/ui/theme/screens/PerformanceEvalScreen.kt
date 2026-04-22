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
    employeeVM   : EmployeeViewModel,
    performanceVM: PerformanceViewModel,
    onBack       : () -> Unit
) {
    val employees by employeeVM.employees.collectAsState()

    var selectedEmpName by remember { mutableStateOf("") }
    var selectedEmpId   by remember { mutableStateOf("") }
    var empExpanded     by remember { mutableStateOf(false) }
    var remarks         by remember { mutableStateOf("") }
    var errorMsg        by remember { mutableStateOf("") }
    var showSuccess     by remember { mutableStateOf(false) }

    var quality       by remember { mutableStateOf(3) }
    var timeliness    by remember { mutableStateOf(3) }
    var attendance    by remember { mutableStateOf(3) }
    var communication by remember { mutableStateOf(3) }
    var innovation    by remember { mutableStateOf(3) }

    val overall = (quality + timeliness + attendance + communication + innovation) / 5f

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Performance Review", fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
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
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SectionLabel("Select Employee")
            ExposedDropdownMenuBox(expanded = empExpanded, onExpandedChange = { empExpanded = it }) {
                OutlinedTextField(
                    value = selectedEmpName, onValueChange = {}, readOnly = true,
                    label = { Text("Employee *", fontFamily = InterFamily, fontSize = 14.sp) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = empExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape  = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00796B), unfocusedBorderColor = Color(0xFF21262D),
                        focusedContainerColor = Color.White, unfocusedContainerColor = Color(0xFF161B22))
                )
                ExposedDropdownMenu(expanded = empExpanded, onDismissRequest = { empExpanded = false }) {
                    employees.forEach { emp ->
                        DropdownMenuItem(
                            text = { Text(emp.name, fontFamily = InterFamily, fontSize = 14.sp) },
                            onClick = {
                                selectedEmpName = emp.name
                                selectedEmpId   = emp.id
                                empExpanded     = false
                            }
                        )
                    }
                }
            }

            // Overall Rating
            Card(shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF00796B)),
                modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Overall Rating", fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp, color = Color.White.copy(0.8f))
                    Text("${"%.1f".format(overall)} / 5.0", fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Bold, fontSize = 36.sp, color = Color.White)
                    Row {
                        repeat(5) { i ->
                            Icon(
                                if (i < overall.toInt()) Icons.Default.Star else Icons.Default.StarBorder,
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
                    focusedContainerColor = Color.White, unfocusedContainerColor = Color(0xFF161B22))
            )

            if (errorMsg.isNotEmpty()) {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                    shape = RoundedCornerShape(10.dp)) {
                    Text(errorMsg, color = Color(0xFFC62828),
                        modifier = Modifier.padding(12.dp), fontFamily = InterFamily, fontSize = 13.sp)
                }
            }
            if (showSuccess) {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                    shape = RoundedCornerShape(10.dp)) {
                    Text("✓ Performance review saved to Firebase!",
                        color = Color(0xFF2E7D32), modifier = Modifier.padding(12.dp),
                        fontFamily = InterFamily, fontSize = 13.sp)
                }
            }

            Button(
                onClick = {
                    errorMsg    = ""
                    showSuccess = false
                    if (selectedEmpId.isBlank()) { errorMsg = "Please select an employee"; return@Button }
                    val month = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date())
                    val perf = Performance(
                        employeeId         = selectedEmpId,
                        employeeName       = selectedEmpName,
                        month              = month,
                        qualityScore       = quality.toFloat(),
                        timelinessScore    = timeliness.toFloat(),
                        attendanceScore    = attendance.toFloat(),
                        communicationScore = communication.toFloat(),
                        innovationScore    = innovation.toFloat(),
                        overallRating      = overall,
                        remarks            = remarks.trim()
                    )
                    performanceVM.savePerformance(perf,
                        onSuccess = {
                            showSuccess = true
                            selectedEmpName = ""; selectedEmpId = ""; remarks = ""
                            quality = 3; timeliness = 3; attendance = 3; communication = 3; innovation = 3
                        },
                        onError = { errorMsg = it }
                    )
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape  = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B))
            ) {
                Text("Submit Review", fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun MetricSlider(label: String, value: Int, onValueChange: (Int) -> Unit) {
    Card(shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
        elevation = CardDefaults.cardElevation(1.dp), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Text(label, fontFamily = InterFamily, fontWeight = FontWeight.Medium,
                    fontSize = 14.sp, color = Color(0xFFD1D5DB))
                Row {
                    repeat(5) { i ->
                        Icon(
                            if (i < value) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = null,
                            tint = if (i < value) Color(0xFFFFB300) else Color(0xFFBDBDBD),
                            modifier = Modifier.size(28.dp).clickable { onValueChange(i + 1) }
                        )
                    }
                }
            }
        }
    }
}