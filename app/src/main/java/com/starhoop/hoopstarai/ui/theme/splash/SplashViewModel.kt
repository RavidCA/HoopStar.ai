package com.starhoop.hoopstar.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.starhoop.hoopstar.core.DataResult
import com.starhoop.hoopstar.domain.repository.AuthRepository
import com.starhoop.hoopstar.domain.usecase.FetchMeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SplashDestination { LOADING, LOGIN, TEAMS }

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val fetchMeUseCase: FetchMeUseCase
) : ViewModel() {

    private val _destination = MutableStateFlow(SplashDestination.LOADING)
    val destination: StateFlow<SplashDestination> = _destination.asStateFlow()

    fun decide() {
        viewModelScope.launch {
            if (!authRepository.isLoggedIn()) {
                _destination.value = SplashDestination.LOGIN
                return@launch
            }
            // יש token שמור — נוודא שהוא עדיין תקף מול /me
            when (fetchMeUseCase()) {
                is DataResult.Success -> _destination.value = SplashDestination.TEAMS
                is DataResult.Error -> {
                    authRepository.logout() // token פג/לא תקין
                    _destination.value = SplashDestination.LOGIN
                }
            }
        }
    }
}