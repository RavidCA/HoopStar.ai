package com.starhoop.hoopstar.domain.model

enum class MatchRating(val raw: String) {
    HIGH("high"),
    MEDIUM("medium"),
    LOW("low"),
    NO_MATCH("no_match"),
    UNKNOWN("unknown");

    companion object {
        fun from(raw: String?): MatchRating =
            entries.firstOrNull { it.raw.equals(raw, ignoreCase = true) } ?: UNKNOWN
    }
}

data class MappingItem(
    val trackId: Int?,
    val detectedJersey: Int?,
    val frameCount: Int?,
    val confidenceMean: Double?,
    val confidenceMax: Double?,
    val suggestedPlayerId: Int?,
    val suggestedPlayerName: String?,
    val rating: MatchRating
)

data class MappingResult(
    val jobId: Int,
    val teamId: Int,
    val total: Int,
    val highCount: Int,
    val mediumCount: Int,
    val lowCount: Int,
    val noMatchCount: Int,
    val items: List<MappingItem>
)