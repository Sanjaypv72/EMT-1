package com.emt.app.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
fun SettingsScreen(
    isAdmin: Boolean = true,
    onLogout: () -> Unit,
    onBack: () -> Unit,
    onProfileClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onChangePasswordClick: () -> Unit,
    onAboutClick: () -> Unit
) {
    val user = if (isAdmin) dummyEmployees[0].copy(name = "Admin User", role = "Administrator") else dummyEmployees[0]

    Scaffold(
        containerColor = Color(0xFF0D1117),
        topBar = {
            Box(modifier = Modifier.fillMaxWidth().background(Color(0xFF161B22)).border(BorderStroke(1.dp, Color.White.copy(0.05f)), RoundedCornerShape(0.dp)).padding(horizontal = 16.dp, vertical = 12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White) }
                    Spacer(Modifier.width(4.dp))
                    Text("Settings", fontSize = 22.sp, fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // Profile hero card
            Box(
                modifier = Modifier.fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(Color(0xFF00897B).copy(0.3f), Color.Transparent)))
                    .padding(24.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Box(
                        modifier = Modifier.size(64.dp).clip(CircleShape)
                            .background(Brush.linearGradient(listOf(Color(0xFF00897B), Color(0xFF004D40)))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(user.name.take(1), fontSize = 26.sp, fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Column {
                        Text(user.name, fontSize = 18.sp, fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(user.role, fontSize = 13.sp, fontFamily = InterFamily, color = Color(0xFF9CA3AF))
                        Spacer(Modifier.height(4.dp))
                        Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(if (isAdmin) Color(0xFF00796B).copy(0.2f) else Color(0xFF5C35CC).copy(0.2f)).padding(horizontal = 10.dp, vertical = 3.dp)) {
                            Text(if (isAdmin) "Admin" else "Staff", fontSize = 11.sp, fontFamily = InterFamily, color = if (isAdmin) Color(0xFF4DB6AC) else Color(0xFF9B7EF8))
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Account section
            SettingsSectionHeader("Account")
            SettingsItem(Icons.Default.Person, "Profile Info", "Update your personal details", Color(0xFF00796B)) { onProfileClick() }
            SettingsItem(Icons.Default.Lock, "Change Password", "Update your login credentials", Color(0xFF00796B)) { onChangePasswordClick() }
            SettingsItem(Icons.Default.Notifications, "Notifications", "Manage alerts & reminders", Color(0xFF00796B)) { onNotificationsClick() }

            Spacer(Modifier.height(8.dp))

            // App section
            SettingsSectionHeader("Application")
            SettingsItem(Icons.Default.Info, "About EMT", "Version, licenses & team", Color(0xFF6366F1)) { onAboutClick() }
            SettingsItem(Icons.Default.Policy, "Privacy Policy", "How we protect your data", Color(0xFF6366F1)) {}
            SettingsItem(Icons.Default.HelpOutline, "Help & Support", "FAQs and contact us", Color(0xFF6366F1)) {}

            Spacer(Modifier.height(16.dp))

            // Logout
            Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                Button(
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336).copy(0.12f)),
                    border = BorderStroke(1.dp, Color(0xFFF44336).copy(0.3f))
                ) {
                    Icon(Icons.Default.Logout, null, tint = Color(0xFFEF9A9A), modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Sign Out", fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = Color(0xFFEF9A9A))
                }
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(title, fontSize = 12.sp, fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold,
        color = Color(0xFF4B5563), modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp), letterSpacing = 1.sp)
}

@Composable
fun SettingsItem(icon: ImageVector, title: String, subtitle: String, accent: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(42.dp).clip(RoundedCornerShape(12.dp)).background(accent.copy(0.12f)), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = accent, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f).padding(vertical = 14.dp)) {
            Text(title, fontSize = 15.sp, fontFamily = PoppinsFamily, fontWeight = FontWeight.Medium, color = Color.White)
            Text(subtitle, fontSize = 12.sp, fontFamily = InterFamily, color = Color(0xFF6B7280))
        }
        Icon(Icons.Default.ChevronRight, null, tint = Color(0xFF374151), modifier = Modifier.size(18.dp))
    }
    HorizontalDivider(modifier = Modifier.padding(start = 72.dp), color = Color.White.copy(0.04f), thickness = 1.dp)
}
