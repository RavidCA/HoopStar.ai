package com.starhoop.hoopstar.data.repository

import com.starhoop.hoopstar.core.DataResult
import com.starhoop.hoopstar.data.local.TokenStore
import com.starhoop.hoopstarai.data.remote.AuthApi
import com.starhoop.hoopstar.data.remote.dto.LoginRequest
import com.starhoop.hoopstar.data.remote.dto.RegisterRequest
import com.starhoop.hoopstar.data.remote.safeApiCall
import com.starhoop.hoopstarai.domain.model.Coach
import com.starhoop.hoopstar.domain.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val tokenStore: TokenStore
) : AuthRepository {

    override suspend fun login(email: String, password: String): DataResult<Coach> =
        withContext(Dispatchers.IO) {
            when (val res = safeApiCall("Login failed.") { api.login(LoginRequest(email, password)) }) {
                is DataResult.Success -> {
                    val d = res.data
                    tokenStore.saveToken(d.accessToken)
                    tokenStore.saveCoach(d.coachId, d.displayName, d.email)
                    DataResult.Success(
                        Coach(d.coachId, d.displayName ?: "", d.email ?: email)
                    )
                }
                is DataResult.Error -> res
            }
        }

    override suspend fun register(
        email: String,
        password: String,
        displayName: String
    ): DataResult<Coach> = withContext(Dispatchers.IO) {
        when (val res = safeApiCall("Registration failed.") {
            api.register(RegisterRequest(email, password, displayName))
        }) {
            is DataResult.Success -> {
                val d = res.data
                tokenStore.saveToken(d.accessToken)
                tokenStore.saveCoach(d.coachId, d.displayName, d.email)
                DataResult.Success(Coach(d.coachId, d.displayName ?: displayName, d.email ?: email))
            }
            is DataResult.Error -> res
        }
    }

    override suspend fun fetchMe(): DataResult<Coach> = withContext(Dispatchers.IO) {
        when (val res = safeApiCall("Failed to load profile.") { api.me() }) {
            is DataResult.Success -> {
                val d = res.data
                tokenStore.saveCoach(d.coachId, d.displayName, d.email)
                DataResult.Success(Coach(d.coachId, d.displayName ?: "", d.email ?: ""))
            }
            is DataResult.Error -> res
        }
    }

    override fun logout() = tokenStore.clear()

    override fun isLoggedIn(): Boolean = tokenStore.isLoggedIn()
}