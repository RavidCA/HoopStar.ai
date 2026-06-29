package com.starhoop.hoopstarai.data.remote

import com.starhoop.hoopstar.data.remote.dto.AuthResponse
import com.starhoop.hoopstar.data.remote.dto.LoginRequest
import com.starhoop.hoopstar.data.remote.dto.MeResponse
import com.starhoop.hoopstar.data.remote.dto.RegisterRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {
    @POST("api/auth/register")
    suspend fun register(@Body body: RegisterRequest): AuthResponse

    @POST("api/auth/login")
    suspend fun login(@Body body: LoginRequest): AuthResponse

    @GET("api/auth/me")
    suspend fun me(): MeResponse
}