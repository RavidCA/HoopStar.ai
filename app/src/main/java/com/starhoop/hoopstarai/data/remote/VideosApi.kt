package com.starhoop.hoopstar.data.remote

import com.starhoop.hoopstar.data.remote.dto.CancelResponse
import com.starhoop.hoopstar.data.remote.dto.JobDto
import com.starhoop.hoopstar.data.remote.dto.UploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface VideosApi {
    @Multipart
    @POST("api/videos/upload")
    suspend fun uploadVideo(
        @Part file: MultipartBody.Part,
        @Part("team_id") teamId: RequestBody
    ): UploadResponse

    @GET("api/videos/{jobId}")
    suspend fun getJob(@Path("jobId") jobId: Int): JobDto

    @GET("api/videos")
    suspend fun listJobs(
        @Query("team_id") teamId: Int? = null,
        @Query("limit") limit: Int = 50
    ): List<JobDto>

    @POST("api/videos/{jobId}/cancel")
    suspend fun cancelJob(@Path("jobId") jobId: Int): CancelResponse
}