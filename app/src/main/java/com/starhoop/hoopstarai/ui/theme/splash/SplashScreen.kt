package com.starhoop.hoopstar.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.starhoop.hoopstar.ui.theme.Charcoal
import com.starhoop.hoopstar.ui.theme.CharcoalElevated

@Composable
fun SplashScreen(
    onLoggedIn: () -> Unit,
    onLoggedOut: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val destination by viewModel.destination.collectAsState()

    val scale = remember { Animatable(0.6f) }
    val alpha = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        viewModel.decide()
        // אנימציית כניסה ללוגו
        alpha.animateTo(1f, tween(500))
        scale.animateTo(1f, tween(650, easing = FastOutSlowInEasing))
        textAlpha.animateTo(1f, tween(500))
    }

    LaunchedEffect(destination) {
        when (destination) {
            SplashDestination.TEAMS -> onLoggedIn()
            SplashDestination.LOGIN -> onLoggedOut()
            SplashDestination.LOADING -> Unit
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(CharcoalElevated, Charcoal),
                    center = Offset.Unspecified,
                    radius = 1400f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height(28.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.alpha(textAlpha.value)
            ) {
                androidx.compose.foundation.layout.Row {
                    Text("HOOPSTAR", color = Color.White,
                        fontWeight = FontWeight.Black, fontSize = 34.sp, letterSpacing = 1.sp)
                    Text(".ai", color = com.starhoop.hoopstar.ui.theme.HoopOrange,
                        fontWeight = FontWeight.Black, fontSize = 34.sp)
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    "הופכים משחק לכוכבים",
                    color = Color.White.copy(alpha = 0.55f),
                    fontWeight = FontWeight.Medium, fontSize = 14.sp, letterSpacing = 0.5.sp
                )
            }
        }
    }
}