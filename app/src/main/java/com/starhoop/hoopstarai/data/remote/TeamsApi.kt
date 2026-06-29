package com.starhoop.hoopstar.data.remote

import com.starhoop.hoopstar.data.remote.dto.CreatePlayerRequest
import com.starhoop.hoopstar.data.remote.dto.CreateTeamRequest
import com.starhoop.hoopstar.data.remote.dto.PlayerDto
import com.starhoop.hoopstar.data.remote.dto.TeamDto
import com.starhoop.hoopstar.data.remote.dto.UpdatePlayerRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface TeamsApi {
    @GET("api/teams")
    suspend fun getMyTeams(): List<TeamDto>

    @POST("api/teams")
    suspend fun createTeam(@Body body: CreateTeamRequest): TeamDto

    @GET("api/teams/{teamId}")
    suspend fun getTeam(@Path("teamId") teamId: Int): TeamDto

    @GET("api/teams/{teamId}/players")
    suspend fun getPlayers(@Path("teamId") teamId: Int): List<PlayerDto>

    @POST("api/teams/{teamId}/players")
    suspend fun addPlayer(
        @Path("teamId") teamId: Int,
        @Body body: CreatePlayerRequest
    ): PlayerDto

    @PUT("api/teams/{teamId}/players/{playerId}")
    suspend fun updatePlayer(
        @Path("teamId") teamId: Int,
        @Path("playerId") playerId: Int,
        @Body body: UpdatePlayerRequest
    ): PlayerDto

    @DELETE("api/teams/{teamId}/players/{playerId}")
    suspend fun deletePlayer(
        @Path("teamId") teamId: Int,
        @Path("playerId") playerId: Int
    )
}