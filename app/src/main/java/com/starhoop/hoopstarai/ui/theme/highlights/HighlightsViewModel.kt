package com.starhoop.hoopstar.ui.highlights

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.starhoop.hoopstar.BuildConfig
import com.starhoop.hoopstar.core.DataResult
import com.starhoop.hoopstar.core.UiState
import com.starhoop.hoopstar.domain.model.ComposedReel
import com.starhoop.hoopstar.domain.model.ExtractedReel
import com.starhoop.hoopstar.domain.model.Player
import com.starhoop.hoopstar.domain.model.Team
import com.starhoop.hoopstar.domain.usecase.ComposeReelUseCase
import com.starhoop.hoopstar.domain.usecase.ExtractReelUseCase
import com.starhoop.hoopstar.domain.usecase.GetTeamUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class GenerationPhase { IDLE, EXTRACTING, COMPOSING, READY, NO_CLIPS, ERROR }

data class HighlightsUiState(
    val roster: UiState<Team> = UiState.Loading,
    val selectedPlayer: Player? = null,
    val phase: GenerationPhase = GenerationPhase.IDLE,
    val extracted: ExtractedReel? = null,
    val composed: ComposedReel? = null,
    val errorMessage: String? = null
) {
    /** ה-URL המלא לנגן/הורדה — מעדיף את download_url מה-JSON. */
    val playbackUrl: String?
        get() {
            val url = composed?.downloadUrl ?: extracted?.downloadUrl ?: return null
            return if (url.startsWith("http")) url else BuildConfig.BASE_URL.trimEnd('/') + "/" + url.trimStart('/')
        }
}

@HiltViewModel
class HighlightsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getTeam: GetTeamUseCase,
    private val extractReel: ExtractReelUseCase,
    private val composeReel: ComposeReelUseCase
) : ViewModel() {

    val jobId: Int = savedStateHandle.get<Int>("jobId") ?: -1
    val teamId: Int = savedStateHandle.get<Int>("teamId") ?: -1

    private val _state = MutableStateFlow(HighlightsUiState())
    val state: StateFlow<HighlightsUiState> = _state.asStateFlow()

    init { loadRoster() }

    fun loadRoster() {
        _state.update { it.copy(roster = UiState.Loading) }
        viewModelScope.launch {
            when (val r = getTeam(teamId)) {
                is DataResult.Success -> _state.update {
                    it.copy(roster = if (r.data.players.isEmpty())
                        UiState.Empty("אין שחקנים בסגל") else UiState.Success(r.data))
                }
                is DataResult.Error -> _state.update { it.copy(roster = UiState.Error(r.message, r.code)) }
            }
        }
    }

    fun selectPlayer(player: Player) {
        _state.update {
            it.copy(selectedPlayer = player, phase = GenerationPhase.IDLE,
                extracted = null, composed = null, errorMessage = null)
        }
    }

    fun clearSelection() = _state.update {
        it.copy(selectedPlayer = null, phase = GenerationPhase.IDLE,
            extracted = null, composed = null, errorMessage = null)
    }

    /** הזרימה המלאה: extract -> compose. שתיהן איטיות (דקות). */
    fun generate() {
        val player = _state.value.selectedPlayer ?: return
        _state.update { it.copy(phase = GenerationPhase.EXTRACTING, errorMessage = null) }
        viewModelScope.launch {
            when (val ex = extractReel(jobId, player.playerId)) {
                is DataResult.Success -> {
                    _state.update { it.copy(extracted = ex.data, phase = GenerationPhase.COMPOSING) }
                    when (val co = composeReel(jobId, ex.data.reelId)) {
                        is DataResult.Success -> _state.update {
                            it.copy(composed = co.data, phase = GenerationPhase.READY)
                        }
                        is DataResult.Error -> _state.update {
                            it.copy(phase = GenerationPhase.ERROR, errorMessage = co.message)
                        }
                    }
                }
                is DataResult.Error -> {
                    // 422 = אין קליפים לשחקן הזה
                    if (ex.code == 422) {
                        _state.update { it.copy(phase = GenerationPhase.NO_CLIPS) }
                    } else {
                        _state.update { it.copy(phase = GenerationPhase.ERROR, errorMessage = ex.message) }
                    }
                }
            }
        }
    }

    fun retry() = generate()
}