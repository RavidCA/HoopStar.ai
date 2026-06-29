package com.starhoop.hoopstar.domain.usecase

import com.starhoop.hoopstar.core.DataResult
import com.starhoop.hoopstar.domain.model.Team
import com.starhoop.hoopstar.domain.repository.TeamsRepository
import javax.inject.Inject

class GetMyTeamsUseCase @Inject constructor(private val repo: TeamsRepository) {
    suspend operator fun invoke(): DataResult<List<Team>> = repo.getMyTeams()
}

class GetTeamUseCase @Inject constructor(private val repo: TeamsRepository) {
    suspend operator fun invoke(teamId: Int): DataResult<Team> = repo.getTeam(teamId)
}

class CreateTeamUseCase @Inject constructor(private val repo: TeamsRepository) {
    suspend operator fun invoke(name: String, season: String, color: String): DataResult<Team> =
        repo.createTeam(name.trim(), season.trim().ifBlank { "2025-26" }, color, null)
}

class AddPlayerUseCase @Inject constructor(private val repo: TeamsRepository) {
    suspend operator fun invoke(teamId: Int, jersey: Int, fullName: String, birthYear: Int?, photoUrl: String?) =
        repo.addPlayer(teamId, jersey, fullName, birthYear, photoUrl)
}

class UpdatePlayerUseCase @Inject constructor(private val repo: TeamsRepository) {
    suspend operator fun invoke(teamId: Int, playerId: Int, jersey: Int?, fullName: String?, birthYear: Int?, photoUrl: String?) =
        repo.updatePlayer(teamId, playerId, jersey, fullName, birthYear, photoUrl)
}

class DeletePlayerUseCase @Inject constructor(private val repo: TeamsRepository) {
    suspend operator fun invoke(teamId: Int, playerId: Int): DataResult<Unit> =
        repo.deletePlayer(teamId, playerId)
}