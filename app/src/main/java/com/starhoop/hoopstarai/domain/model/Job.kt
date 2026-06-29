package com.starhoop.hoopstar.domain.model

enum class JobStatus(val raw: String) {
    PENDING("pending"),
    PROCESSING("processing"),
    COMPLETED("completed"),
    FAILED("failed"),
    CANCELED("canceled"),
    UNKNOWN("unknown");

    val isTerminal get() = this == COMPLETED || this == FAILED || this == CANCELED

    companion object {
        fun from(raw: String?): JobStatus =
            entries.firstOrNull { it.raw.equals(raw, ignoreCase = true) } ?: UNKNOWN
    }
}

data class Job(
    val jobId: Int,
    val teamId: Int?,
    val status: JobStatus,
    val sourceFilename: String?,
    val totalFrames: Int?,
    val processedFrames: Int?,
    val progressPercent: Double?,
    val throughputFps: Double?,
    val processingDurationSec: Double?,
    val errorMessage: String?,
    val createdAt: String?
)