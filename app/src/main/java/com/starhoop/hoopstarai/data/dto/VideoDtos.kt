package com.starhoop.hoopstar.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class JobDto(
    @Json(name = "job_id") val jobId: Int,
    @Json(name = "team_id") val teamId: Int?,
    val status: String,
    @Json(name = "source_filename") val sourceFilename: String?,
    @Json(name = "tracker_name") val trackerName: String?,
    @Json(name = "model_name") val modelName: String?,
    @Json(name = "total_frames") val totalFrames: Int?,
    @Json(name = "processed_frames") val processedFrames: Int?,
    @Json(name = "progress_percent") val progressPercent: Double?,
    @Json(name = "processing_duration_sec") val processingDurationSec: Double?,
    @Json(name = "throughput_fps") val throughputFps: Double?,
    @Json(name = "error_message") val errorMessage: String?,
    @Json(name = "created_at") val createdAt: String?,
    @Json(name = "updated_at") val updatedAt: String?
)

@JsonClass(generateAdapter = true)
data class UploadResponse(
    @Json(name = "job_id") val jobId: Int,
    val status: String?,
    val message: String?
)

@JsonClass(generateAdapter = true)
data class CancelResponse(
    @Json(name = "job_id") val jobId: Int,
    val status: String?,
    val message: String?
)