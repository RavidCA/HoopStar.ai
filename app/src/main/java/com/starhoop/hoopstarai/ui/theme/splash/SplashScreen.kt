package com.starhoop.hoopstar.ui.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SplashScreen(
    onLoggedIn: () -> Unit,
    onLoggedOut: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val destination by viewModel.destination.collectAsState()

    LaunchedEffect(Unit) { viewModel.decide() }
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
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("HOOPSTAR", color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Black, fontSize = 40.sp)
            Text(".ai", color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Black, fontSize = 28.sp)
            Spacer(Modifier.height(24.dp))
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    }
}