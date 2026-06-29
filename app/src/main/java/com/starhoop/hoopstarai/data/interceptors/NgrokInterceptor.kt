package com.starhoop.hoopstar.data.remote.interceptors

import okhttp3.Interceptor
import okhttp3.Response

/** מוסיף את ה-header שמדלג על דף האזהרה של ngrok בכל בקשה. */
class NgrokInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .header("ngrok-skip-browser-warning", "true")
            .build()
        return chain.proceed(request)
    }
}