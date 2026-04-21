package com.emt.app.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.emt.app.model.Attendance
import com.emt.app.ui.theme.InterFamily
import com.emt.app.ui.theme.PoppinsFamily
import com.emt.app.viewmodel.AttendanceViewModel
import com.emt.app.viewmodel.EmployeeViewModel
import com.emt.app.viewmodel.OpState
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(onBack: () -> Unit) {

    val attendanceVM: AttendanceViewModel = viewModel()
    val employeeVM: EmployeeViewModel = viewModel()

    val attendanceList by attendanceVM.attendanceList.collectAsState()
    val employees by employeeVM.employees.collectAsState()
    val selectedDate by attendanceVM.selectedDate.collectAsState()
    val opState by attendanceVM.opState.collectAsState()

    var showDatePicker by remember { mutableStateOf(false) }
    var showMarkDialog by remember { mutableStateOf(false) }
    var selectedEmployee by remember { mutableStateOf<com.emt.app.model.Employee?>(null) }
    var snackMsg by remember { mutableStateOf("") }

    // Snackbar on success/error
    LaunchedEffect(opState) {
        if (opState is OpState.Success) {
            snackMsg = "Attendance marked!"
            attendanceVM.resetOpState()
        } else if (opState is OpState.Error) {
            snackMsg = (opState as OpState.Error).message
            attendanceVM.resetOpState()
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(snackMsg) {
        if (snackMsg.isNotEmpty()) {
            snackbarHostState.showSnackbar(snackMsg)
            snackMsg = ""
        }
    }

    // Summary counts
    val presentCount = attendanceList.count { it.status == "Present" }
    val absentCount  = attendanceList.count { it.status == "Absent" }
    val halfCount    = attendanceList.count { it.status == "Half Day" }
    val leaveCount   = attendanceList.count { it.status == "Leave" }

    // Already marked employee IDs for today
    val markedIds = attendanceList.map { it.employeeId }.toSet()

    Scaffold(
        containerColor = Color(0xFF0D1117),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Box(
                modifier = Modifier.fillMaxWidth()
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
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Attendance",
                            fontSize = 22.sp,
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            formatDisplayDate(selectedDate),
                            fontSize = 12.sp,
                            fontFamily = InterFamily,
                            color = Color.White.copy(0.7f)
                        )
                    }
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.CalendarToday, null, tint = Color.White)
                    }
                }
            }
        },
        floatingActionButton = {
            if (employees.isNotEmpty()) {
                ExtendedFloatingActionButton(
                    onClick = { showMarkDialog = true },
                    containerColor = Color(0xFF00796B),
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    icon = { Icon(Icons.Default.HowToReg, null) },
                    text = { Text("Mark Attendance", fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold) }
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // ── Summary Cards ──────────────────────────
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AttendanceSummaryCard("Present",  presentCount.toString(), Color(0xFF10B981), Modifier.weight(1f))
                    AttendanceSummaryCard("Absent",   absentCount.toString(),  Color(0xFFF43F5E), Modifier.weight(1f))
                    AttendanceSummaryCard("Half Day", halfCount.toString(),    Color(0xFFF59E0B), Modifier.weight(1f))
                    AttendanceSummaryCard("Leave",    leaveCount.toString(),   Color(0xFF6366F1), Modifier.weight(1f))
                }
            }

            // ── Progress Bar ───────────────────────────
            item {
                val total = employees.size
                val marked = markedIds.size
                val progress = if (total > 0) marked.toFloat() / total else 0f

                Column(
                    modifier = Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF161B22))
                        .border(1.dp, Color.White.copy(0.06f), RoundedCornerShape(14.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Marking Progress",
                            fontSize = 13.sp,
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Text(
                            "$marked / $total employees",
                            fontSize = 12.sp,
                            fontFamily = InterFamily,
                            color = Color(0xFF9CA3AF)
                        )
                    }
                    Box(
                        modifier = Modifier.fillMaxWidth().height(8.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Color(0xFF21262D))
                    ) {
                        Box(
                            modifier = Modifier.fillMaxHeight()
                                .fillMaxWidth(progress)
                                .clip(RoundedCornerShape(50))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFF00796B), Color(0xFF4DB6AC))
                                    )
                                )
                        )
                    }
                }
            }

            // ── Attendance Records ─────────────────────
            if (attendanceList.isNotEmpty()) {
                item {
                    Text(
                        "TODAY'S RECORDS",
                        fontSize = 11.sp,
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF4B5563),
                        letterSpacing = 1.sp
                    )
                }
                items(attendanceList, key = { it.id }) { record ->
                    AttendanceRecordCard(record = record, onDelete = { attendanceVM.deleteAttendance(it) })
                }
            }

            // ── Unmarked Employees ─────────────────────
            val unmarked = employees.filter { it.id !in markedIds }
            if (unmarked.isNotEmpty()) {
                item {
                    Text(
                        "NOT MARKED YET (${unmarked.size})",
                        fontSize = 11.sp,
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFF59E0B),
                        letterSpacing = 1.sp
                    )
                }
                items(unmarked, key = { it.id }) { emp ->
                    UnmarkedEmployeeCard(
                        name = emp.name,
                        role = emp.role,
                        dept = emp.department,
                        onClick = {
                            selectedEmployee = emp
                            showMarkDialog = true
                        }
                    )
                }
            }

            if (employees.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Group, null, modifier = Modifier.size(56.dp), tint = Color(0xFF374151))
                            Text("No employees found", fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Color(0xFF4B5563))
                            Text("Add employees first to mark attendance", fontFamily = InterFamily, fontSize = 13.sp, color = Color(0xFF374151), textAlign = TextAlign.Center)
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }

    // ── Mark Attendance Dialog ─────────────────────────
    if (showMarkDialog) {
        MarkAttendanceDialog(
            employees = if (selectedEmployee != null) listOf(selectedEmployee!!) else employees.filter { it.id !in markedIds },
            preSelected = selectedEmployee,
            onDismiss = { showMarkDialog = false; selectedEmployee = null },
            onMark = { empId, empName, status, notes ->
                attendanceVM.markAttendance(empId, empName, status, notes)
                showMarkDialog = false
                selectedEmployee = null
            }
        )
    }

    // ── Simple Date Picker ─────────────────────────────
    if (showDatePicker) {
        DatePickerDialog(
            currentDate = selectedDate,
            onDateSelected = { attendanceVM.setDate(it) },
            onDismiss = { showDatePicker = false }
        )
    }
}

