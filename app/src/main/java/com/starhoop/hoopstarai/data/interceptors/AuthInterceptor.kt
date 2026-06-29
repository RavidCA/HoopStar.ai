package com.starhoop.hoopstar.data.remote.interceptors

import com.starhoop.hoopstar.data.local.TokenStore
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/** מצרף "Authorization: Bearer <token>" כשקיים token. */
class AuthInterceptor @Inject constructor(
    private val tokenStore: TokenStore
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenStore.getToken()
        val request = if (!token.isNullOrBlank()) {
            chain.request().newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }
        return chain.proceed(request)
    }
}