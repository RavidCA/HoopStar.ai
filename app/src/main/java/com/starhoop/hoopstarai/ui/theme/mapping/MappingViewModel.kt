package com.starhoop.hoopstar.ui.mapping

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.starhoop.hoopstar.core.DataResult
import com.starhoop.hoopstar.core.UiState
import com.starhoop.hoopstar.domain.model.MappingResult
import com.starhoop.hoopstar.domain.usecase.AutoMapPlayersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MappingViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val autoMap: AutoMapPlayersUseCase
) : ViewModel() {

    val jobId: Int = savedStateHandle.get<Int>("jobId") ?: -1
    val teamId: Int = savedStateHandle.get<Int>("teamId") ?: -1

    private val _state = MutableStateFlow<UiState<MappingResult>>(UiState.Loading)
    val state: StateFlow<UiState<MappingResult>> = _state.asStateFlow()

    init { run() }

    fun run() {
        _state.update { UiState.Loading }
        viewModelScope.launch {
            when (val r = autoMap(jobId, teamId)) {
                is DataResult.Success -> _state.update {
                    if (r.data.items.isEmpty()) UiState.Empty("לא זוהו שחקנים בסרטון")
                    else UiState.Success(r.data)
                }
                is DataResult.Error -> _state.update { UiState.Error(r.message, r.code) }
            }
        }
    }
}