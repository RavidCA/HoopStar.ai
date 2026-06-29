package com.starhoop.hoopstarai.domain.repository

import com.starhoop.hoopstar.core.DataResult
import com.starhoop.hoopstar.domain.model.Job

interface VideosRepository {
    suspend fun uploadVideo(teamId: Int, fileUri: String): DataResult<Int> // מחזיר job_id
    suspend fun getJob(jobId: Int): DataResult<Job>
    suspend fun listJobs(teamId: Int?): DataResult<List<Job>>
    suspend fun cancelJob(jobId: Int): DataResult<Job>
}