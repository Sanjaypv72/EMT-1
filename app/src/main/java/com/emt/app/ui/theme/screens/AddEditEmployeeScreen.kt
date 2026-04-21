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
import com.emt.app.model.Employee
import com.emt.app.ui.components.EMTTextField
import com.emt.app.ui.components.SectionLabel
import com.emt.app.ui.theme.PoppinsFamily
import com.emt.app.viewmodel.EmployeeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditEmployeeScreen(
    existingEmployee: EmployeeUI? = null,
    onBack: () -> Unit,
    viewModel: EmployeeViewModel = viewModel()
) {
    val isEditing = existingEmployee != null

    var name        by remember { mutableStateOf(existingEmployee?.name ?: "") }
    var role        by remember { mutableStateOf(existingEmployee?.role ?: "") }
    var department  by remember { mutableStateOf(existingEmployee?.department ?: "") }
    var joiningDate by remember { mutableStateOf(existingEmployee?.joiningDate ?: "") }
    var email       by remember { mutableStateOf(existingEmployee?.email ?: "") }
    var contact     by remember { mutableStateOf(existingEmployee?.contact ?: "") }

    val deptList = listOf("Engineering","Design","HR","Marketing","Sales","Finance","Operations")
    var deptExpanded by remember { mutableStateOf(false) }

    val isLoading by viewModel.isLoading.collectAsState()
    var showSuccess by remember { mutableStateOf(false) }
    var errorMsg    by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditing) "Edit Employee" else "Add Employee",
                        fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold, fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF00796B), titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize().padding(padding)
                .background(Color(0xFF0D1117))
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            SectionLabel("Personal Info")
            EMTTextField(value = name,        onValueChange = { name = it },        label = "Full Name *")
            EMTTextField(value = role,        onValueChange = { role = it },        label = "Role / Designation *")

            SectionLabel("Department")
            ExposedDropdownMenuBox(expanded = deptExpanded, onExpandedChange = { deptExpanded = it }) {
                OutlinedTextField(
                    value = department, onValueChange = {}, readOnly = true,
                    label = { Text("Department *", fontSize = 14.sp) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = deptExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00796B), unfocusedBorderColor = Color(0xFF21262D),
                        focusedContainerColor = Color.White, unfocusedContainerColor = Color(0xFF161B22)
                    )
                )
                ExposedDropdownMenu(expanded = deptExpanded, onDismissRequest = { deptExpanded = false }) {
                    deptList.forEach { dept ->
                        DropdownMenuItem(text = { Text(dept, fontSize = 14.sp) },
                            onClick = { department = dept; deptExpanded = false })
                    }
                }
            }

            SectionLabel("Contact Details")
            EMTTextField(value = email,       onValueChange = { email = it },       label = "Email Address")
            EMTTextField(value = contact,     onValueChange = { contact = it },     label = "Phone Number")
            EMTTextField(value = joiningDate, onValueChange = { joiningDate = it }, label = "Joining Date (DD/MM/YYYY)")

            if (showSuccess) {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)), shape = RoundedCornerShape(10.dp)) {
                    Text("✓ Saved to Firebase Firestore!", color = Color(0xFF2E7D32),
                        modifier = Modifier.padding(12.dp), fontSize = 13.sp)
                }
            }
            errorMsg?.let {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)), shape = RoundedCornerShape(10.dp)) {
                    Text("✗ $it", color = Color(0xFFC62828), modifier = Modifier.padding(12.dp), fontSize = 13.sp)
                }
            }

            // ✅ FIXED BUTTON — Now saves to Firebase
            Button(
                onClick = {
                    if (name.isBlank() || role.isBlank() || department.isBlank()) {
                        errorMsg = "Please fill all required (*) fields"
                        return@Button
                    }
                    errorMsg = null
                    val employee = Employee(
                        id = existingEmployee?.id ?: "",
                        name = name.trim(), role = role.trim(),
                        department = department.trim(), email = email.trim(),
                        contact = contact.trim(), joiningDate = joiningDate.trim()
                    )
                    if (isEditing) {
                        viewModel.updateEmployee(employee,
                            onSuccess = { showSuccess = true },
                            onError   = { errorMsg = it }
                        )
                    } else {
                        viewModel.addEmployee(employee,
                            onSuccess = {
                                showSuccess = true
                                name = ""; role = ""; department = ""; email = ""; contact = ""; joiningDate = ""
                            },
                            onError = { errorMsg = it }
                        )
                    }
                },
                enabled  = !isLoading,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape    = RoundedCornerShape(12.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B))
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                } else {
                    Text(
                        if (isEditing) "Update Employee" else "Save Employee",
                        fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold, fontSize = 16.sp
                    )
                }
            }
        }
    }
}