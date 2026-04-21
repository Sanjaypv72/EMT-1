package com.emt.app.ui.theme.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emt.app.ui.theme.InterFamily
import com.emt.app.ui.theme.PoppinsFamily
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

// ── Dummy Monthly Data for Line Chart ───────────────
data class MonthlyRating(val month: String, val avgRating: Float)

val monthlyTrend = listOf(
    MonthlyRating("Oct", 3.2f),
    MonthlyRating("Nov", 3.5f),
    MonthlyRating("Dec", 3.1f),
    MonthlyRating("Jan", 3.8f),
    MonthlyRating("Feb", 3.6f),
    MonthlyRating("Mar", 4.1f),
)

// ── Chart Colors ─────────────────────────────────────
val chartColors = listOf(
    Color(0xFF00796B), Color(0xFFFFB300), Color(0xFFE65100),
    Color(0xFF7B1FA2), Color(0xFF00838F), Color(0xFFC62828),
    Color(0xFFFFB300), Color(0xFF37474F)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsDashboard(onSettingsClick: () -> Unit) {

    // ── Computed Data ────────────────────────────────
    val totalEmployees = dummyEmployees.size
    val completedTasks = dummyTasks.count {
        it.status == "Completed" || it.status == "Reviewed"
    }
    val avgRating = dummyPerformance.map { it.overall }.average()

    // Per-employee avg rating (for Bar Chart)
    val empRatings = dummyEmployees.map { emp ->
        val reviews = dummyPerformance.filter { it.employeeId == emp.id }
        val avg = if (reviews.isEmpty()) 0f else reviews.map { it.overall }.average().toFloat()
        emp to avg
    }

    // Per-dept avg rating (for Pie Chart)
    val deptRatings = dummyEmployees.groupBy { it.department }.map { (dept, emps) ->
        val empIds = emps.map { it.id }
        val reviews = dummyPerformance.filter { it.employeeId in empIds }
        val avg = if (reviews.isEmpty()) 0f else reviews.map { it.overall }.average().toFloat()
        dept to avg
    }

    // Top 3 performers
    val topPerformers = empRatings
        .sortedByDescending { it.second }
        .take(3)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Analytics & Insights",
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Color.White
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFFAFAFA)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── KPI Cards ────────────────────────────
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    KpiCard(
                        label = "Employees",
                        value = totalEmployees.toString(),
                        icon = Icons.Default.Group,
                        color = Color(0xFF00796B),
                        modifier = Modifier.weight(1f)
                    )
                    KpiCard(
                        label = "Tasks Done",
                        value = completedTasks.toString(),
                        icon = Icons.Default.CheckCircle,
                        color = Color(0xFFFFB300),
                        modifier = Modifier.weight(1f)
                    )
                    KpiCard(
                        label = "Avg Rating",
                        value = "${"%.1f".format(avgRating)}★",
                        icon = Icons.Default.Star,
                        color = Color(0xFFFFB300),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // ── BAR CHART — Avg Rating Per Employee ──
            item {
                ChartCard(title = "Average Rating per Employee", subtitle = "Based on latest reviews") {
                    EmployeeBarChart(empRatings = empRatings)
                }
            }

            // ── PIE CHART — Dept Performance ─────────
            item {
                ChartCard(title = "Department Performance", subtitle = "Average rating by department") {
                    Column {
                        DeptPieChart(deptRatings = deptRatings)
                        Spacer(modifier = Modifier.height(12.dp))
                        PieLegend(deptRatings = deptRatings)
                    }
                }
            }

            // ── LINE CHART — Monthly Trend ────────────
            item {
                ChartCard(title = "Monthly Improvement Curve", subtitle = "Team avg rating trend") {
                    MonthlyLineChart(data = monthlyTrend)
                }
            }

            // ── TOP 3 PERFORMERS ─────────────────────
            item {
                Text(
                    "🏆 Top 3 Performers",
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = Color(0xFF212121)
                )
            }

            itemsIndexed(topPerformers) { index, (emp, rating) ->
                TopPerformerCard(index = index, employee = emp, rating = rating)
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

// ════════════════════════════════════════════════════
// 📊 BAR CHART — Employee Ratings
// ════════════════════════════════════════════════════
@Composable
fun EmployeeBarChart(empRatings: List<Pair<EmployeeUI, Float>>) {
    val barColor   = Color(0xFF00796B)
    val labelColor = Color(0xFF616161).toArgb()
    val maxValue   = 5f
    val barWidth   = 36f

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        val canvasW   = size.width
        val canvasH   = size.height
        val topPad    = 20f
        val bottomPad = 50f
        val chartH    = canvasH - topPad - bottomPad
        val count     = empRatings.size
        val spacing   = canvasW / count

        empRatings.forEachIndexed { i, (emp, rating) ->
            val barH  = (rating / maxValue) * chartH
            val x     = spacing * i + (spacing - barWidth) / 2
            val y     = topPad + chartH - barH

            // Bar
            drawRoundRect(
                color        = barColor,
                topLeft      = Offset(x, y),
                size         = Size(barWidth, barH),
                cornerRadius = CornerRadius(6f, 6f)
            )

            // Rating label on top
            drawContext.canvas.nativeCanvas.drawText(
                "${"%.1f".format(rating)}",
                x + barWidth / 2,
                y - 6f,
                android.graphics.Paint().apply {
                    color     = barColor.toArgb()
                    textSize  = 24f
                    textAlign = android.graphics.Paint.Align.CENTER
                    isFakeBoldText = true
                }
            )

            // Name label below
            drawContext.canvas.nativeCanvas.drawText(
                emp.name.split(" ").first(),
                x + barWidth / 2,
                canvasH - 10f,
                android.graphics.Paint().apply {
                    color     = labelColor
                    textSize  = 22f
                    textAlign = android.graphics.Paint.Align.CENTER
                }
            )
        }

        // Baseline
        drawLine(
            color       = Color(0xFFE0E0E0),
            start       = Offset(0f, topPad + chartH),
            end         = Offset(canvasW, topPad + chartH),
            strokeWidth = 2f
        )
    }
}

// ════════════════════════════════════════════════════
// 🥧 PIE CHART — Department Performance
// ════════════════════════════════════════════════════
@Composable
fun DeptPieChart(deptRatings: List<Pair<String, Float>>) {
    val total = deptRatings.sumOf { it.second.toDouble() }.toFloat()

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        val diameter  = min(size.width, size.height) * 0.85f
        val topLeft   = Offset(
            (size.width  - diameter) / 2f,
            (size.height - diameter) / 2f
        )
        var startAngle = -90f

        deptRatings.forEachIndexed { i, (_, rating) ->
            val sweep = (rating / total) * 360f
            drawArc(
                color      = chartColors[i % chartColors.size],
                startAngle = startAngle,
                sweepAngle = sweep,
                useCenter  = true,
                topLeft    = topLeft,
                size       = Size(diameter, diameter)
            )
            // White separator
            drawArc(
                color      = Color.White,
                startAngle = startAngle,
                sweepAngle = sweep,
                useCenter  = true,
                topLeft    = topLeft,
                size       = Size(diameter, diameter),
                style      = Stroke(width = 3f)
            )
            startAngle += sweep
        }

        // Center donut hole
        drawCircle(
            color  = Color.White,
            radius = diameter * 0.28f,
            center = Offset(size.width / 2f, size.height / 2f)
        )
    }
}

@Composable
fun PieLegend(deptRatings: List<Pair<String, Float>>) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        deptRatings.chunked(2).forEachIndexed { rowIndex, pair ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                pair.forEachIndexed { colIndex, (dept, rating) ->
                    val colorIndex = rowIndex * 2 + colIndex
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(
                                    chartColors[colorIndex % chartColors.size],
                                    CircleShape
                                )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "$dept (${"%.1f".format(rating)})",
                            fontFamily = InterFamily,
                            fontSize = 12.sp,
                            color = Color(0xFF424242)
                        )
                    }
                }
            }
        }
    }
}

