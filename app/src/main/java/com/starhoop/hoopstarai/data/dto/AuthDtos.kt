package com.starhoop.hoopstar.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegisterRequest(
    val email: String,
    val password: String,
    @Json(name = "display_name") val displayName: String
)

@JsonClass(generateAdapter = true)
data class LoginRequest(
    val email: String,
    val password: String
)

/** התשובה ל-register/login זהה. */
@JsonClass(generateAdapter = true)
data class AuthResponse(
    @Json(name = "access_token") val accessToken: String,
    @Json(name = "token_type") val tokenType: String?,
    @Json(name = "coach_id") val coachId: Int,
    @Json(name = "display_name") val displayName: String?,
    val email: String?
)

@JsonClass(generateAdapter = true)
data class MeResponse(
    @Json(name = "coach_id") val coachId: Int,
    @Json(name = "display_name") val displayName: String?,
    val email: String?,
    @Json(name = "created_at") val createdAt: String?
)