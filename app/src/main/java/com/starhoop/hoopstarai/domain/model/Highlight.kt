package com.starhoop.hoopstar.domain.model

data class Clip(
    val clipId: Int?,
    val orderIndex: Int,
    val eventType: String?,
    val made: Boolean?,
    val startSec: Double?,
    val endSec: Double?,
    val confidence: Double?,
    val downloadUrl: String?
)

data class ExtractedReel(
    val reelId: Int,
    val jobId: Int,
    val playerId: Int?,
    val clipCount: Int,
    val totalDurationSec: Double?,
    val downloadUrl: String?,
    val clips: List<Clip>
)

data class ComposedReel(
    val composedReelId: Int?,
    val reelId: Int?,
    val jobId: Int?,
    val playerId: Int?,
    val aspectRatio: String?,
    val musicTrack: String?,
    val totalDurationSec: Double?,
    val downloadUrl: String?
)

/** מוזיקות זמינות מה-contract (אפשר גם null = ברירת מחדל). */
object MusicTracks {
    const val TRACK_1 = "b57402a47ea94e1db89ebf3dad133a78_starhoop_ai.mp3"
    const val TRACK_2 = "fa65129e2db740148c74c5410b3d82ef_starhoop_ai.mp3"
}