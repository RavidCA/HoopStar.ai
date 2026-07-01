package com.starhoop.hoopstar.ui.splash

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

private data class Star(val x: Float, val y: Float, val r: Float, val phase: Float, val baseAlpha: Float)

@Composable
fun StarField(modifier: Modifier = Modifier, count: Int = 70) {
    val stars = remember {
        val rnd = Random(42)
        List(count) {
            Star(
                x = rnd.nextFloat(),
                y = rnd.nextFloat(),
                r = rnd.nextFloat() * 2.2f + 0.6f,
                phase = rnd.nextFloat(),
                baseAlpha = rnd.nextFloat() * 0.5f + 0.2f
            )
        }
    }
    val transition = rememberInfiniteTransition(label = "stars")
    val t by transition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3000), RepeatMode.Reverse),
        label = "twinkle"
    )

    Canvas(modifier) {
        stars.forEach { s ->
            // נצנוץ: אלפא משתנה לפי הפאזה של כל כוכב
            val twinkle = 0.5f + 0.5f * kotlin.math.sin((t + s.phase) * 6.28f)
            val alpha = (s.baseAlpha * twinkle).coerceIn(0.05f, 0.85f)
            drawCircle(
                color = Color.White.copy(alpha = alpha),
                radius = s.r,
                center = Offset(s.x * size.width, s.y * size.height)
            )
        }
    }
}