package com.starhoop.hoopstar.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.starhoop.hoopstar.core.DataResult
import com.starhoop.hoopstar.domain.usecase.LoginUseCase
import com.starhoop.hoopstar.domain.usecase.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthFormState(
    val email: String = "",
    val password: String = "",
    val displayName: String = "",
    val loading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AuthFormState())
    val state: StateFlow<AuthFormState> = _state.asStateFlow()

    fun onEmailChange(v: String) = _state.update { it.copy(email = v, error = null) }
    fun onPasswordChange(v: String) = _state.update { it.copy(password = v, error = null) }
    fun onDisplayNameChange(v: String) = _state.update { it.copy(displayName = v, error = null) }

    fun resetForNavigation() = _state.update { AuthFormState() }

    fun login() {
        val s = _state.value
        if (!validate(s, requireName = false)) return
        _state.update { it.copy(loading = true, error = null) }
        viewModelScope.launch {
            when (val res = loginUseCase(s.email, s.password)) {
                is DataResult.Success -> _state.update { it.copy(loading = false, success = true) }
                is DataResult.Error -> _state.update { it.copy(loading = false, error = res.message) }
            }
        }
    }

    fun register() {
        val s = _state.value
        if (!validate(s, requireName = true)) return
        _state.update { it.copy(loading = true, error = null) }
        viewModelScope.launch {
            when (val res = registerUseCase(s.email, s.password, s.displayName)) {
                is DataResult.Success -> _state.update { it.copy(loading = false, success = true) }
                is DataResult.Error -> _state.update { it.copy(loading = false, error = res.message) }
            }
        }
    }

    private fun validate(s: AuthFormState, requireName: Boolean): Boolean {
        when {
            s.email.isBlank() || !s.email.contains("@") -> {
                _state.update { it.copy(error = "כתובת אימייל לא תקינה.") }
                return false
            }
            s.password.length < 6 -> {
                _state.update { it.copy(error = "הסיסמה חייבת להיות לפחות 6 תווים.") }
                return false
            }
            requireName && s.displayName.isBlank() -> {
                _state.update { it.copy(error = "יש להזין שם תצוגה.") }
                return false
            }
        }
        return true
    }
}