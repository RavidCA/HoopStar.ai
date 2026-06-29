package com.starhoop.hoopstar.domain.repository

import com.starhoop.hoopstar.core.DataResult
import com.starhoop.hoopstar.domain.model.Player
import com.starhoop.hoopstar.domain.model.Team

interface TeamsRepository {
    suspend fun getMyTeams(): DataResult<List<Team>>
    suspend fun getTeam(teamId: Int): DataResult<Team>
    suspend fun createTeam(name: String, season: String, color: String, logoUrl: String?): DataResult<Team>
    suspend fun addPlayer(teamId: Int, jersey: Int, fullName: String, birthYear: Int?, photoUrl: String?): DataResult<Player>
    suspend fun updatePlayer(teamId: Int, playerId: Int, jersey: Int?, fullName: String?, birthYear: Int?, photoUrl: String?): DataResult<Player>
    suspend fun deletePlayer(teamId: Int, playerId: Int): DataResult<Unit>
}