// ── Mark Attendance Dialog ─────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarkAttendanceDialog(
    employees: List<com.emt.app.model.Employee>,
    preSelected: com.emt.app.model.Employee?,
    onDismiss: () -> Unit,
    onMark: (String, String, String, String) -> Unit
) {
    var selectedEmp by remember { mutableStateOf(preSelected ?: employees.firstOrNull()) }
    var status by remember { mutableStateOf("Present") }
    var notes by remember { mutableStateOf("") }
    var empExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF161B22),
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                "Mark Attendance",
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.White
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {

                // Employee selector
                if (preSelected == null) {
                    ExposedDropdownMenuBox(
                        expanded = empExpanded,
                        onExpandedChange = { empExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedEmp?.name ?: "Select Employee",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Employee", fontFamily = InterFamily, fontSize = 13.sp) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = empExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF00796B),
                                unfocusedBorderColor = Color(0xFF374151),
                                focusedContainerColor = Color(0xFF0D1117),
                                unfocusedContainerColor = Color(0xFF0D1117),
                                focusedTextColor = Color.White, unfocusedTextColor = Color.White
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = empExpanded,
                            onDismissRequest = { empExpanded = false },
                            modifier = Modifier.background(Color(0xFF161B22))
                        ) {
                            employees.forEach { emp ->
                                DropdownMenuItem(
                                    text = { Text(emp.name, fontFamily = InterFamily, fontSize = 14.sp, color = Color.White) },
                                    onClick = { selectedEmp = emp; empExpanded = false }
                                )
                            }
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF00796B).copy(0.15f))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier.size(36.dp).clip(CircleShape)
                                .background(Color(0xFF00796B)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(preSelected.name.take(1), fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Column {
                            Text(preSelected.name, fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color.White)
                            Text(preSelected.role, fontFamily = InterFamily, fontSize = 12.sp, color = Color(0xFF9CA3AF))
                        }
                    }
                }

                // Status selector
                Text("Status", fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Color(0xFF9CA3AF))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Present" to Color(0xFF10B981), "Absent" to Color(0xFFF43F5E), "Half Day" to Color(0xFFF59E0B), "Leave" to Color(0xFF6366F1)).forEach { (s, c) ->
                        val selected = status == s
                        Box(
                            modifier = Modifier.weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selected) c else c.copy(0.1f))
                                .border(1.dp, if (selected) c else c.copy(0.3f), RoundedCornerShape(8.dp))
                                .clickable { status = s }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(s, fontSize = 10.sp, fontFamily = InterFamily, fontWeight = FontWeight.SemiBold,
                                color = if (selected) Color.White else c, textAlign = TextAlign.Center)
                        }
                    }
                }

                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (optional)", fontFamily = InterFamily, fontSize = 13.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    maxLines = 2,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00796B),
                        unfocusedBorderColor = Color(0xFF374151),
                        focusedContainerColor = Color(0xFF0D1117),
                        unfocusedContainerColor = Color(0xFF0D1117),
                        focusedTextColor = Color.White, unfocusedTextColor = Color.White
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val emp = selectedEmp ?: return@Button
                    onMark(emp.id, emp.name, status, notes)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Mark", fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", fontFamily = InterFamily, color = Color(0xFF6B7280))
            }
        }
    )
}

