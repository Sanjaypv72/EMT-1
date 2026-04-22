package com.emt.app.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.emt.app.model.Employee
import com.emt.app.navigation.Routes
import com.emt.app.ui.theme.InterFamily
import com.emt.app.ui.theme.PoppinsFamily
import com.emt.app.viewmodel.EmployeeViewModel

// Avatar color palette
private val avatarColors = listOf(
    0xFF00796BL, 0xFF43A047L, 0xFF7B1FA2L,
    0xFFE65100L, 0xFF00838FL, 0xFFC62828L
)

fun Employee.avatarColorLong(): Long =
    avatarColors[id.hashCode().and(0x7FFFFFFF) % avatarColors.size]

private val departmentList = listOf(
    "All", "Engineering", "Design", "HR", "Marketing", "Sales", "Finance", "Operations"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeListScreen(
    navController: NavHostController,
    employeeVM: EmployeeViewModel, // We only need this one
    onAddEmployee: () -> Unit,
    onEmployeeClick: (Employee) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedDept by remember { mutableStateOf("All") }

    // Collect the employee list from the VM
    val allEmployees by employeeVM.employees.collectAsState()

    // We'll consider it "loading" if the list is empty (until DB returns data)
    val isLoading = allEmployees.isEmpty()

    val filtered = allEmployees.filter { emp ->
        val matchSearch = emp.name.contains(searchQuery, ignoreCase = true) ||
                emp.role.contains(searchQuery, ignoreCase = true)
        val matchDept = selectedDept == "All" || emp.department == selectedDept
        matchSearch && matchDept
    }

    Scaffold(
        containerColor = Color(0xFF0D1117),
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(Color(0xFF00897B), Color(0xFF004D40))))
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Team", fontSize = 26.sp,
                            fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold, color = Color.White
                        )
                        Text(
                            "${filtered.size} of ${allEmployees.size} members",
                            fontSize = 12.sp, fontFamily = InterFamily, color = Color.White.copy(0.65f)
                        )
                    }
                    IconButton(onClick = { navController.navigate(Routes.ANALYTICS) }) {
                        Icon(Icons.Default.BarChart, "Analytics", tint = Color.White)
                    }
                    IconButton(onClick = { navController.navigate(Routes.ATTENDANCE) }) {
                        Icon(Icons.Default.HowToReg, "Attendance", tint = Color.White)
                    }
                    IconButton(onClick = { navController.navigate(Routes.TASK_ASSIGNMENT) }) {
                        Icon(Icons.AutoMirrored.Filled.Assignment, "Tasks", tint = Color.White)
                    }
                    IconButton(onClick = { navController.navigate(Routes.SETTINGS) }) {
                        Icon(Icons.Default.Settings, "Settings", tint = Color.White)
                    }
                }
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddEmployee,
                containerColor = Color(0xFF00796B),
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                icon = { Icon(Icons.Default.PersonAdd, null) },
                text = {
                    Text("Add Member", fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold)
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {

            // Search bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0D1117))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text("Search name or role...", fontFamily = InterFamily, fontSize = 14.sp, color = Color(0xFF4B5563))
                    },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = Color(0xFF4B5563)) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty())
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, null, tint = Color(0xFF4B5563))
                            }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00796B),
                        unfocusedBorderColor = Color(0xFF21262D),
                        focusedContainerColor = Color(0xFF161B22),
                        unfocusedContainerColor = Color(0xFF161B22),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
            }

            // Department filter chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(departmentList) { dept ->
                    val sel = selectedDept == dept
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (sel) Color(0xFF00796B) else Color(0xFF161B22))
                            .border(1.dp, if (sel) Color.Transparent else Color(0xFF21262D), RoundedCornerShape(20.dp))
                            .clickable { selectedDept = dept }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            dept, fontSize = 13.sp, fontFamily = InterFamily,
                            fontWeight = if (sel) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (sel) Color.White else Color(0xFF9CA3AF)
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF00796B))
                }
            } else if (filtered.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.SearchOff, null, modifier = Modifier.size(56.dp), tint = Color(0xFF374151))
                        Text("No members found", fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Color(0xFF4B5563))
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filtered, key = { it.id }) { emp ->
                        MemberCard(employee = emp, onClick = { onEmployeeClick(emp) })
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
fun MemberCard(employee: Employee, onClick: () -> Unit) {
    val avatarColor = Color(employee.avatarColorLong())

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF161B22))
            .border(1.dp, Color.White.copy(0.05f), RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(avatarColor, avatarColor.copy(0.5f))))
            )
            Text(
                employee.name.take(1).uppercase(),
                fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold,
                fontSize = 20.sp, color = Color.White
            )
        }

        Spacer(Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                employee.name,
                fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp, color = Color.White,
                maxLines = 1, overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(2.dp))
            Text(
                employee.role,
                fontFamily = InterFamily, fontSize = 12.sp, color = Color(0xFF9CA3AF),
                maxLines = 1, overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(avatarColor.copy(0.15f))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    employee.department,
                    fontSize = 11.sp, fontFamily = InterFamily,
                    fontWeight = FontWeight.Medium, color = avatarColor
                )
            }
        }

        Icon(Icons.Default.ChevronRight, null, tint = Color(0xFF374151))
    }
}