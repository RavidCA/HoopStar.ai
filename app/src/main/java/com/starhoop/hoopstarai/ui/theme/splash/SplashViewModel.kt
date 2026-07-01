package com.starhoop.hoopstar.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.starhoop.hoopstar.core.DataResult
import com.starhoop.hoopstar.domain.repository.AuthRepository
import com.starhoop.hoopstar.domain.usecase.FetchMeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
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
            val minDelay = async { delay(3500) }
            val result = async {
                if (!authRepository.isLoggedIn()) {
                    SplashDestination.LOGIN
                } else {
                    when (fetchMeUseCase()) {
                        is DataResult.Success -> SplashDestination.TEAMS
                        is DataResult.Error -> {
                            authRepository.logout()
                            SplashDestination.LOGIN
                        }
                    }
                }
            }
            minDelay.await()           // מחכים לפחות 4 שניות
            _destination.value = result.await()
        }
    }
}