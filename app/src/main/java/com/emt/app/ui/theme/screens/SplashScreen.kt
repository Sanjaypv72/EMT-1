package com.emt.app.ui.theme.screens

import androidx.compose.animation.core.*
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.LinearEasing
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emt.app.ui.theme.InterFamily
import com.emt.app.ui.theme.PoppinsFamily
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onSplashComplete: () -> Unit) {
    var phase by remember { mutableStateOf(0) }

    val logoScale by animateFloatAsState(
        targetValue = if (phase >= 1) 1f else 0.4f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow),
        label = "s"
    )
    val logoAlpha by animateFloatAsState(targetValue = if (phase >= 1) 1f else 0f, animationSpec = tween(600), label = "la")
    val textAlpha by animateFloatAsState(targetValue = if (phase >= 2) 1f else 0f, animationSpec = tween(500, delayMillis = 100), label = "ta")
    val textOffsetY by animateFloatAsState(targetValue = if (phase >= 2) 0f else 20f, animationSpec = tween(500, delayMillis = 100, easing = EaseOutCubic), label = "ty")
    val tagAlpha by animateFloatAsState(targetValue = if (phase >= 3) 1f else 0f, animationSpec = tween(400, delayMillis = 80), label = "ga")
    val barWidth by animateFloatAsState(targetValue = if (phase >= 3) 1f else 0f, animationSpec = tween(700, easing = EaseOutCubic), label = "bw")

    val inf = rememberInfiniteTransition(label = "i")
    val rotate by inf.animateFloat(0f, 360f, infiniteRepeatable(tween(9000, easing = LinearEasing)), label = "r")
    val pulse by inf.animateFloat(0.90f, 1.10f, infiniteRepeatable(tween(1800, easing = EaseInOutSine), RepeatMode.Reverse), label = "p")

    LaunchedEffect(Unit) {
        delay(200); phase = 1; delay(350); phase = 2; delay(300); phase = 3; delay(1900); onSplashComplete()
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0D1117)), contentAlignment = Alignment.Center) {
        Box(modifier = Modifier.size(420.dp).align(Alignment.Center).scale(pulse).background(Brush.radialGradient(listOf(Color(0xFF00796B).copy(alpha = 0.15f), Color.Transparent))))
        Box(modifier = Modifier.size(165.dp).rotate(rotate).background(Brush.sweepGradient(listOf(Color.Transparent, Color(0xFF00796B).copy(0.55f), Color.Transparent)), RoundedCornerShape(50)))

        // Corner decorations
        Box(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.size(90.dp).align(Alignment.TopStart).offset((-25).dp, (-25).dp).rotate(45f).background(Color(0xFF00796B).copy(0.07f), RoundedCornerShape(14.dp)))
            Box(modifier = Modifier.size(55.dp).align(Alignment.BottomEnd).offset(18.dp, 18.dp).rotate(30f).background(Color(0xFFFFB300).copy(0.06f), RoundedCornerShape(10.dp)))
            Box(modifier = Modifier.size(35.dp).align(Alignment.TopEnd).offset(8.dp, 90.dp).rotate(20f).background(Color(0xFF00796B).copy(0.05f), RoundedCornerShape(6.dp)))
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier.scale(logoScale).alpha(logoAlpha).size(92.dp).clip(RoundedCornerShape(28.dp))
                    .background(Brush.linearGradient(listOf(Color(0xFF00897B), Color(0xFF004D40)))),
                contentAlignment = Alignment.Center
            ) {
                Text("EMT", fontSize = 24.sp, fontFamily = PoppinsFamily, fontWeight = FontWeight.ExtraBold, color = Color.White, letterSpacing = 1.sp)
            }

            Spacer(Modifier.height(30.dp))

            Column(modifier = Modifier.alpha(textAlpha).offset(y = textOffsetY.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("EMT", fontSize = 40.sp, fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold, color = Color.White, letterSpacing = 5.sp)
                Spacer(Modifier.height(4.dp))
                Text("Employee Management Tool", fontSize = 12.sp, fontFamily = InterFamily, color = Color(0xFF4DB6AC), letterSpacing = 1.8.sp, textAlign = TextAlign.Center)
            }

            Spacer(Modifier.height(44.dp))

            Box(modifier = Modifier.alpha(tagAlpha).width(130.dp).height(3.dp).clip(RoundedCornerShape(50)).background(Color(0xFF1A2A28))) {
                Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(barWidth).clip(RoundedCornerShape(50)).background(Brush.horizontalGradient(listOf(Color(0xFF00796B), Color(0xFF80CBC4)))))
            }
        }

        Text("v1.0.0", fontSize = 11.sp, fontFamily = InterFamily, color = Color.White.copy(0.18f), modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 24.dp))
    }
}