// ════════════════════════════════════════════════════
// 📈 LINE CHART — Monthly Improvement
// ════════════════════════════════════════════════════
@Composable
fun MonthlyLineChart(data: List<MonthlyRating>) {
    val lineColor  = Color(0xFFFFB300)
    val dotColor   = Color(0xFF2E7D32)
    val labelColor = Color(0xFF616161).toArgb()
    val gridColor  = Color(0xFFE0E0E0)
    val minVal     = 0f
    val maxVal     = 5f

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        val canvasW   = size.width
        val canvasH   = size.height
        val topPad    = 20f
        val bottomPad = 40f
        val leftPad   = 40f
        val chartH    = canvasH - topPad - bottomPad
        val chartW    = canvasW - leftPad
        val count     = data.size

        // Horizontal grid lines (0, 1, 2, 3, 4, 5)
        for (v in 0..5) {
            val y = topPad + chartH - (v / maxVal) * chartH
            drawLine(
                color       = gridColor,
                start       = Offset(leftPad, y),
                end         = Offset(canvasW, y),
                strokeWidth = 1f
            )
            drawContext.canvas.nativeCanvas.drawText(
                "$v",
                leftPad - 8f,
                y + 8f,
                android.graphics.Paint().apply {
                    color     = labelColor
                    textSize  = 22f
                    textAlign = android.graphics.Paint.Align.RIGHT
                }
            )
        }

        // Calculate points
        val points = data.mapIndexed { i, item ->
            val x = leftPad + (i.toFloat() / (count - 1)) * chartW
            val y = topPad + chartH - ((item.avgRating - minVal) / (maxVal - minVal)) * chartH
            Offset(x, y)
        }

        // Filled area under line
        val fillPath = Path().apply {
            moveTo(points.first().x, topPad + chartH)
            points.forEach { lineTo(it.x, it.y) }
            lineTo(points.last().x, topPad + chartH)
            close()
        }
        drawPath(
            path  = fillPath,
            color = lineColor.copy(alpha = 0.12f)
        )

        // Line
        val linePath = Path().apply {
            moveTo(points.first().x, points.first().y)
            for (i in 1 until points.size) {
                val cp1x = (points[i - 1].x + points[i].x) / 2f
                cubicTo(cp1x, points[i - 1].y, cp1x, points[i].y, points[i].x, points[i].y)
            }
        }
        drawPath(
            path        = linePath,
            color       = lineColor,
            style       = Stroke(width = 4f, cap = StrokeCap.Round)
        )

        // Dots + labels
        points.forEachIndexed { i, point ->
            // Outer dot
            drawCircle(color = Color.White, radius = 8f, center = point)
            drawCircle(color = dotColor,    radius = 5f, center = point)

            // Rating label above dot
            drawContext.canvas.nativeCanvas.drawText(
                "${"%.1f".format(data[i].avgRating)}",
                point.x,
                point.y - 14f,
                android.graphics.Paint().apply {
                    color          = dotColor.toArgb()
                    textSize       = 22f
                    textAlign      = android.graphics.Paint.Align.CENTER
                    isFakeBoldText = true
                }
            )

            // Month label below
            drawContext.canvas.nativeCanvas.drawText(
                data[i].month,
                point.x,
                canvasH - 8f,
                android.graphics.Paint().apply {
                    color     = labelColor
                    textSize  = 22f
                    textAlign = android.graphics.Paint.Align.CENTER
                }
            )
        }
    }
}

