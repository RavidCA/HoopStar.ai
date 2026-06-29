package com.starhoop.hoopstar.data.repository

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.starhoop.hoopstar.core.DataResult
import com.starhoop.hoopstar.data.remote.VideosApi
import com.starhoop.hoopstar.data.remote.dto.JobDto
import com.starhoop.hoopstar.data.remote.dto.UploadResponse
import com.starhoop.hoopstar.data.remote.safeApiCall
import com.starhoop.hoopstar.domain.model.Job
import com.starhoop.hoopstar.domain.model.JobStatus
import com.starhoop.hoopstarai.domain.repository.VideosRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideosRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val api: VideosApi
) : VideosRepository {

    private fun JobDto.toDomain() = Job(
        jobId = jobId,
        teamId = teamId,
        status = JobStatus.from(status),
        sourceFilename = sourceFilename,
        totalFrames = totalFrames,
        processedFrames = processedFrames,
        progressPercent = progressPercent,
        throughputFps = throughputFps,
        processingDurationSec = processingDurationSec,
        errorMessage = errorMessage,
        createdAt = createdAt
    )

    override suspend fun uploadVideo(teamId: Int, fileUri: String): DataResult<Int> =
        withContext(Dispatchers.IO) {
            // מעתיק את הוידאו לקובץ זמני כדי לקבל אורך/שם אמינים ל-multipart
            val tempFile = try {
                copyUriToTemp(Uri.parse(fileUri))
            } catch (e: Exception) {
                return@withContext DataResult.Error("לא ניתן לקרוא את קובץ הוידאו.")
            }

            val result = safeApiCall<UploadResponse>("ההעלאה נכשלה.") {
                val mediaType = "video/*".toMediaTypeOrNull()
                val filePart = MultipartBody.Part.createFormData(
                    name = "file",
                    filename = tempFile.name,
                    body = tempFile.asRequestBody(mediaType)
                )
                val teamIdPart: RequestBody = teamId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                api.uploadVideo(filePart, teamIdPart)
            }
            tempFile.delete()

            when (result) {
                is DataResult.Success -> DataResult.Success(result.data.jobId)
                is DataResult.Error -> result
            }
        }

    override suspend fun getJob(jobId: Int): DataResult<Job> = withContext(Dispatchers.IO) {
        when (val r = safeApiCall("טעינת הסטטוס נכשלה.") { api.getJob(jobId) }) {
            is DataResult.Success -> DataResult.Success(r.data.toDomain())
            is DataResult.Error -> r
        }
    }

    override suspend fun listJobs(teamId: Int?): DataResult<List<Job>> = withContext(Dispatchers.IO) {
        when (val r = safeApiCall("טעינת המשחקים נכשלה.") { api.listJobs(teamId) }) {
            is DataResult.Success -> DataResult.Success(r.data.map { it.toDomain() })
            is DataResult.Error -> r
        }
    }

    override suspend fun cancelJob(jobId: Int): DataResult<Job> = withContext(Dispatchers.IO) {
        when (val r = safeApiCall("הביטול נכשל.") { api.cancelJob(jobId) }) {
            is DataResult.Success -> getJob(jobId) // מביא את הסטטוס המעודכן המלא
            is DataResult.Error -> r
        }
    }

    private fun copyUriToTemp(uri: Uri): File {
        val resolver = context.contentResolver
        val name = queryFileName(uri) ?: "upload_${System.currentTimeMillis()}.mp4"
        val temp = File(context.cacheDir, name)
        resolver.openInputStream(uri)?.use { input ->
            FileOutputStream(temp).use { output -> input.copyTo(output) }
        } ?: throw IllegalStateException("Cannot open stream")
        return temp
    }

    private fun queryFileName(uri: Uri): String? {
        return context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (idx >= 0 && cursor.moveToFirst()) cursor.getString(idx) else null
        }
    }
}