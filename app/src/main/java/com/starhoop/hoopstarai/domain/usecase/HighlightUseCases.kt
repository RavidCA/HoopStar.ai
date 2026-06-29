package com.starhoop.hoopstar.domain.usecase

import com.starhoop.hoopstar.core.DataResult
import com.starhoop.hoopstar.domain.model.ComposedReel
import com.starhoop.hoopstar.domain.model.ExtractedReel
import com.starhoop.hoopstar.domain.repository.HighlightsRepository
import javax.inject.Inject

class ExtractReelUseCase @Inject constructor(private val repo: HighlightsRepository) {
    suspend operator fun invoke(jobId: Int, playerId: Int?): DataResult<ExtractedReel> =
        repo.extract(jobId, playerId)
}

class ComposeReelUseCase @Inject constructor(private val repo: HighlightsRepository) {
    suspend operator fun invoke(
        jobId: Int, reelId: Int,
        aspectRatio: String? = "16:9", musicTrack: String? = null,
        includeIntro: Boolean = true, includeStats: Boolean = false, includeWatermark: Boolean = true
    ): DataResult<ComposedReel> =
        repo.compose(jobId, reelId, aspectRatio, musicTrack, includeIntro, includeStats, includeWatermark)
}