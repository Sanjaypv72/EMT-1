package com.emt.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emt.app.ui.theme.InterFamily
import com.emt.app.ui.theme.PoppinsFamily

// ── Reusable TextField ──────────────────────────────
@Composable
fun EMTTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontFamily = InterFamily, fontSize = 14.sp) },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        singleLine = singleLine,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = Color(0xFF00796B),
            unfocusedBorderColor = Color(0xFFE0E0E0),
            focusedContainerColor   = Color.White,
            unfocusedContainerColor = Color.White
        )
    )
}

// ── Section Label ───────────────────────────────────
@Composable
fun SectionLabel(text: String) {
    Text(
        text = text,
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp,
        color = Color(0xFF00796B),
        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
    )
}

// ── Status Chip ─────────────────────────────────────
@Composable
fun StatusChip(status: String) {
    val (bg, fg) = when (status) {
        "Completed" -> Color(0xFFFFF8E1) to Color(0xFF2E7D32)
        "In Progress" -> Color(0xFFFFF8E1) to Color(0xFFF57F17)
        "Reviewed"  -> Color(0xFFE0F2F1) to Color(0xFF00796B)
        else        -> Color(0xFFFFEBEE) to Color(0xFFC62828) // Pending
    }
    Box(
        modifier = Modifier
            .background(bg, RoundedCornerShape(6.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = status,
            fontFamily = InterFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            color = fg
        )
    }
}

// ── Priority Chip ────────────────────────────────────
@Composable
fun PriorityChip(priority: String) {
    val (bg, fg) = when (priority) {
        "High"   -> Color(0xFFFFEBEE) to Color(0xFFC62828)
        "Medium" -> Color(0xFFFFF8E1) to Color(0xFFF57F17)
        else     -> Color(0xFFFFF8E1) to Color(0xFF2E7D32) // Low
    }
    Box(
        modifier = Modifier
            .background(bg, RoundedCornerShape(6.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = priority,
            fontFamily = InterFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            color = fg
        )
    }
}

// ── Department Badge ─────────────────────────────────
@Composable
fun DeptBadge(dept: String) {
    Box(
        modifier = Modifier
            .background(Color(0xFFE0F2F1), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = dept,
            fontFamily = InterFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            color = Color(0xFF00796B)
        )
    }
}