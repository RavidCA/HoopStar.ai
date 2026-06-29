package com.starhoop.hoopstar.domain.usecase

import com.starhoop.hoopstar.core.DataResult
import com.starhoop.hoopstar.domain.model.MappingResult
import com.starhoop.hoopstar.domain.repository.MappingRepository
import javax.inject.Inject

class AutoMapPlayersUseCase @Inject constructor(private val repo: MappingRepository) {
    suspend operator fun invoke(jobId: Int, teamId: Int): DataResult<MappingResult> =
        repo.autoMap(jobId, teamId)
}