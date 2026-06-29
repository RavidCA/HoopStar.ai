package com.starhoop.hoopstar.domain.repository

import com.starhoop.hoopstar.core.DataResult
import com.starhoop.hoopstarai.domain.model.Coach

interface AuthRepository {
    suspend fun login(email: String, password: String): DataResult<Coach>
    suspend fun register(email: String, password: String, displayName: String): DataResult<Coach>
    suspend fun fetchMe(): DataResult<Coach>
    fun logout()
    fun isLoggedIn(): Boolean
}