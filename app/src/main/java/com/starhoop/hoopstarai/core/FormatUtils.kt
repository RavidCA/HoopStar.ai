package com.starhoop.hoopstar.core

import com.starhoop.hoopstar.domain.model.JobStatus

fun jobStatusLabel(status: JobStatus): String = when (status) {
    JobStatus.PENDING -> "ממתין"
    JobStatus.PROCESSING -> "מעבד"
    JobStatus.COMPLETED -> "הושלם"
    JobStatus.FAILED -> "נכשל"
    JobStatus.CANCELED -> "בוטל"
    JobStatus.UNKNOWN -> "לא ידוע"
}

fun formatDuration(seconds: Double?): String {
    if (seconds == null || seconds <= 0) return "—"
    val total = seconds.toInt()
    val m = total / 60
    val s = total % 60
    return if (m > 0) "${m}ד׳ ${s}ש׳" else "${s}ש׳"
}