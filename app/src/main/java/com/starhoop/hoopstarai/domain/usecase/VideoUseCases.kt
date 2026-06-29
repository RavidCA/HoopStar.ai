package com.starhoop.hoopstar.domain.usecase

import com.starhoop.hoopstar.core.DataResult
import com.starhoop.hoopstar.domain.model.Job
import com.starhoop.hoopstarai.domain.repository.VideosRepository
import javax.inject.Inject

class UploadVideoUseCase @Inject constructor(private val repo: VideosRepository) {
    suspend operator fun invoke(teamId: Int, fileUri: String): DataResult<Int> =
        repo.uploadVideo(teamId, fileUri)
}

class GetJobUseCase @Inject constructor(private val repo: VideosRepository) {
    suspend operator fun invoke(jobId: Int): DataResult<Job> = repo.getJob(jobId)
}

class ListJobsUseCase @Inject constructor(private val repo: VideosRepository) {
    suspend operator fun invoke(teamId: Int?): DataResult<List<Job>> = repo.listJobs(teamId)
}

class CancelJobUseCase @Inject constructor(private val repo: VideosRepository) {
    suspend operator fun invoke(jobId: Int): DataResult<Job> = repo.cancelJob(jobId)
}