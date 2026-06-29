package com.starhoop.hoopstar.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ClipDto(
    @Json(name = "clip_id") val clipId: Int?,
    @Json(name = "order_index") val orderIndex: Int?,
    @Json(name = "event_type") val eventType: String?,
    val source: String?,
    @Json(name = "track_id") val trackId: Int?,
    @Json(name = "mapped_player_id") val mappedPlayerId: Int?,
    @Json(name = "start_frame") val startFrame: Int?,
    @Json(name = "end_frame") val endFrame: Int?,
    @Json(name = "start_timestamp_sec") val startTimestampSec: Double?,
    @Json(name = "end_timestamp_sec") val endTimestampSec: Double?,
    val made: Boolean?,
    val score: Double?,
    val confidence: Double?,
    @Json(name = "clip_filename") val clipFilename: String?,
    @Json(name = "download_url") val downloadUrl: String?
)

/** reel_id חייב להיות nullable — מקרה "אין קליפים" חוזר כ-422 בלי reel_id. */
@JsonClass(generateAdapter = true)
data class ExtractResponseDto(
    @Json(name = "reel_id") val reelId: Int?,
    @Json(name = "job_id") val jobId: Int?,
    @Json(name = "team_id") val teamId: Int?,
    val status: String?,
    val scope: String?,
    @Json(name = "source_mode") val sourceMode: String?,
    @Json(name = "player_id") val playerId: Int?,
    @Json(name = "clip_count") val clipCount: Int?,
    @Json(name = "total_duration_sec") val totalDurationSec: Double?,
    @Json(name = "output_filename") val outputFilename: String?,
    @Json(name = "download_url") val downloadUrl: String?,
    @Json(name = "created_at") val createdAt: String?,
    val clips: List<ClipDto>?
)

@JsonClass(generateAdapter = true)
data class ComposeResponseDto(
    @Json(name = "composed_reel_id") val composedReelId: Int?,
    @Json(name = "reel_id") val reelId: Int?,
    @Json(name = "job_id") val jobId: Int?,
    @Json(name = "team_id") val teamId: Int?,
    @Json(name = "player_id") val playerId: Int?,
    val status: String?,
    @Json(name = "aspect_ratio") val aspectRatio: String?,
    @Json(name = "music_track") val musicTrack: String?,
    @Json(name = "has_intro") val hasIntro: Boolean?,
    @Json(name = "has_stats") val hasStats: Boolean?,
    @Json(name = "has_watermark") val hasWatermark: Boolean?,
    @Json(name = "total_duration_sec") val totalDurationSec: Double?,
    @Json(name = "output_filename") val outputFilename: String?,
    @Json(name = "download_url") val downloadUrl: String?,
    @Json(name = "created_at") val createdAt: String?
)