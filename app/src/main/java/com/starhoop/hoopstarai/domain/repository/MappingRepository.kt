package com.starhoop.hoopstar.domain.repository

import com.starhoop.hoopstar.core.DataResult
import com.starhoop.hoopstar.domain.model.MappingResult

interface MappingRepository {
    suspend fun autoMap(jobId: Int, teamId: Int): DataResult<MappingResult>
}