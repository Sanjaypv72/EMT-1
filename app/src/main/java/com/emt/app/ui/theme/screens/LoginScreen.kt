package com.emt.app.ui.theme.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emt.app.ui.theme.InterFamily
import com.emt.app.ui.theme.PoppinsFamily

@Composable
fun LoginScreen(onLoginAsAdmin: () -> Unit, onLoginAsEmployee: () -> Unit) {
    var email           by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isAdmin         by remember { mutableStateOf(true) }
    var showError       by remember { mutableStateOf(false) }
    var visible         by remember { mutableStateOf(false) }

    val slideUp by animateFloatAsState(targetValue = if (visible) 0f else 60f, animationSpec = tween(650, easing = EaseOutCubic), label = "su")
    val fadeIn  by animateFloatAsState(targetValue = if (visible) 1f else 0f,  animationSpec = tween(500), label = "fi")

    LaunchedEffect(Unit) { visible = true }

    val primary   = if (isAdmin) Color(0xFF00796B) else Color(0xFF5C35CC)
    val secondary = if (isAdmin) Color(0xFF004D40) else Color(0xFF3A1F99)

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0D1117))) {

        // Top brand band
        Box(
            modifier = Modifier.fillMaxWidth().height(260.dp)
                .background(Brush.verticalGradient(listOf(Color(0xFF00897B), Color(0xFF004D40), Color(0xFF0D1117))))
        )

        // Decorative rotated square top-right
        Box(modifier = Modifier.size(120.dp).align(Alignment.TopEnd).offset(40.dp, (-40).dp)
            .clip(RoundedCornerShape(24.dp)).background(Color.White.copy(0.04f)))

        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(52.dp))

            // Logo + brand
            Column(
                modifier = Modifier.alpha(fadeIn),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.size(72.dp).clip(RoundedCornerShape(22.dp))
                        .background(Color.White.copy(0.12f))
                        .border(1.dp, Color.White.copy(0.25f), RoundedCornerShape(22.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("EMT", fontSize = 20.sp, fontFamily = PoppinsFamily, fontWeight = FontWeight.ExtraBold, color = Color.White)
                }
                Spacer(Modifier.height(14.dp))
                Text("Welcome Back", fontSize = 28.sp, fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(Modifier.height(4.dp))
                Text("Sign in to continue to your workspace", fontSize = 13.sp, fontFamily = InterFamily, color = Color.White.copy(0.65f))
            }

            Spacer(Modifier.height(32.dp))

            // Main card
            Box(
                modifier = Modifier
                    .fillMaxWidth().padding(horizontal = 20.dp)
                    .offset(y = slideUp.dp).alpha(fadeIn)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color(0xFF161B22))
                    .border(1.dp, Color.White.copy(0.06f), RoundedCornerShape(28.dp))
            ) {
                Column(modifier = Modifier.padding(26.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {

                    // Role toggle
                    Row(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF0D1117)).padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf(true to "Admin", false to "Staff").forEach { (adminMode, label) ->
                            val selected = isAdmin == adminMode
                            val tabColor by animateColorAsState(
                                if (selected) if (adminMode) Color(0xFF00796B) else Color(0xFF5C35CC) else Color.Transparent,
                                tween(250), label = "tc"
                            )
                            Box(
                                modifier = Modifier.weight(1f).clip(RoundedCornerShape(11.dp))
                                    .background(tabColor)
                                    .clickable { isAdmin = adminMode; showError = false }
                                    .padding(vertical = 11.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        if (adminMode) Icons.Default.AdminPanelSettings else Icons.Default.Badge,
                                        contentDescription = null, modifier = Modifier.size(15.dp),
                                        tint = if (selected) Color.White else Color(0xFF6B7280)
                                    )
                                    Text(label, fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold, fontSize = 14.sp,
                                        color = if (selected) Color.White else Color(0xFF6B7280))
                                }
                            }
                        }
                    }

                    // Role hint pill
                    AnimatedContent(targetState = isAdmin, transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(150)) }, label = "rh") { admin ->
                        Row(
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                                .background(if (admin) Color(0xFF00796B).copy(0.12f) else Color(0xFF5C35CC).copy(0.12f))
                                .padding(horizontal = 14.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(if (admin) Icons.Default.Shield else Icons.Default.Person, contentDescription = null,
                                modifier = Modifier.size(14.dp), tint = if (admin) Color(0xFF4DB6AC) else Color(0xFF9B7EF8))
                            Text(
                                if (admin) "Full access — manage team, tasks & analytics" else "View your tasks, performance & profile",
                                fontSize = 12.sp, fontFamily = InterFamily,
                                color = if (admin) Color(0xFF4DB6AC) else Color(0xFF9B7EF8)
                            )
                        }
                    }

                    // Email field
                    DarkTextField(
                        value = email, onValueChange = { email = it; showError = false },
                        label = "Email Address", icon = Icons.Default.Email, accent = primary
                    )

                    // Password field
                    DarkTextField(
                        value = password, onValueChange = { password = it; showError = false },
                        label = "Password", icon = Icons.Default.Lock, accent = primary,
                        isPassword = true, passwordVisible = passwordVisible,
                        onTogglePassword = { passwordVisible = !passwordVisible }
                    )

                    // Error
                    AnimatedVisibility(visible = showError, enter = fadeIn() + expandVertically(), exit = fadeOut() + shrinkVertically()) {
                        Row(
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFF44336).copy(0.1f)).padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.ErrorOutline, null, modifier = Modifier.size(15.dp), tint = Color(0xFFEF9A9A))
                            Text("Please fill in both fields", fontSize = 12.sp, fontFamily = InterFamily, color = Color(0xFFEF9A9A))
                        }
                    }

                    // Sign in button
                    Button(
                        onClick = {
                            if (email.isBlank() || password.isBlank()) { showError = true; return@Button }
                            if (isAdmin) onLoginAsAdmin() else onLoginAsEmployee()
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primary),
                        elevation = ButtonDefaults.buttonElevation(6.dp)
                    ) {
                        Text("Sign in as ${if (isAdmin) "Admin" else "Staff"}", fontSize = 15.sp,
                            fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold, color = Color.White)
                    }

                    // Demo hint
                    Text("Demo: any email + password works", fontSize = 11.sp, fontFamily = InterFamily,
                        color = Color(0xFF4B5563), textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                }
            }

            Spacer(Modifier.height(28.dp))
            Text("Secure  •  Private  •  Reliable", fontSize = 11.sp, fontFamily = InterFamily,
                color = Color.White.copy(0.2f), letterSpacing = 1.sp)
            Spacer(Modifier.height(28.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DarkTextField(
    value: String, onValueChange: (String) -> Unit, label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector, accent: Color,
    isPassword: Boolean = false, passwordVisible: Boolean = false, onTogglePassword: (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value, onValueChange = onValueChange,
        label = { Text(label, fontFamily = InterFamily, fontSize = 13.sp, color = Color(0xFF6B7280)) },
        leadingIcon = { Icon(icon, null, tint = if (value.isNotEmpty()) accent else Color(0xFF4B5563), modifier = Modifier.size(18.dp)) },
        trailingIcon = if (isPassword) ({
            IconButton(onClick = { onTogglePassword?.invoke() }) {
                Icon(if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, null, tint = Color(0xFF4B5563), modifier = Modifier.size(18.dp))
            }
        }) else null,
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Email),
        singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = accent, unfocusedBorderColor = Color(0xFF21262D),
            focusedContainerColor = Color(0xFF0D1117), unfocusedContainerColor = Color(0xFF0D1117),
            focusedLabelColor = accent, unfocusedLabelColor = Color(0xFF6B7280),
            focusedTextColor = Color.White, unfocusedTextColor = Color.White
        )
    )
}
