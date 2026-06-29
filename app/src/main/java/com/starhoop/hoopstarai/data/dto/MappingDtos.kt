package com.starhoop.hoopstar.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MappingItemDto(
    @Json(name = "track_id") val trackId: Int?,
    @Json(name = "detected_jersey") val detectedJersey: Int?,
    @Json(name = "frame_count") val frameCount: Int?,
    @Json(name = "confidence_mean") val confidenceMean: Double?,
    @Json(name = "confidence_max") val confidenceMax: Double?,
    @Json(name = "suggested_player_id") val suggestedPlayerId: Int?,
    @Json(name = "suggested_player_name") val suggestedPlayerName: String?,
    @Json(name = "match_rating") val matchRating: String?
)

@JsonClass(generateAdapter = true)
data class MappingResponseDto(
    @Json(name = "job_id") val jobId: Int,
    @Json(name = "team_id") val teamId: Int,
    @Json(name = "total_mappings") val totalMappings: Int?,
    @Json(name = "high_confidence_count") val highConfidenceCount: Int?,
    @Json(name = "medium_confidence_count") val mediumConfidenceCount: Int?,
    @Json(name = "low_confidence_count") val lowConfidenceCount: Int?,
    @Json(name = "no_match_count") val noMatchCount: Int?,
    val mappings: List<MappingItemDto>?
)