// ── Simple Date Picker Dialog ──────────────────────────
@Composable
fun DatePickerDialog(
    currentDate: String,
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    // Quick date selector: today, yesterday, -2 days, -3 days
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val today = Calendar.getInstance()
    val dates = (0..6).map { offset ->
        val cal = today.clone() as Calendar
        cal.add(Calendar.DAY_OF_YEAR, -offset)
        sdf.format(cal.time) to when (offset) {
            0 -> "Today"
            1 -> "Yesterday"
            else -> SimpleDateFormat("EEE, MMM d", Locale.getDefault()).format(cal.time)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF161B22),
        shape = RoundedCornerShape(20.dp),
        title = {
            Text("Select Date", fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                dates.forEach { (date, label) ->
                    val selected = date == currentDate
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selected) Color(0xFF00796B) else Color(0xFF0D1117))
                            .clickable { onDateSelected(date); onDismiss() }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(label, fontFamily = PoppinsFamily, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                            fontSize = 14.sp, color = Color.White)
                        Text(date, fontFamily = InterFamily, fontSize = 12.sp, color = if (selected) Color.White.copy(0.8f) else Color(0xFF6B7280))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", fontFamily = InterFamily, color = Color(0xFF4DB6AC))
            }
        }
    )
}

// ── Attendance Record Card ─────────────────────────────
@Composable
fun AttendanceRecordCard(record: Attendance, onDelete: (String) -> Unit) {
    val statusColor = when (record.status) {
        "Present"  -> Color(0xFF10B981)
        "Absent"   -> Color(0xFFF43F5E)
        "Half Day" -> Color(0xFFF59E0B)
        "Leave"    -> Color(0xFF6366F1)
        else       -> Color(0xFF6B7280)
    }
    val statusIcon = when (record.status) {
        "Present"  -> Icons.Default.CheckCircle
        "Absent"   -> Icons.Default.Cancel
        "Half Day" -> Icons.Default.AccessTime
        "Leave"    -> Icons.Default.BeachAccess
        else       -> Icons.Default.Circle
    }

    Row(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF161B22))
            .border(1.dp, statusColor.copy(0.2f), RoundedCornerShape(14.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Status icon
        Box(
            modifier = Modifier.size(42.dp).clip(CircleShape)
                .background(statusColor.copy(0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(statusIcon, null, tint = statusColor, modifier = Modifier.size(22.dp))
        }

        // Info
        Column(modifier = Modifier.weight(1f)) {
            Text(record.employeeName, fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color.White)
            if (record.checkInTime.isNotEmpty()) {
                Text("In: ${record.checkInTime}", fontFamily = InterFamily, fontSize = 11.sp, color = Color(0xFF6B7280))
            }
            if (record.notes.isNotEmpty()) {
                Text(record.notes, fontFamily = InterFamily, fontSize = 11.sp, color = Color(0xFF4B5563))
            }
        }

        // Status badge
        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Box(
                modifier = Modifier.clip(RoundedCornerShape(6.dp))
                    .background(statusColor.copy(0.15f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(record.status, fontSize = 11.sp, fontFamily = InterFamily, fontWeight = FontWeight.SemiBold, color = statusColor)
            }
            IconButton(onClick = { onDelete(record.id) }, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.DeleteOutline, null, tint = Color(0xFF374151), modifier = Modifier.size(16.dp))
            }
        }
    }
}

// ── Unmarked Employee Card ─────────────────────────────
@Composable
fun UnmarkedEmployeeCard(name: String, role: String, dept: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF161B22))
            .border(1.dp, Color(0xFFF59E0B).copy(0.2f), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier.size(38.dp).clip(CircleShape)
                .background(Color(0xFF374151)),
            contentAlignment = Alignment.Center
        ) {
            Text(name.take(1).uppercase(), fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF9CA3AF))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(name, fontFamily = PoppinsFamily, fontWeight = FontWeight.Medium, fontSize = 14.sp, color = Color(0xFFD1D5DB))
            Text("$role • $dept", fontFamily = InterFamily, fontSize = 11.sp, color = Color(0xFF4B5563))
        }
        Box(
            modifier = Modifier.clip(RoundedCornerShape(6.dp))
                .background(Color(0xFFF59E0B).copy(0.15f))
                .border(1.dp, Color(0xFFF59E0B).copy(0.3f), RoundedCornerShape(6.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text("Mark", fontSize = 11.sp, fontFamily = InterFamily, fontWeight = FontWeight.SemiBold, color = Color(0xFFF59E0B))
        }
    }
}

// ── Summary Stat Card ──────────────────────────────────
@Composable
fun AttendanceSummaryCard(label: String, count: String, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(0.1f))
            .border(1.dp, color.copy(0.25f), RoundedCornerShape(12.dp))
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(count, fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = color)
            Text(label, fontFamily = InterFamily, fontSize = 10.sp, color = Color(0xFF6B7280))
        }
    }
}

// ── Helpers ────────────────────────────────────────────
fun formatDisplayDate(dateStr: String): String {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = sdf.parse(dateStr) ?: return dateStr
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val yesterday = run {
            val cal = Calendar.getInstance(); cal.add(Calendar.DAY_OF_YEAR, -1)
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
        }
        when (dateStr) {
            today     -> "Today, ${SimpleDateFormat("MMM d", Locale.getDefault()).format(date)}"
            yesterday -> "Yesterday, ${SimpleDateFormat("MMM d", Locale.getDefault()).format(date)}"
            else      -> SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault()).format(date)
        }
    } catch (e: Exception) { dateStr }
}
