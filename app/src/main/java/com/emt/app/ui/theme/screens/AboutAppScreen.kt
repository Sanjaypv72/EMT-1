package com.emt.app.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emt.app.ui.theme.InterFamily
import com.emt.app.ui.theme.PoppinsFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutAppScreen(onBack: () -> Unit) {
    Scaffold(
        containerColor = Color(0xFF0D1117),
        topBar = {
            Box(modifier = Modifier.fillMaxWidth().background(Color(0xFF161B22)).border(BorderStroke(1.dp, Color.White.copy(0.05f)), RoundedCornerShape(0.dp)).padding(horizontal = 8.dp, vertical = 12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White) }
                    Text("About EMT", fontSize = 20.sp, fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Hero section
            Box(
                modifier = Modifier.fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(Color(0xFF00897B).copy(0.2f), Color.Transparent)))
                    .padding(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Box(
                        modifier = Modifier.size(88.dp).clip(RoundedCornerShape(26.dp))
                            .background(Brush.linearGradient(listOf(Color(0xFF00897B), Color(0xFF004D40)))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("EMT", fontSize = 24.sp, fontFamily = PoppinsFamily, fontWeight = FontWeight.ExtraBold, color = Color.White, letterSpacing = 1.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("EMT", fontSize = 28.sp, fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Employee Management Tool", fontSize = 13.sp, fontFamily = InterFamily, color = Color(0xFF4DB6AC))
                    }
                    Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(Color(0xFF00796B).copy(0.15f)).border(1.dp, Color(0xFF00796B).copy(0.3f), RoundedCornerShape(8.dp)).padding(horizontal = 14.dp, vertical = 6.dp)) {
                        Text("Version 1.0.0", fontSize = 12.sp, fontFamily = InterFamily, color = Color(0xFF4DB6AC))
                    }
                }
            }

            // Feature cards
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Features", fontSize = 13.sp, fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold, color = Color(0xFF4B5563), letterSpacing = 1.sp)
                Spacer(Modifier.height(2.dp))

                val features = listOf(
                    Triple(Icons.Default.Group, "Team Management", "Add, edit and manage your entire workforce"),
                    Triple(Icons.AutoMirrored.Filled.Assignment, "Task Tracking", "Assign tasks with priorities and deadlines"),
                    Triple(Icons.Default.BarChart, "Analytics", "Real-time dashboards and performance charts"),
                    Triple(Icons.Default.Star, "Performance Reviews", "Monthly evaluations with detailed scoring"),
                    Triple(Icons.Default.Notifications, "Smart Alerts", "Customisable notifications for key events"),
                    Triple(Icons.Default.Lock, "Secure Access", "Role-based login for admins and staff"),
                )

                features.forEach { (icon, title, desc) ->
                    Row(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF161B22)).border(1.dp, Color.White.copy(0.05f), RoundedCornerShape(14.dp))
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(11.dp)).background(Color(0xFF00796B).copy(0.15f)), contentAlignment = Alignment.Center) {
                            Icon(icon, null, tint = Color(0xFF4DB6AC), modifier = Modifier.size(20.dp))
                        }
                        Column {
                            Text(title, fontSize = 14.sp, fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold, color = Color.White)
                            Text(desc, fontSize = 12.sp, fontFamily = InterFamily, color = Color(0xFF6B7280))
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Built with section
                Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Color(0xFF161B22)).border(1.dp, Color.White.copy(0.05f), RoundedCornerShape(16.dp)).padding(20.dp)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Built With", fontSize = 13.sp, fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold, color = Color(0xFF4B5563), letterSpacing = 1.sp)
                        Spacer(Modifier.height(4.dp))
                        listOf("Kotlin", "Jetpack Compose", "Firebase", "Material 3", "Navigation Component").forEach { tech ->
                            Text("• $tech", fontSize = 13.sp, fontFamily = InterFamily, color = Color(0xFF9CA3AF), textAlign = TextAlign.Center)
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))
                Text("Made with ♥ for teams", fontSize = 12.sp, fontFamily = InterFamily, color = Color(0xFF374151), textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(32.dp))
            }
        }
    }
}
