package com.starhoop.hoopstar.domain.usecase

import com.starhoop.hoopstar.core.DataResult
import com.starhoop.hoopstarai.domain.model.Coach
import com.starhoop.hoopstar.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val repo: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): DataResult<Coach> =
        repo.login(email.trim(), password)
}

class RegisterUseCase @Inject constructor(private val repo: AuthRepository) {
    suspend operator fun invoke(
        email: String,
        password: String,
        displayName: String
    ): DataResult<Coach> = repo.register(email.trim(), password, displayName.trim())
}

class FetchMeUseCase @Inject constructor(private val repo: AuthRepository) {
    suspend operator fun invoke(): DataResult<Coach> = repo.fetchMe()
}