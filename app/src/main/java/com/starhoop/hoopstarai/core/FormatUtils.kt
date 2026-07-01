package com.starhoop.hoopstar.core

import com.starhoop.hoopstar.domain.model.JobStatus

fun jobStatusLabel(status: JobStatus): String = when (status) {
    JobStatus.PENDING -> "Pending"
    JobStatus.PROCESSING -> "Processing"
    JobStatus.COMPLETED -> "Completed"
    JobStatus.FAILED -> "Failed"
    JobStatus.CANCELED -> "Canceled"
    JobStatus.UNKNOWN -> "Unknown"
}

fun formatDuration(seconds: Double?): String {
    if (seconds == null || seconds <= 0) return "—"
    val total = seconds.toInt()
    val m = total / 60
    val s = total % 60
    return if (m > 0) "${m}m ${s}s" else "${s}s"
}