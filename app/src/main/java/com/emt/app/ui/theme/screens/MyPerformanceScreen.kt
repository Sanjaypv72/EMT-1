package com.emt.app.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// --- CRITICAL IMPORTS ---
import com.emt.app.model.Performance
import com.emt.app.ui.theme.InterFamily
import com.emt.app.ui.theme.PoppinsFamily
import com.emt.app.viewmodel.PerformanceViewModel
import com.emt.app.viewmodel.EmployeeViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPerformanceScreen(
    performanceVM: PerformanceViewModel,
    employeeVM: EmployeeViewModel,
    onBack: () -> Unit
) {
    // Collect state from ViewModels
    val employees by employeeVM.employees.collectAsState(initial = emptyList())
    val allPerformance by performanceVM.allPerformance.collectAsState(initial = emptyList())

    // Logic to find current employee and their history
    val loggedInEmployee = employees.firstOrNull()
    val perfHistory = allPerformance.filter { it.employeeId == loggedInEmployee?.id }

    Scaffold(
        containerColor = Color(0xFF0D1117),
        topBar = {
            Box(
                modifier = Modifier.fillMaxWidth()
                    .background(Brush.horizontalGradient(listOf(Color(0xFFB45309), Color(0xFFD97706))))
                    .padding(horizontal = 8.dp, vertical = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                    }
                    Column {
                        Text("My Performance", fontSize = 20.sp, fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(text = loggedInEmployee?.name ?: "Employee", fontSize = 12.sp, fontFamily = InterFamily, color = Color.White.copy(0.7f))
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // Latest Rating Card
            item {
                perfHistory.firstOrNull()?.let { latest ->
                    Box(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp))
                            .background(Brush.linearGradient(listOf(Color(0xFF92400E), Color(0xFFB45309), Color(0xFFD97706))))
                            .padding(24.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Text("Latest Rating", fontSize = 13.sp, fontFamily = InterFamily, color = Color.White.copy(0.7f))
                            Spacer(Modifier.height(4.dp))
                            // Used overallRating from your model
                            Text("${"%.1f".format(latest.overallRating)}", fontSize = 52.sp, fontFamily = PoppinsFamily, fontWeight = FontWeight.ExtraBold, color = Color.White)
                            Text("out of 5.0", fontSize = 13.sp, fontFamily = InterFamily, color = Color.White.copy(0.6f))
                            Spacer(Modifier.height(10.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                repeat(5) { i ->
                                    val isFilled = i < latest.overallRating.toInt()
                                    Icon(
                                        imageVector = if (isFilled) Icons.Default.Star else Icons.Default.StarBorder,
                                        contentDescription = null,
                                        tint = Color(0xFFFDE68A),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(Color.White.copy(0.15f)).padding(horizontal = 14.dp, vertical = 5.dp)) {
                                Text(latest.month, fontSize = 12.sp, fontFamily = InterFamily, color = Color.White.copy(0.9f))
                            }
                        }
                    }
                }
            }

            item {
                Text("Review History", fontSize = 12.sp, fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold, color = Color(0xFF9CA3AF), letterSpacing = 1.sp)
            }

            items(perfHistory) { perf ->
                Column(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF161B22)).border(1.dp, Color.White.copy(0.06f), RoundedCornerShape(16.dp))
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(perf.month, fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = Color.White)
                        Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(Color(0xFFD97706).copy(0.15f)).padding(horizontal = 12.dp, vertical = 5.dp)) {
                            // Used overallRating from your model
                            Text("★ ${"%.1f".format(perf.overallRating)}", fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFFFBBF24))
                        }
                    }

                    // Updated to use your specific model field names
                    DarkMetricBar("Quality",       perf.qualityScore.toInt())
                    DarkMetricBar("Timeliness",    perf.timelinessScore.toInt())
                    DarkMetricBar("Attendance",    perf.attendanceScore.toInt())
                    DarkMetricBar("Communication", perf.communicationScore.toInt())
                    DarkMetricBar("Innovation",    perf.innovationScore.toInt())
                }
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun DarkMetricBar(label: String, score: Int) {
    val fraction = score / 5f
    val barColor = when {
        fraction >= 0.8f -> Color(0xFF10B981)
        fraction >= 0.6f -> Color(0xFFF59E0B)
        else -> Color(0xFFF43F5E)
    }
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, fontSize = 12.sp, fontFamily = InterFamily, color = Color(0xFF9CA3AF))
            Text("$score / 5", fontSize = 12.sp, fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold, color = barColor)
        }
        Box(modifier = Modifier.fillMaxWidth().height(5.dp).clip(RoundedCornerShape(50)).background(Color(0xFF21262D))) {
            Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(fraction).clip(RoundedCornerShape(50)).background(barColor))
        }
    }
}