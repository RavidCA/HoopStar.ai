package com.starhoop.hoopstar.data.repository

import com.starhoop.hoopstar.core.DataResult
import com.starhoop.hoopstar.data.remote.HighlightsApi
import com.starhoop.hoopstar.data.remote.dto.ComposeResponseDto
import com.starhoop.hoopstar.data.remote.dto.ExtractResponseDto
import com.starhoop.hoopstar.data.remote.safeApiCall
import com.starhoop.hoopstar.domain.model.Clip
import com.starhoop.hoopstar.domain.model.ComposedReel
import com.starhoop.hoopstar.domain.model.ExtractedReel
import com.starhoop.hoopstar.domain.repository.HighlightsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HighlightsRepositoryImpl @Inject constructor(
    private val api: HighlightsApi
) : HighlightsRepository {

    private fun ExtractResponseDto.toDomain(): ExtractedReel = ExtractedReel(
        reelId = reelId ?: -1,
        jobId = jobId ?: -1,
        playerId = playerId,
        clipCount = clipCount ?: (clips?.size ?: 0),
        totalDurationSec = totalDurationSec,
        downloadUrl = downloadUrl,
        clips = (clips ?: emptyList()).mapIndexed { idx, c ->
            Clip(
                clipId = c.clipId,
                orderIndex = c.orderIndex ?: idx,
                eventType = c.eventType,
                made = c.made,
                startSec = c.startTimestampSec,
                endSec = c.endTimestampSec,
                confidence = c.confidence,
                downloadUrl = c.downloadUrl
            )
        }.sortedBy { it.orderIndex }
    )

    private fun ComposeResponseDto.toDomain(): ComposedReel = ComposedReel(
        composedReelId = composedReelId,
        reelId = reelId,
        jobId = jobId,
        playerId = playerId,
        aspectRatio = aspectRatio,
        musicTrack = musicTrack,
        totalDurationSec = totalDurationSec,
        downloadUrl = downloadUrl
    )

    override suspend fun extract(jobId: Int, playerId: Int?): DataResult<ExtractedReel> =
        withContext(Dispatchers.IO) {
            // source="annotated" כברירת מחדל (יש מסגרות זיהוי על השחקנים)
            when (val r = safeApiCall("יצירת ההיילייטים נכשלה.") {
                api.extract(jobId, playerId, source = "annotated")
            }) {
                is DataResult.Success -> {
                    val reel = r.data.toDomain()
                    if (reel.reelId == -1) {
                        // מקרה נדיר: 200 אבל בלי reel_id
                        DataResult.Error("לא נמצאו היילייטים לשחקן הזה.", 422)
                    } else DataResult.Success(reel)
                }
                is DataResult.Error -> r // כולל 422 = אין קליפים, מטופל ב-ViewModel
            }
        }

    override suspend fun compose(
        jobId: Int, reelId: Int,
        aspectRatio: String?, musicTrack: String?,
        includeIntro: Boolean, includeStats: Boolean, includeWatermark: Boolean
    ): DataResult<ComposedReel> = withContext(Dispatchers.IO) {
        when (val r = safeApiCall("הרכבת הריל נכשלה.") {
            api.compose(jobId, reelId, aspectRatio, musicTrack, includeIntro, includeStats, includeWatermark)
        }) {
            is DataResult.Success -> DataResult.Success(r.data.toDomain())
            is DataResult.Error -> r
        }
    }
}