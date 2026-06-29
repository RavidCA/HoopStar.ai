package com.starhoop.hoopstar.data.remote

import com.starhoop.hoopstar.data.remote.dto.ComposeResponseDto
import com.starhoop.hoopstar.data.remote.dto.ExtractResponseDto
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface HighlightsApi {
    @POST("api/videos/{jobId}/highlights/extract")
    suspend fun extract(
        @Path("jobId") jobId: Int,
        @Query("player_id") playerId: Int?,
        @Query("source") source: String? = null,
        @Query("max_clips") maxClips: Int? = null,
        @Query("min_confidence") minConfidence: Double? = null
    ): ExtractResponseDto

    @POST("api/videos/{jobId}/highlights/{reelId}/compose")
    suspend fun compose(
        @Path("jobId") jobId: Int,
        @Path("reelId") reelId: Int,
        @Query("aspect_ratio") aspectRatio: String? = null,
        @Query("music_track") musicTrack: String? = null,
        @Query("include_intro") includeIntro: Boolean? = null,
        @Query("include_stats") includeStats: Boolean? = null,
        @Query("include_watermark") includeWatermark: Boolean? = null
    ): ComposeResponseDto
}