// ════════════════════════════════════════════════════
// 🏆 TOP 3 PERFORMER CARD
// ════════════════════════════════════════════════════
@Composable
fun TopPerformerCard(index: Int, employee: EmployeeUI, rating: Float) {
    val (rankBg, medal) = when (index) {
        0 -> Color(0xFFFFD700) to "🥇"
        1 -> Color(0xFFB0BEC5) to "🥈"
        else -> Color(0xFFBCAAA4) to "🥉"
    }

    Card(
        shape  = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Medal
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(rankBg.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(medal, fontSize = 22.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Avatar
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(employee.avatarColor).copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = employee.name.take(1).uppercase(),
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(employee.avatarColor)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    employee.name,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = Color(0xFF212121)
                )
                Text(
                    "${employee.department} • ${employee.role}",
                    fontFamily = InterFamily,
                    fontSize = 12.sp,
                    color = Color(0xFF9E9E9E)
                )
            }

            // Rating Badge
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "${"%.1f".format(rating)}",
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = Color(0xFF00796B)
                )
                Text(
                    "★★★★★".take(rating.toInt()),
                    fontSize = 12.sp,
                    color = Color(0xFFFFB300)
                )
            }
        }
    }
}

// ════════════════════════════════════════════════════
// 🔲 CHART CARD WRAPPER
// ════════════════════════════════════════════════════
@Composable
fun ChartCard(
    title: String,
    subtitle: String,
    content: @Composable () -> Unit
) {
    Card(
        shape  = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                title,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = Color(0xFF212121)
            )
            Text(
                subtitle,
                fontFamily = InterFamily,
                fontSize = 12.sp,
                color = Color(0xFF9E9E9E)
            )
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

// ════════════════════════════════════════════════════
// 📦 KPI CARD
// ════════════════════════════════════════════════════
@Composable
fun KpiCard(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier  = modifier,
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(color.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null,
                    tint = color, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                value,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = color
            )
            Text(
                label,
                fontFamily = InterFamily,
                fontSize = 11.sp,
                color = Color(0xFF9E9E9E)
            )
        }
    }
}
