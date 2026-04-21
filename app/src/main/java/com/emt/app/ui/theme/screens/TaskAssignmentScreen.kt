package com.emt.app.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.emt.app.model.Task
import com.emt.app.ui.components.EMTTextField
import com.emt.app.ui.components.PriorityChip
import com.emt.app.ui.components.SectionLabel
import com.emt.app.ui.theme.InterFamily
import com.emt.app.ui.theme.PoppinsFamily
import com.emt.app.viewmodel.EmployeeViewModel
import com.emt.app.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskAssignmentScreen(
    onBack: () -> Unit,
    employeeViewModel: EmployeeViewModel = viewModel(),
    taskViewModel: TaskViewModel = viewModel()
) {
    var title         by remember { mutableStateOf("") }
    var description   by remember { mutableStateOf("") }
    var deadline      by remember { mutableStateOf("") }
    var priority      by remember { mutableStateOf("Medium") }
    var assignedTo    by remember { mutableStateOf("") }
    var assignedEmpId by remember { mutableStateOf("") }
    var showSuccess   by remember { mutableStateOf(false) }
    var errorMsg      by remember { mutableStateOf<String?>(null) }

    val priorities = listOf("Low", "Medium", "High")
    val employees  by employeeViewModel.employees.collectAsState()
    val isLoading  by taskViewModel.isLoading.collectAsState()

    var priorityExpanded by remember { mutableStateOf(false) }
    var assignedExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Assign Task", fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Bold, fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back", tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF00796B),
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFF0D1117))
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            SectionLabel("Task Details")

            EMTTextField(
                value = title,
                onValueChange = { title = it },
                label = "Task Title *"
            )

            EMTTextField(
                value = description,
                onValueChange = { description = it },
                label = "Description",
                singleLine = false,
                modifier = Modifier.height(100.dp)
            )

            EMTTextField(
                value = deadline,
                onValueChange = { deadline = it },
                label = "Deadline (DD/MM/YYYY)"
            )

            SectionLabel("Priority")

            ExposedDropdownMenuBox(
                expanded = priorityExpanded,
                onExpandedChange = { priorityExpanded = it }
            ) {
                OutlinedTextField(
                    value = priority,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Priority *", fontFamily = InterFamily, fontSize = 14.sp) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = priorityExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00796B),
                        unfocusedBorderColor = Color(0xFF21262D),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color(0xFF161B22)
                    )
                )
                ExposedDropdownMenu(
                    expanded = priorityExpanded,
                    onDismissRequest = { priorityExpanded = false }
                ) {
                    priorities.forEach { p ->
                        DropdownMenuItem(
                            text = { Text(p, fontFamily = InterFamily, fontSize = 14.sp) },
                            onClick = { priority = p; priorityExpanded = false }
                        )
                    }
                }
            }

            Row { PriorityChip(priority = priority) }

            SectionLabel("Assign To")

            ExposedDropdownMenuBox(
                expanded = assignedExpanded,
                onExpandedChange = { assignedExpanded = it }
            ) {
                OutlinedTextField(
                    value = assignedTo,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Select Employee *", fontFamily = InterFamily, fontSize = 14.sp) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = assignedExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00796B),
                        unfocusedBorderColor = Color(0xFF21262D),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color(0xFF161B22)
                    )
                )
                ExposedDropdownMenu(
                    expanded = assignedExpanded,
                    onDismissRequest = { assignedExpanded = false }
                ) {
                    employees.forEach { emp ->
                        DropdownMenuItem(
                            text = { Text(emp.name, fontFamily = InterFamily, fontSize = 14.sp) },
                            onClick = {
                                assignedTo    = emp.name
                                assignedEmpId = emp.id
                                assignedExpanded = false
                            }
                        )
                    }
                }
            }

            // Success banner
            if (showSuccess) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        "✓ Task assigned to Firebase!",
                        color = Color(0xFF2E7D32),
                        modifier = Modifier.padding(12.dp),
                        fontFamily = InterFamily,
                        fontSize = 13.sp
                    )
                }
            }

            // Error banner
            errorMsg?.let { msg ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        "✗ $msg",
                        color = Color(0xFFC62828),
                        modifier = Modifier.padding(12.dp),
                        fontFamily = InterFamily,
                        fontSize = 13.sp
                    )
                }
            }

            // Assign Task button — saves to Firebase
            Button(
                onClick = {
                    if (title.isBlank() || assignedEmpId.isBlank()) {
                        errorMsg = "Please fill title and select an employee"
                        return@Button
                    }
                    errorMsg = null
                    showSuccess = false

                    val task = Task(
                        employeeId   = assignedEmpId,
                        description  = description.ifBlank { title },
                        deadline     = deadline,
                        priority     = priority,
                        status       = "Pending",
                        assignedDate = SimpleDateFormat(
                            "dd/MM/yyyy", Locale.getDefault()
                        ).format(Date())
                    )

                    taskViewModel.addTask(
                        task      = task,
                        onSuccess = {
                            showSuccess   = true
                            title         = ""
                            description   = ""
                            deadline      = ""
                            assignedTo    = ""
                            assignedEmpId = ""
                            priority      = "Medium"
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
                    CircularProgressIndicator(
                        color    = Color.White,
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        "Assign Task",
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 16.sp
                    )
                }
            }
        }
    }
}
