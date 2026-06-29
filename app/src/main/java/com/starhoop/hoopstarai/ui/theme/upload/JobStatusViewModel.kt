package com.starhoop.hoopstar.ui.upload

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.starhoop.hoopstar.core.DataResult
import com.starhoop.hoopstar.core.UiState
import com.starhoop.hoopstar.domain.model.Job
import com.starhoop.hoopstar.domain.usecase.CancelJobUseCase
import com.starhoop.hoopstar.domain.usecase.GetJobUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

data class JobStatusUiState(
    val job: UiState<Job> = UiState.Loading,
    val canceling: Boolean = false
)

@HiltViewModel
class JobStatusViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getJob: GetJobUseCase,
    private val cancelJob: CancelJobUseCase
) : ViewModel() {

    val jobId: Int = savedStateHandle.get<Int>("jobId") ?: -1

    private val _state = MutableStateFlow(JobStatusUiState())
    val state: StateFlow<JobStatusUiState> = _state.asStateFlow()

    init { startPolling() }

    private fun startPolling() {
        viewModelScope.launch {
            while (isActive) {
                when (val r = getJob(jobId)) {
                    is DataResult.Success -> {
                        _state.update { it.copy(job = UiState.Success(r.data)) }
                        if (r.data.status.isTerminal) break
                    }
                    is DataResult.Error -> {
                        // לא מפילים את כל המסך על שגיאת polling זמנית אם כבר יש נתון
                        if (_state.value.job !is UiState.Success) {
                            _state.update { it.copy(job = UiState.Error(r.message, r.code)) }
                        }
                    }
                }
                delay(4000)
            }
        }
    }

    fun refresh() {
        _state.update { it.copy(job = UiState.Loading) }
        startPolling()
    }

    fun cancel() {
        _state.update { it.copy(canceling = true) }
        viewModelScope.launch {
            when (val r = cancelJob(jobId)) {
                is DataResult.Success -> _state.update {
                    it.copy(canceling = false, job = UiState.Success(r.data))
                }
                is DataResult.Error -> _state.update { it.copy(canceling = false) }
            }
        }
    }
}