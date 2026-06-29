package com.starhoop.hoopstar.domain.repository

import com.starhoop.hoopstar.core.DataResult
import com.starhoop.hoopstar.domain.model.ComposedReel
import com.starhoop.hoopstar.domain.model.ExtractedReel

interface HighlightsRepository {
    /** Error עם code=422 פירושו "אין קליפים לשחקן הזה". */
    suspend fun extract(jobId: Int, playerId: Int?): DataResult<ExtractedReel>
    suspend fun compose(
        jobId: Int, reelId: Int,
        aspectRatio: String?, musicTrack: String?,
        includeIntro: Boolean, includeStats: Boolean, includeWatermark: Boolean
    ): DataResult<ComposedReel>
}