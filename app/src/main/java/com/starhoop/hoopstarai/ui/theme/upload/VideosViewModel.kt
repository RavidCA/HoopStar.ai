package com.starhoop.hoopstar.ui.upload

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.starhoop.hoopstar.core.DataResult
import com.starhoop.hoopstar.core.UiState
import com.starhoop.hoopstar.domain.model.Job
import com.starhoop.hoopstar.domain.usecase.ListJobsUseCase
import com.starhoop.hoopstar.domain.usecase.UploadVideoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VideosUiState(
    val jobs: UiState<List<Job>> = UiState.Loading,
    val uploading: Boolean = false,
    val selectedFileName: String? = null,
    val selectedFileUri: String? = null,
    val uploadError: String? = null,
    val newJobId: Int? = null // אירוע ניווט אחרי העלאה
)

@HiltViewModel
class VideosViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val listJobs: ListJobsUseCase,
    private val uploadVideo: UploadVideoUseCase
) : ViewModel() {

    val teamId: Int = savedStateHandle.get<Int>("teamId") ?: -1

    private val _state = MutableStateFlow(VideosUiState())
    val state: StateFlow<VideosUiState> = _state.asStateFlow()

    init { loadJobs() }

    fun loadJobs() {
        if (_state.value.jobs !is UiState.Success) {
            _state.update { it.copy(jobs = UiState.Loading) }
        }
        viewModelScope.launch {
            when (val r = listJobs(teamId)) {
                is DataResult.Success -> _state.update {
                    it.copy(jobs = if (r.data.isEmpty()) UiState.Empty("עדיין אין משחקים")
                    else UiState.Success(r.data))
                }
                is DataResult.Error -> _state.update { it.copy(jobs = UiState.Error(r.message, r.code)) }
            }
        }
    }

    fun onFileSelected(uri: String, name: String?) = _state.update {
        it.copy(selectedFileUri = uri, selectedFileName = name, uploadError = null)
    }

    fun clearSelection() = _state.update {
        it.copy(selectedFileUri = null, selectedFileName = null, uploadError = null)
    }

    fun upload() {
        val uri = _state.value.selectedFileUri ?: return
        _state.update { it.copy(uploading = true, uploadError = null) }
        viewModelScope.launch {
            when (val r = uploadVideo(teamId, uri)) {
                is DataResult.Success -> _state.update {
                    it.copy(uploading = false, newJobId = r.data,
                        selectedFileUri = null, selectedFileName = null)
                }
                is DataResult.Error -> _state.update {
                    it.copy(uploading = false, uploadError = r.message)
                }
            }
        }
    }

    fun consumeNavigation() = _state.update { it.copy(newJobId = null) }
}