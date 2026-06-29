package com.starhoop.hoopstar.data.remote

import com.starhoop.hoopstar.data.remote.dto.MappingResponseDto
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MappingApi {
    // שים לב: קו תחתון בנתיב, team_id הוא query param
    @POST("api/videos/{jobId}/player_mapping/auto")
    suspend fun autoMap(
        @Path("jobId") jobId: Int,
        @Query("team_id") teamId: Int
    ): MappingResponseDto
}