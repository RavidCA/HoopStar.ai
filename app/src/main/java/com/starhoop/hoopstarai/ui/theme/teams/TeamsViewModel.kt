package com.starhoop.hoopstar.ui.teams

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.starhoop.hoopstar.core.DataResult
import com.starhoop.hoopstar.core.TeamColorPalette
import com.starhoop.hoopstar.core.TeamLogos
import com.starhoop.hoopstar.core.UiState
import com.starhoop.hoopstar.data.local.TokenStore
import com.starhoop.hoopstar.domain.model.Team
import com.starhoop.hoopstar.domain.repository.AuthRepository
import com.starhoop.hoopstar.domain.repository.TeamsRepository
import com.starhoop.hoopstar.domain.usecase.GetMyTeamsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CreateTeamForm(
    val name: String = "",
    val season: String = "2025-26",
    val color: String = TeamColorPalette.first(),
    val logoId: String = TeamLogos.all.first().id,
    val loading: Boolean = false,
    val error: String? = null
)

data class TeamsUiState(
    val teams: UiState<List<Team>> = UiState.Loading,
    val coachName: String = "",
    val showCreate: Boolean = false,
    val create: CreateTeamForm = CreateTeamForm()
)

@HiltViewModel
class TeamsViewModel @Inject constructor(
    private val getMyTeams: GetMyTeamsUseCase,
    private val teamsRepository: TeamsRepository,
    private val authRepository: AuthRepository,
    private val tokenStore: TokenStore
) : ViewModel() {

    private val _state = MutableStateFlow(TeamsUiState(coachName = tokenStore.getDisplayName() ?: ""))
    val state: StateFlow<TeamsUiState> = _state.asStateFlow()

    fun load() {
        if (_state.value.teams !is UiState.Success) {
            _state.update { it.copy(teams = UiState.Loading) }
        }
        viewModelScope.launch {
            when (val r = getMyTeams()) {
                is DataResult.Success -> _state.update {
                    it.copy(teams = if (r.data.isEmpty())
                        UiState.Empty("עדיין אין קבוצות") else UiState.Success(r.data))
                }
                is DataResult.Error -> _state.update { it.copy(teams = UiState.Error(r.message, r.code)) }
            }
        }
    }

    fun openCreate() = _state.update { it.copy(showCreate = true, create = CreateTeamForm()) }
    fun closeCreate() = _state.update { it.copy(showCreate = false) }

    fun onName(v: String) = _state.update { it.copy(create = it.create.copy(name = v, error = null)) }
    fun onSeason(v: String) = _state.update { it.copy(create = it.create.copy(season = v, error = null)) }
    fun onColor(hex: String) = _state.update { it.copy(create = it.create.copy(color = hex)) }
    fun onLogo(id: String) = _state.update { it.copy(create = it.create.copy(logoId = id)) }

    fun submitCreate() {
        val form = _state.value.create
        if (form.name.isBlank()) {
            _state.update { it.copy(create = it.create.copy(error = "יש להזין שם קבוצה.")) }
            return
        }
        _state.update { it.copy(create = it.create.copy(loading = true, error = null)) }
        viewModelScope.launch {
            val r = teamsRepository.createTeam(
                name = form.name.trim(),
                season = form.season.trim().ifBlank { "2025-26" },
                color = form.color,
                logoUrl = TeamLogos.encode(form.logoId)
            )
            when (r) {
                is DataResult.Success -> {
                    _state.update { it.copy(showCreate = false, create = CreateTeamForm()) }
                    load()
                }
                is DataResult.Error -> _state.update {
                    it.copy(create = it.create.copy(loading = false, error = r.message))
                }
            }
        }
    }

    fun logout() = authRepository.logout()
}