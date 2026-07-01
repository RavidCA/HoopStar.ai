package com.starhoop.hoopstar.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material3.Text
import com.starhoop.hoopstar.ui.theme.Charcoal
import com.starhoop.hoopstar.ui.theme.CharcoalElevated
import com.starhoop.hoopstar.ui.theme.HoopOrange

@Composable
fun SplashScreen(
    onLoggedIn: () -> Unit,
    onLoggedOut: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val destination by viewModel.destination.collectAsState()

    // אנימציות כניסה
    val logoScale = remember { Animatable(0.7f) }
    val logoAlpha = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }
    // אנימציית יציאה — fade של כל המסך
    val screenAlpha = remember { Animatable(1f) }

    // קפיצת כדור + צל
    val anim = rememberInfiniteTransition(label = "ball")
    val bounce by anim.animateFloat(
        0f, -20f,
        infiniteRepeatable(tween(600, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "bounce"
    )
    val shadowScale by anim.animateFloat(
        1f, 0.6f,
        infiniteRepeatable(tween(600, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "shadow"
    )
    // פס טעינה אינסופי
    val loadT by anim.animateFloat(
        0f, 1f,
        infiniteRepeatable(tween(1300, easing = LinearEasing), RepeatMode.Restart), label = "load"
    )

    LaunchedEffect(Unit) {
        viewModel.decide()
        logoAlpha.animateTo(1f, tween(550))
        logoScale.animateTo(1f, tween(700, easing = FastOutSlowInEasing))
        textAlpha.animateTo(1f, tween(600))
    }

    // כשההחלטה מוכנה — fade out ואז ניווט
    LaunchedEffect(destination) {
        if (destination != SplashDestination.LOADING) {
            screenAlpha.animateTo(0f, tween(450))
            when (destination) {
                SplashDestination.TEAMS -> onLoggedIn()
                SplashDestination.LOGIN -> onLoggedOut()
                SplashDestination.LOADING -> Unit
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(screenAlpha.value)
            .background(Brush.radialGradient(listOf(CharcoalElevated, Charcoal), radius = 1700f)),
        contentAlignment = Alignment.Center
    ) {
        StarField(Modifier.fillMaxSize())

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                Modifier.size(170.dp).alpha(logoAlpha.value),
                contentAlignment = Alignment.BottomCenter
            ) {
                // צל קרקע מתחת לכדור (מתכווץ כשהכדור עולה)
                Box(
                    Modifier
                        .padding(bottom = 8.dp)
                        .width(90.dp)
                        .height(14.dp)
                        .scale(scaleX = shadowScale, scaleY = 1f)
                        .clip(RoundedCornerShape(50))
                        .background(Color.Black.copy(alpha = 0.35f))
                )
                // הכדור
                Box(
                    Modifier
                        .size(130.dp)
                        .offset(y = bounce.dp)
                        .scale(logoScale.value),
                    contentAlignment = Alignment.Center
                ) {
                    VolumetricBall(Modifier.size(130.dp))
                }
            }

            Spacer(Modifier.height(40.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.alpha(textAlpha.value)
            ) {
                Row {
                    Text("HoopStar", color = Color.White, fontWeight = FontWeight.Black, fontSize = 46.sp)
                    Text(".ai", color = HoopOrange, fontWeight = FontWeight.Black, fontSize = 46.sp)
                }
                Spacer(Modifier.height(12.dp))
                Text("COURT INTELLIGENCE. ELEVATED.",
                    color = Color.White.copy(alpha = 0.35f),
                    fontWeight = FontWeight.Medium, fontSize = 12.sp, letterSpacing = 3.sp)

                Spacer(Modifier.height(40.dp))
                // פס טעינה דק ועדין
                LoadingBar(progress = loadT, modifier = Modifier.width(150.dp))
            }
        }
    }
}

@Composable
private fun LoadingBar(progress: Float, modifier: Modifier = Modifier) {
    Box(
        modifier
            .height(3.dp)
            .clip(RoundedCornerShape(50))
            .background(Color.White.copy(alpha = 0.10f))
    ) {
        // נקודה זוהרת שרצה לאורך הפס
        Canvas(Modifier.fillMaxWidth().height(3.dp)) {
            val cx = size.width * progress
            val grad = Brush.horizontalGradient(
                listOf(Color.Transparent, HoopOrange, Color.Transparent),
                startX = cx - 50f, endX = cx + 50f
            )
            drawRect(grad)
        }
    }
}

@Composable
private fun VolumetricBall(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val w = size.width
        val cx = w / 2f
        val cy = size.height / 2f
        val r = w * 0.40f
        val sw = w * 0.045f

        // הילה רכה מסביב
        drawCircle(
            brush = Brush.radialGradient(
                listOf(HoopOrange.copy(alpha = 0.45f), Color.Transparent),
                center = Offset(cx, cy), radius = r * 1.9f
            ),
            radius = r * 1.9f, center = Offset(cx, cy)
        )

        // גוף הכדור — gradient רדיאלי עם מקור אור למעלה-שמאל (נראה תלת-ממדי)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFFFA94D), Color(0xFFFF6F00), Color(0xFFC43E00)),
                center = Offset(cx - r * 0.3f, cy - r * 0.3f),
                radius = r * 1.5f
            ),
            radius = r, center = Offset(cx, cy)
        )

        // נקודת ברק (highlight) למעלה-שמאל
        drawCircle(
            brush = Brush.radialGradient(
                listOf(Color.White.copy(alpha = 0.5f), Color.Transparent),
                center = Offset(cx - r * 0.35f, cy - r * 0.4f), radius = r * 0.6f
            ),
            radius = r * 0.55f, center = Offset(cx - r * 0.35f, cy - r * 0.4f)
        )

        // קווי תפר כהים
        val seamColor = Color(0xFF2A1505)
        val seam = sw * 0.65f
        drawLine(seamColor, Offset(cx, cy - r), Offset(cx, cy + r), seam, StrokeCap.Round)
        drawLine(seamColor, Offset(cx - r, cy), Offset(cx + r, cy), seam, StrokeCap.Round)
        val inset = r * 0.55f
        drawArc(seamColor, -90f, 180f, false,
            topLeft = Offset(cx - inset, cy - r), size = Size(inset * 2, r * 2),
            style = Stroke(width = seam, cap = StrokeCap.Round))
        drawArc(seamColor, 90f, 180f, false,
            topLeft = Offset(cx - inset, cy - r), size = Size(inset * 2, r * 2),
            style = Stroke(width = seam, cap = StrokeCap.Round))
    }
}