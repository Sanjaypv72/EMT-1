package com.emt.app.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emt.app.ui.theme.InterFamily
import com.emt.app.ui.theme.PoppinsFamily

data class NotifSetting(val title: String, val subtitle: String, val icon: ImageVector)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(onBack: () -> Unit) {
    val settings = listOf(
        NotifSetting("Task Assigned",      "Notify when a new task is assigned",  Icons.AutoMirrored.Filled.Assignment),
        NotifSetting("Task Due Soon",      "Alert 1 day before deadline",         Icons.Default.Alarm),
        NotifSetting("Task Overdue",       "Notify when deadline is missed",      Icons.Default.Warning),
        NotifSetting("Performance Review", "Alert when review is submitted",      Icons.Default.Star),
        NotifSetting("New Team Member",    "Notify when someone joins the team",  Icons.Default.PersonAdd),
        NotifSetting("Daily Summary",      "Daily team digest at 9:00 AM",       Icons.Default.Summarize),
    )
    val toggleStates = remember { mutableStateListOf(true, true, false, true, false, true) }
    val allOn = toggleStates.all { it }

    Scaffold(
        containerColor = Color(0xFF0D1117),
        topBar = {
            Box(modifier = Modifier.fillMaxWidth().background(Color(0xFF161B22)).border(BorderStroke(1.dp, Color.White.copy(0.05f)), RoundedCornerShape(0.dp)).padding(horizontal = 8.dp, vertical = 12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White) }
                    Text("Notifications", fontSize = 20.sp, fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Master toggle
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                        .background(if (allOn) Color(0xFF00796B) else Color(0xFF161B22))
                        .border(1.dp, if (allOn) Color.Transparent else Color.White.copy(0.06f), RoundedCornerShape(16.dp))
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.NotificationsActive, null, tint = if (allOn) Color.White else Color(0xFF4DB6AC), modifier = Modifier.size(22.dp))
                        Column {
                            Text("All Notifications", fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                            Text("Enable or disable all alerts", fontFamily = InterFamily, fontSize = 12.sp, color = Color.White.copy(if (allOn) 0.7f else 0.4f))
                        }
                    }
                    Switch(
                        checked = allOn,
                        onCheckedChange = { en -> toggleStates.indices.forEach { toggleStates[it] = en } },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00796B), checkedTrackColor = Color.White, uncheckedTrackColor = Color(0xFF21262D))
                    )
                }
            }

            item { Text("Individual Alerts", fontSize = 12.sp, fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold, color = Color(0xFF4B5563), letterSpacing = 1.sp, modifier = Modifier.padding(top = 4.dp)) }

            items(settings.size) { i ->
                val s = settings[i]
                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF161B22)).border(1.dp, Color.White.copy(0.05f), RoundedCornerShape(14.dp))
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(11.dp)).background(if (toggleStates[i]) Color(0xFF00796B).copy(0.2f) else Color(0xFF21262D)), contentAlignment = Alignment.Center) {
                        Icon(s.icon, null, tint = if (toggleStates[i]) Color(0xFF4DB6AC) else Color(0xFF4B5563), modifier = Modifier.size(20.dp))
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(s.title, fontFamily = PoppinsFamily, fontWeight = FontWeight.Medium, fontSize = 14.sp, color = if (toggleStates[i]) Color.White else Color(0xFF6B7280))
                        Text(s.subtitle, fontFamily = InterFamily, fontSize = 12.sp, color = Color(0xFF4B5563))
                    }
                    Switch(
                        checked = toggleStates[i], onCheckedChange = { toggleStates[i] = it },
                        colors = SwitchDefaults.colors(checkedTrackColor = Color(0xFF00796B), uncheckedTrackColor = Color(0xFF21262D))
                    )
                }
            }
        }
    }
}
