package com.starhoop.hoopstar.data.repository

import com.starhoop.hoopstar.core.DataResult
import com.starhoop.hoopstar.data.remote.MappingApi
import com.starhoop.hoopstar.data.remote.dto.MappingResponseDto
import com.starhoop.hoopstar.data.remote.safeApiCall
import com.starhoop.hoopstar.domain.model.MappingItem
import com.starhoop.hoopstar.domain.model.MappingResult
import com.starhoop.hoopstar.domain.model.MatchRating
import com.starhoop.hoopstar.domain.repository.MappingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MappingRepositoryImpl @Inject constructor(
    private val api: MappingApi
) : MappingRepository {

    private fun MappingResponseDto.toDomain(): MappingResult {
        // מיון: high -> medium -> low -> no_match, ובתוך כל קבוצה לפי frame_count יורד
        val order = mapOf(
            MatchRating.HIGH to 0, MatchRating.MEDIUM to 1,
            MatchRating.LOW to 2, MatchRating.NO_MATCH to 3, MatchRating.UNKNOWN to 4
        )
        val items = (mappings ?: emptyList()).map {
            MappingItem(
                trackId = it.trackId,
                detectedJersey = it.detectedJersey,
                frameCount = it.frameCount,
                confidenceMean = it.confidenceMean,
                confidenceMax = it.confidenceMax,
                suggestedPlayerId = it.suggestedPlayerId,
                suggestedPlayerName = it.suggestedPlayerName,
                rating = MatchRating.from(it.matchRating)
            )
        }.sortedWith(
            compareBy({ order[it.rating] ?: 4 }, { -(it.frameCount ?: 0) })
        )
        return MappingResult(
            jobId = jobId,
            teamId = teamId,
            total = totalMappings ?: items.size,
            highCount = highConfidenceCount ?: 0,
            mediumCount = mediumConfidenceCount ?: 0,
            lowCount = lowConfidenceCount ?: 0,
            noMatchCount = noMatchCount ?: 0,
            items = items
        )
    }

    override suspend fun autoMap(jobId: Int, teamId: Int): DataResult<MappingResult> =
        withContext(Dispatchers.IO) {
            when (val r = safeApiCall("Player mapping failed.") { api.autoMap(jobId, teamId) }) {
                is DataResult.Success -> DataResult.Success(r.data.toDomain())
                is DataResult.Error -> r
            }
        }
}