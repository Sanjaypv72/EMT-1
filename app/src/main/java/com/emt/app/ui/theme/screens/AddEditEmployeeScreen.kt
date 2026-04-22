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
// --- CRITICAL IMPORTS FOR THE 'BY' KEYWORD ---
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emt.app.model.Employee
import com.emt.app.ui.components.EMTTextField
import com.emt.app.ui.components.SectionLabel
import com.emt.app.ui.theme.InterFamily
import com.emt.app.ui.theme.PoppinsFamily
import com.emt.app.viewmodel.EmployeeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditEmployeeScreen(
    existingEmployee: Employee?,
    employeeVM: EmployeeViewModel,
    onBack: () -> Unit
) {
    val isEditMode = existingEmployee != null
    val title = if (isEditMode) "Edit Employee" else "Add Employee"

    // State management
    var fullName by remember { mutableStateOf(existingEmployee?.name ?: "") }
    var role by remember { mutableStateOf(existingEmployee?.role ?: "") }
    var department by remember { mutableStateOf(existingEmployee?.department ?: "") }
    var email by remember { mutableStateOf(existingEmployee?.email ?: "") }
    var phone by remember { mutableStateOf(existingEmployee?.contact ?: "") }
    var joiningDate by remember { mutableStateOf(existingEmployee?.joiningDate ?: "") }

    var deptExpanded by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }
    var showSuccess by remember { mutableStateOf(false) }
    var isLocalLoading by remember { mutableStateOf(false) }

    val deptList = listOf("Engineering", "Design", "HR", "Marketing", "Sales", "Finance", "Operations")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
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
                .background(Color(0xFFF5F5F5))
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            SectionLabel("Personal Info")

            EMTTextField(value = fullName, onValueChange = { fullName = it }, label = "Full Name *")
            EMTTextField(value = role, onValueChange = { role = it }, label = "Role / Designation *")

            SectionLabel("Department")

            ExposedDropdownMenuBox(expanded = deptExpanded, onExpandedChange = { deptExpanded = it }) {
                OutlinedTextField(
                    value = department,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Department *", fontFamily = InterFamily, fontSize = 14.sp) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = deptExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00796B),
                        unfocusedBorderColor = Color(0xFF21262D),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )
                ExposedDropdownMenu(expanded = deptExpanded, onDismissRequest = { deptExpanded = false }) {
                    deptList.forEach { d ->
                        DropdownMenuItem(
                            text = { Text(d, fontFamily = InterFamily, fontSize = 14.sp) },
                            onClick = { department = d; deptExpanded = false }
                        )
                    }
                }
            }

            SectionLabel("Contact Details")

            EMTTextField(value = email, onValueChange = { email = it }, label = "Email Address")
            EMTTextField(value = phone, onValueChange = { phone = it }, label = "Phone Number")
            EMTTextField(value = joiningDate, onValueChange = { joiningDate = it }, label = "Joining Date (DD/MM/YYYY)")

            // Status Messages
            if (errorMsg.isNotEmpty()) {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)), shape = RoundedCornerShape(10.dp)) {
                    Text(errorMsg, color = Color(0xFFC62828), modifier = Modifier.padding(12.dp), fontFamily = InterFamily, fontSize = 13.sp)
                }
            }

            if (showSuccess) {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)), shape = RoundedCornerShape(10.dp)) {
                    Text(
                        if (isEditMode) "✓ Employee updated!" else "✓ Employee saved!",
                        color = Color(0xFF2E7D32), modifier = Modifier.padding(12.dp), fontFamily = InterFamily, fontSize = 13.sp
                    )
                }
            }

            Button(
                onClick = {
                    if (fullName.isBlank() || role.isBlank() || department.isBlank()) {
                        errorMsg = "Please fill all required fields (*)"
                        return@Button
                    }

                    val employee = Employee(
                        id = existingEmployee?.id ?: "",
                        name = fullName.trim(),
                        role = role.trim(),
                        department = department,
                        email = email.trim(),
                        contact = phone.trim(),
                        joiningDate = joiningDate.trim(),
                        isActive = existingEmployee?.isActive ?: true
                    )

                    isLocalLoading = true
                    errorMsg = ""

                    if (isEditMode) {
                        employeeVM.updateEmployee(employee) { success ->
                            isLocalLoading = false
                            if (success) showSuccess = true else errorMsg = "Update failed"
                        }
                    } else {
                        // Using the standard add logic
                        employeeVM.addEmployee(employee,
                            onSuccess = {
                                isLocalLoading = false
                                showSuccess = true
                                fullName = ""; role = ""; department = ""; email = ""; phone = ""; joiningDate = ""
                            },
                            onError = {
                                isLocalLoading = false
                                errorMsg = it
                            }
                        )
                    }
                },
                enabled = !isLocalLoading,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B))
            ) {
                if (isLocalLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                } else {
                    Text(if (isEditMode) "Update Employee" else "Save Employee",
                        fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                }
            }
        }
    }
}