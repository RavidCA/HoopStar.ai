package com.starhoop.hoopstar.data.repository

import com.starhoop.hoopstar.core.DataResult
import com.starhoop.hoopstar.data.remote.TeamsApi
import com.starhoop.hoopstar.data.remote.dto.CreatePlayerRequest
import com.starhoop.hoopstar.data.remote.dto.CreateTeamRequest
import com.starhoop.hoopstar.data.remote.dto.PlayerDto
import com.starhoop.hoopstar.data.remote.dto.TeamDto
import com.starhoop.hoopstar.data.remote.dto.UpdatePlayerRequest
import com.starhoop.hoopstar.data.remote.safeApiCall
import com.starhoop.hoopstar.domain.model.Player
import com.starhoop.hoopstar.domain.model.Team
import com.starhoop.hoopstar.domain.repository.TeamsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TeamsRepositoryImpl @Inject constructor(
    private val api: TeamsApi
) : TeamsRepository {

    private fun PlayerDto.toDomain() = Player(playerId, teamId, jerseyNumber, fullName, photoUrl, birthYear)

    private fun TeamDto.toDomain() = Team(
        teamId = teamId,
        coachId = coachId,
        name = name,
        season = season ?: "",
        color = color,
        logoUrl = logoUrl,
        players = (players ?: emptyList()).map { it.toDomain() }.sortedBy { it.jerseyNumber }
    )

    override suspend fun getMyTeams(): DataResult<List<Team>> = withContext(Dispatchers.IO) {
        when (val r = safeApiCall("טעינת הקבוצות נכשלה.") { api.getMyTeams() }) {
            is DataResult.Success -> DataResult.Success(r.data.map { it.toDomain() })
            is DataResult.Error -> r
        }
    }

    override suspend fun getTeam(teamId: Int): DataResult<Team> = withContext(Dispatchers.IO) {
        when (val r = safeApiCall("טעינת הקבוצה נכשלה.") { api.getTeam(teamId) }) {
            is DataResult.Success -> DataResult.Success(r.data.toDomain())
            is DataResult.Error -> r
        }
    }

    override suspend fun createTeam(
        name: String, season: String, color: String, logoUrl: String?
    ): DataResult<Team> = withContext(Dispatchers.IO) {
        when (val r = safeApiCall("יצירת הקבוצה נכשלה.") {
            api.createTeam(CreateTeamRequest(name = name, season = season, color = color, logoUrl = logoUrl))
        }) {
            is DataResult.Success -> DataResult.Success(r.data.toDomain())
            is DataResult.Error -> r
        }
    }

    override suspend fun addPlayer(
        teamId: Int, jersey: Int, fullName: String, birthYear: Int?, photoUrl: String?
    ): DataResult<Player> = withContext(Dispatchers.IO) {
        when (val r = safeApiCall("הוספת השחקן נכשלה.") {
            api.addPlayer(teamId, CreatePlayerRequest(jersey, fullName, photoUrl, birthYear))
        }) {
            is DataResult.Success -> DataResult.Success(r.data.toDomain())
            is DataResult.Error -> r
        }
    }

    override suspend fun updatePlayer(
        teamId: Int, playerId: Int, jersey: Int?, fullName: String?, birthYear: Int?, photoUrl: String?
    ): DataResult<Player> = withContext(Dispatchers.IO) {
        when (val r = safeApiCall("עדכון השחקן נכשל.") {
            api.updatePlayer(teamId, playerId, UpdatePlayerRequest(jersey, fullName, photoUrl, birthYear))
        }) {
            is DataResult.Success -> DataResult.Success(r.data.toDomain())
            is DataResult.Error -> r
        }
    }

    override suspend fun deletePlayer(teamId: Int, playerId: Int): DataResult<Unit> =
        withContext(Dispatchers.IO) {
            safeApiCall("מחיקת השחקן נכשלה.") { api.deletePlayer(teamId, playerId) }
        }
}