package com.starhoop.hoopstar.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.starhoop.hoopstar.ui.components.ErrorBanner
import com.starhoop.hoopstar.ui.components.HoopPrimaryButton
import com.starhoop.hoopstar.ui.components.HoopTextField

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onGoToRegister: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.success) {
        if (state.success) {
            viewModel.resetForNavigation()
            onLoginSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 28.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("HOOPSTAR", fontWeight = FontWeight.Black, fontSize = 38.sp,
            color = MaterialTheme.colorScheme.onBackground)
        Text(".ai", fontWeight = FontWeight.Black, fontSize = 24.sp,
            color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(8.dp))
        Text("התחברות מאמן", style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(36.dp))

        HoopTextField(
            value = state.email, onValueChange = viewModel::onEmailChange,
            label = "אימייל", keyboardType = KeyboardType.Email,
            isError = state.error != null
        )
        Spacer(Modifier.height(14.dp))
        HoopTextField(
            value = state.password, onValueChange = viewModel::onPasswordChange,
            label = "סיסמה", isPassword = true, isError = state.error != null
        )

        if (state.error != null) {
            Spacer(Modifier.height(8.dp))
            ErrorBanner(state.error!!)
        }

        Spacer(Modifier.height(24.dp))
        HoopPrimaryButton(
            text = "התחבר",
            onClick = viewModel::login,
            loading = state.loading
        )
        Spacer(Modifier.height(8.dp))
        TextButton(onClick = onGoToRegister, enabled = !state.loading) {
            Text("אין לך חשבון? הרשמה", color = MaterialTheme.colorScheme.primary)
        }
    }
}