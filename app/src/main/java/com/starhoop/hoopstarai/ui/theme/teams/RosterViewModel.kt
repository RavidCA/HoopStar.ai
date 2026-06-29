package com.starhoop.hoopstar.ui.teams

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.starhoop.hoopstar.core.DataResult
import com.starhoop.hoopstar.core.UiState
import com.starhoop.hoopstar.data.local.TokenStore
import com.starhoop.hoopstar.domain.model.Player
import com.starhoop.hoopstar.domain.model.Team
import com.starhoop.hoopstar.domain.usecase.AddPlayerUseCase
import com.starhoop.hoopstar.domain.usecase.DeletePlayerUseCase
import com.starhoop.hoopstar.domain.usecase.GetTeamUseCase
import com.starhoop.hoopstar.domain.usecase.UpdatePlayerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlayerEditorForm(
    val playerId: Int? = null, // null = הוספה, אחרת עריכה
    val jersey: String = "",
    val fullName: String = "",
    val birthYear: String = "",
    val photoUrl: String = "",
    val loading: Boolean = false,
    val error: String? = null
) {
    val isEdit get() = playerId != null
}

data class RosterUiState(
    val team: UiState<Team> = UiState.Loading,
    val isOwner: Boolean = false,
    val editor: PlayerEditorForm? = null,
    val confirmDelete: Player? = null
)

@HiltViewModel
class RosterViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getTeam: GetTeamUseCase,
    private val addPlayer: AddPlayerUseCase,
    private val updatePlayer: UpdatePlayerUseCase,
    private val deletePlayer: DeletePlayerUseCase,
    private val tokenStore: TokenStore
) : ViewModel() {

    private val teamId: Int = savedStateHandle.get<Int>("teamId") ?: -1

    private val _state = MutableStateFlow(RosterUiState())
    val state: StateFlow<RosterUiState> = _state.asStateFlow()

    init { load() }

    fun load() {
        if (_state.value.team !is UiState.Success) {
            _state.update { it.copy(team = UiState.Loading) }
        }
        viewModelScope.launch {
            when (val r = getTeam(teamId)) {
                is DataResult.Success -> _state.update {
                    it.copy(
                        team = UiState.Success(r.data),
                        isOwner = r.data.coachId == tokenStore.getCoachId()
                    )
                }
                is DataResult.Error -> _state.update { it.copy(team = UiState.Error(r.message, r.code)) }
            }
        }
    }

    fun openAdd() = _state.update { it.copy(editor = PlayerEditorForm()) }

    fun openEdit(p: Player) = _state.update {
        it.copy(editor = PlayerEditorForm(
            playerId = p.playerId,
            jersey = p.jerseyNumber.toString(),
            fullName = p.fullName,
            birthYear = p.birthYear?.toString() ?: "",
            photoUrl = p.photoUrl ?: ""
        ))
    }

    fun closeEditor() = _state.update { it.copy(editor = null) }

    fun onJersey(v: String) = updateEditor { it.copy(jersey = v.filter { c -> c.isDigit() }.take(2), error = null) }
    fun onName(v: String) = updateEditor { it.copy(fullName = v, error = null) }
    fun onBirthYear(v: String) = updateEditor { it.copy(birthYear = v.filter { c -> c.isDigit() }.take(4), error = null) }
    fun onPhotoUrl(v: String) = updateEditor { it.copy(photoUrl = v, error = null) }

    private fun updateEditor(block: (PlayerEditorForm) -> PlayerEditorForm) =
        _state.update { it.copy(editor = it.editor?.let(block)) }

    fun submitEditor() {
        val e = _state.value.editor ?: return
        val jersey = e.jersey.toIntOrNull()
        when {
            e.fullName.isBlank() -> { updateEditor { it.copy(error = "יש להזין שם מלא.") }; return }
            jersey == null || jersey !in 0..99 -> { updateEditor { it.copy(error = "מספר חולצה בין 0 ל-99.") }; return }
        }
        val birthYear = e.birthYear.takeIf { it.isNotBlank() }?.toIntOrNull()
        if (e.birthYear.isNotBlank() && birthYear == null) {
            updateEditor { it.copy(error = "שנת לידה לא תקינה.") }; return
        }
        val photo = e.photoUrl.trim().ifBlank { null }
        updateEditor { it.copy(loading = true, error = null) }
        viewModelScope.launch {
            val result = if (e.playerId == null)
                addPlayer(teamId, jersey!!, e.fullName.trim(), birthYear, photo)
            else
                updatePlayer(teamId, e.playerId, jersey!!, e.fullName.trim(), birthYear, photo)
            when (result) {
                is DataResult.Success -> { closeEditor(); load() }
                is DataResult.Error -> updateEditor { it.copy(loading = false, error = result.message) }
            }
        }
    }

    fun requestDelete(p: Player) = _state.update { it.copy(confirmDelete = p) }
    fun cancelDelete() = _state.update { it.copy(confirmDelete = null) }

    fun confirmDelete() {
        val p = _state.value.confirmDelete ?: return
        _state.update { it.copy(confirmDelete = null) }
        viewModelScope.launch {
            when (deletePlayer(teamId, p.playerId)) {
                is DataResult.Success -> load()
                is DataResult.Error -> load() // נטען מחדש כדי לשקף את המצב האמיתי
            }
        }
    }
}