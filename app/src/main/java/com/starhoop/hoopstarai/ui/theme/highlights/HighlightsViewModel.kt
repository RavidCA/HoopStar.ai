package com.starhoop.hoopstar.ui.highlights

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.starhoop.hoopstar.core.DataResult
import com.starhoop.hoopstar.core.UiState
import com.starhoop.hoopstar.data.repository.SavedReelsRepository
import com.starhoop.hoopstar.domain.model.Player
import com.starhoop.hoopstar.domain.model.Team
import com.starhoop.hoopstar.domain.usecase.GetTeamUseCase
import com.starhoop.hoopstar.service.ReelGenerationService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HighlightsUiState(
    val roster: UiState<Team> = UiState.Loading,
    val selectedPlayer: Player? = null,
    val started: Boolean = false // האם שלחנו את הבקשה לרקע
)

@HiltViewModel
class HighlightsViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    savedStateHandle: SavedStateHandle,
    private val getTeam: GetTeamUseCase,
    private val savedReels: SavedReelsRepository
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
                        UiState.Empty("No players in roster") else UiState.Success(r.data))
                }
                is DataResult.Error -> _state.update { it.copy(roster = UiState.Error(r.message, r.code)) }
            }
        }
    }

    fun selectPlayer(player: Player) =
        _state.update { it.copy(selectedPlayer = player, started = false) }

    fun clearSelection() = _state.update { it.copy(selectedPlayer = null, started = false) }

    /** מפעיל את החילוץ ברקע (Foreground Service). לא נהרג כשיוצאים. */
    fun generate() {
        val player = _state.value.selectedPlayer ?: return
        ReelGenerationService.start(appContext, jobId, teamId, player.playerId, player.fullName)
        _state.update { it.copy(started = true) }
    }
}