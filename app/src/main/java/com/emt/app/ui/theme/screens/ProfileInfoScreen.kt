package com.emt.app.ui.theme.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Assignment
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
import com.emt.app.ui.theme.InterFamily
import com.emt.app.ui.theme.PoppinsFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileInfoScreen(onBack: () -> Unit) {
    val user = dummyEmployees.first()
    var name      by remember { mutableStateOf(user.name) }
    var email     by remember { mutableStateOf(user.email) }
    var contact   by remember { mutableStateOf(user.contact) }
    var role      by remember { mutableStateOf(user.role) }
    var showSaved by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color(0xFF0D1117),
        topBar = {
            Box(modifier = Modifier.fillMaxWidth().background(Color(0xFF161B22)).border(BorderStroke(1.dp, Color.White.copy(0.05f)), RoundedCornerShape(0.dp)).padding(horizontal = 8.dp, vertical = 12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White) }
                    Text("Profile", fontSize = 20.sp, fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // Avatar hero
            Box(modifier = Modifier.fillMaxWidth().background(Brush.verticalGradient(listOf(Color(0xFF00897B).copy(0.25f), Color.Transparent))).padding(28.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        modifier = Modifier.size(80.dp).clip(CircleShape)
                            .background(Brush.linearGradient(listOf(Color(0xFF00897B), Color(0xFF004D40)))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(user.name.take(1).uppercase(), fontSize = 36.sp, fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Text(user.name, fontSize = 18.sp, fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold, color = Color.White)
                    Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(Color(0xFF00796B).copy(0.2f)).padding(horizontal = 14.dp, vertical = 5.dp)) {
                        Text("Admin", fontSize = 12.sp, fontFamily = InterFamily, color = Color(0xFF4DB6AC))
                    }
                }
            }

            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                DarkSectionLabel("Personal Details")
                DarkProfileField(name, { name = it }, "Full Name", Icons.Default.Person)
                DarkProfileField(role, { role = it }, "Role / Title", Icons.Default.Work)

                DarkSectionLabel("Contact Info")
                DarkProfileField(email, { email = it }, "Email Address", Icons.Default.Email)
                DarkProfileField(contact, { contact = it }, "Phone Number", Icons.Default.Phone)

                DarkSectionLabel("Other Info")
                DarkReadOnlyField("Department", user.department, Icons.Default.Groups)
                DarkReadOnlyField("Joining Date", user.joiningDate, Icons.Default.CalendarToday)

                AnimatedVisibility(visible = showSaved, enter = fadeIn() + expandVertically(), exit = fadeOut() + shrinkVertically()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF10B981).copy(0.12f)).border(1.dp, Color(0xFF10B981).copy(0.3f), RoundedCornerShape(12.dp))
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF10B981), modifier = Modifier.size(18.dp))
                        Text("Profile updated successfully!", fontSize = 13.sp, fontFamily = InterFamily, color = Color(0xFF10B981))
                    }
                }

                Button(
                    onClick = { showSaved = true },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B))
                ) {
                    Icon(Icons.Default.Save, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Save Changes", fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = Color.White)
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun DarkSectionLabel(text: String) {
    Text(text, fontSize = 12.sp, fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold, color = Color(0xFF4B5563), letterSpacing = 1.sp, modifier = Modifier.padding(top = 4.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DarkProfileField(value: String, onValueChange: (String) -> Unit, label: String, icon: ImageVector) {
    OutlinedTextField(
        value = value, onValueChange = onValueChange,
        label = { Text(label, fontFamily = InterFamily, fontSize = 13.sp) },
        leadingIcon = { Icon(icon, null, tint = Color(0xFF4DB6AC), modifier = Modifier.size(18.dp)) },
        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF00796B), unfocusedBorderColor = Color(0xFF21262D),
            focusedContainerColor = Color(0xFF161B22), unfocusedContainerColor = Color(0xFF161B22),
            focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedLabelColor = Color(0xFF4DB6AC)
        )
    )
}

@Composable
fun DarkReadOnlyField(label: String, value: String, icon: ImageVector) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF161B22)).border(1.dp, Color.White.copy(0.05f), RoundedCornerShape(12.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(icon, null, tint = Color(0xFF374151), modifier = Modifier.size(18.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontSize = 11.sp, fontFamily = InterFamily, color = Color(0xFF4B5563))
            Text(value, fontSize = 14.sp, fontFamily = PoppinsFamily, fontWeight = FontWeight.Medium, color = Color(0xFF9CA3AF))
        }
        Icon(Icons.Default.Lock, null, tint = Color(0xFF374151), modifier = Modifier.size(14.dp))
    }
}

@Composable
fun ProfileTextField(value: String, onValueChange: (String) -> Unit, label: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    DarkProfileField(value, onValueChange, label, icon)
}

@Composable
fun ReadOnlyField(label: String, value: String) {
    DarkReadOnlyField(label, value, Icons.Default.Info)
}
