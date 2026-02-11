package com.itd.app.data.api

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val builder = originalRequest.newBuilder()

        val token = runBlocking { tokenManager.getAccessToken() }
        val cookie = runBlocking { tokenManager.getSessionCookie() }

        if (token != null) {
            builder.addHeader("Authorization", "Bearer $token")
        }

        if (cookie != null) {
            builder.addHeader("Cookie", "is_auth=1; $cookie")
        } else if (token != null) {
            builder.addHeader("Cookie", "is_auth=1")
        }

        builder.addHeader("Accept", "application/json")
        builder.addHeader("Content-Type", "application/json")

        return chain.proceed(builder.build())
    }
}
