package com.emt.app.ui.theme.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emt.app.ui.components.SectionLabel
import com.emt.app.ui.theme.InterFamily
import com.emt.app.ui.theme.PoppinsFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(onBack: () -> Unit) {

    var currentPass  by remember { mutableStateOf("") }
    var newPass      by remember { mutableStateOf("") }
    var confirmPass  by remember { mutableStateOf("") }

    var showCurrent  by remember { mutableStateOf(false) }
    var showNew      by remember { mutableStateOf(false) }
    var showConfirm  by remember { mutableStateOf(false) }

    var errorMsg     by remember { mutableStateOf("") }
    var showSuccess  by remember { mutableStateOf(false) }

    // Password strength
    val strength = when {
        newPass.length >= 10 && newPass.any { it.isUpperCase() } &&
                newPass.any { it.isDigit() } -> "Strong"
        newPass.length >= 6          -> "Medium"
        newPass.isNotEmpty()         -> "Weak"
        else -> ""
    }
    val strengthColor = when (strength) {
        "Strong" -> Color(0xFFFFB300)
        "Medium" -> Color(0xFFF57F17)
        "Weak"   -> Color(0xFFF44336)
        else     -> Color.Transparent
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Change Password",
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back", tint = Color.White)
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
                .background(Color(0xFF0D1117))
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Info Card
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F2F1)),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Info, contentDescription = null,
                        tint = Color(0xFF00796B), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Use min 8 characters with uppercase & numbers for a strong password.",
                        fontFamily = InterFamily,
                        fontSize = 13.sp,
                        color = Color(0xFF00796B))
                }
            }

            SectionLabel("Current Password")

            PasswordField(
                value = currentPass,
                onValueChange = { currentPass = it; errorMsg = "" },
                label = "Current Password",
                visible = showCurrent,
                onToggle = { showCurrent = !showCurrent }
            )

            SectionLabel("New Password")

            PasswordField(
                value = newPass,
                onValueChange = { newPass = it; errorMsg = "" },
                label = "New Password",
                visible = showNew,
                onToggle = { showNew = !showNew }
            )

            // Strength Indicator
            if (strength.isNotEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(3) { i ->
                        val filled = when (strength) {
                            "Strong" -> true
                            "Medium" -> i < 2
                            else     -> i < 1
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(5.dp)
                                .background(
                                    if (filled) strengthColor else Color(0xFFE0E0E0),
                                    RoundedCornerShape(4.dp)
                                )
                        )
                    }
                    Text(strength,
                        fontFamily = InterFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        color = strengthColor)
                }
            }

            PasswordField(
                value = confirmPass,
                onValueChange = { confirmPass = it; errorMsg = "" },
                label = "Confirm New Password",
                visible = showConfirm,
                onToggle = { showConfirm = !showConfirm }
            )

            // Password match indicator
            if (confirmPass.isNotEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        if (newPass == confirmPass) Icons.Default.CheckCircle
                        else Icons.Default.Cancel,
                        contentDescription = null,
                        tint = if (newPass == confirmPass) Color(0xFFFFB300)
                        else Color(0xFFF44336),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        if (newPass == confirmPass) "Passwords match"
                        else "Passwords do not match",
                        fontFamily = InterFamily,
                        fontSize = 12.sp,
                        color = if (newPass == confirmPass) Color(0xFFFFB300)
                        else Color(0xFFF44336)
                    )
                }
            }

            if (errorMsg.isNotEmpty()) {
                Text(errorMsg,
                    color = Color(0xFFF44336),
                    fontFamily = InterFamily,
                    fontSize = 13.sp)
            }

            if (showSuccess) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("✓ Password changed successfully! (UI only)",
                        color = Color(0xFF2E7D32),
                        modifier = Modifier.padding(12.dp),
                        fontFamily = InterFamily,
                        fontSize = 13.sp)
                }
            }

            Button(
                onClick = {
                    when {
                        currentPass.isBlank() -> errorMsg = "Enter current password"
                        newPass.length < 6    -> errorMsg = "New password min 6 characters"
                        newPass != confirmPass -> errorMsg = "Passwords do not match"
                        else -> { showSuccess = true; errorMsg = "" }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B))
            ) {
                Text("Update Password",
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    visible: Boolean,
    onToggle: () -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontFamily = InterFamily, fontSize = 14.sp) },
        leadingIcon = {
            Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF00796B))
        },
        trailingIcon = {
            IconButton(onClick = onToggle) {
                Icon(
                    if (visible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = "Toggle"
                )
            }
        },
        visualTransformation = if (visible) VisualTransformation.None
        else PasswordVisualTransformation(),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF00796B),
            unfocusedBorderColor = Color(0xFF21262D),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color(0xFF161B22)
        )
    )
}
