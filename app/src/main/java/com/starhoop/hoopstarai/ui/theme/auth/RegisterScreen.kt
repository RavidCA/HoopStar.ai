package com.starhoop.hoopstar.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.starhoop.hoopstar.ui.components.ErrorBanner
import com.starhoop.hoopstar.ui.components.HoopPrimaryButton
import com.starhoop.hoopstar.ui.components.HoopTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onBack: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.success) {
        if (state.success) {
            viewModel.resetForNavigation()
            onRegisterSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("יצירת חשבון") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "חזרה")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HoopTextField(
                value = state.displayName, onValueChange = viewModel::onDisplayNameChange,
                label = "שם תצוגה", isError = state.error != null
            )
            Spacer(Modifier.height(14.dp))
            HoopTextField(
                value = state.email, onValueChange = viewModel::onEmailChange,
                label = "אימייל", keyboardType = KeyboardType.Email,
                isError = state.error != null
            )
            Spacer(Modifier.height(14.dp))
            HoopTextField(
                value = state.password, onValueChange = viewModel::onPasswordChange,
                label = "סיסמה (לפחות 6 תווים)", isPassword = true,
                isError = state.error != null
            )

            if (state.error != null) {
                Spacer(Modifier.height(8.dp))
                ErrorBanner(state.error!!)
            }

            Spacer(Modifier.height(24.dp))
            HoopPrimaryButton(
                text = "הרשמה",
                onClick = viewModel::register,
                loading = state.loading
            )
        }
    }
}