package com.itd.app.data.repository

import com.itd.app.data.api.ITDApiService
import com.itd.app.data.api.TokenManager
import com.itd.app.data.model.*
import javax.inject.Inject
import javax.inject.Singleton

data class SignInResult(
    val success: Boolean,
    val requiresOtp: Boolean = false,
    val flowToken: String? = null,
    val email: String? = null,
    val error: String? = null,
    val user: User? = null
)

@Singleton
class AuthRepository @Inject constructor(
    private val api: ITDApiService,
    private val tokenManager: TokenManager
) {
    suspend fun signIn(email: String, password: String, turnstileToken: String): SignInResult {
        return try {
            val response = api.signIn(LoginRequest(email, password, turnstileToken))
            if (response.isSuccessful) {
                val body = response.body()!!
                if (body.requiresVerification) {
                    SignInResult(
                        success = true,
                        requiresOtp = true,
                        flowToken = body.flowToken,
                        email = body.email ?: email
                    )
                } else if (body.accessToken != null) {
                    tokenManager.saveAccessToken(body.accessToken)
                    val profile = fetchAndSaveProfile()
                    SignInResult(success = true, user = profile)
                } else {
                    SignInResult(success = false, error = "Неожиданный ответ сервера")
                }
            } else {
                val code = response.code()
                val msg = when (code) {
                    401 -> "Неверный email или пароль"
                    403 -> "Аккаунт деактивирован"
                    429 -> "Слишком много попыток. Попробуйте позже"
                    else -> "Ошибка входа: $code"
                }
                SignInResult(success = false, error = msg)
            }
        } catch (e: Exception) {
            SignInResult(success = false, error = e.message ?: "Ошибка сети")
        }
    }

    suspend fun verifyOtp(
        email: String,
        password: String,
        otp: String,
        flowToken: String
    ): SignInResult {
        return try {
            val response = api.verifyOtp(VerifyOtpRequest(email, password, otp, flowToken))
            if (response.isSuccessful) {
                val body = response.body()!!
                if (body.accessToken != null) {
                    tokenManager.saveAccessToken(body.accessToken)
                    val profile = fetchAndSaveProfile()
                    SignInResult(success = true, user = profile)
                } else {
                    SignInResult(success = false, error = "Неожиданный ответ")
                }
            } else {
                SignInResult(success = false, error = "Неверный код подтверждения")
            }
        } catch (e: Exception) {
            SignInResult(success = false, error = e.message ?: "Ошибка сети")
        }
    }

    suspend fun resendOtp(email: String, flowToken: String): Result<Unit> {
        return try {
            val response = api.resendOtp(ResendOtpRequest(email, flowToken))
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Не удалось отправить код"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signUp(email: String, password: String, turnstileToken: String): SignInResult {
        return try {
            val response = api.signUp(RegisterRequest(email, password, turnstileToken))
            if (response.isSuccessful) {
                val body = response.body()!!
                if (body.requiresVerification) {
                    SignInResult(
                        success = true,
                        requiresOtp = true,
                        flowToken = body.flowToken,
                        email = body.email ?: email
                    )
                } else if (body.accessToken != null) {
                    tokenManager.saveAccessToken(body.accessToken)
                    val profile = fetchAndSaveProfile()
                    SignInResult(success = true, user = profile)
                } else {
                    SignInResult(success = false, error = "Неожиданный ответ")
                }
            } else {
                val code = response.code()
                val msg = when (code) {
                    409 -> "Пользователь с таким email уже существует"
                    422 -> "Проверьте введённые данные"
                    else -> "Ошибка регистрации: $code"
                }
                SignInResult(success = false, error = msg)
            }
        } catch (e: Exception) {
            SignInResult(success = false, error = e.message ?: "Ошибка сети")
        }
    }

    private suspend fun fetchAndSaveProfile(): User? {
        return try {
            val profileResponse = api.getProfile()
            if (profileResponse.isSuccessful) {
                val user = profileResponse.body()?.user
                user?.username?.let { tokenManager.saveUsername(it) }
                user
            } else null
        } catch (_: Exception) { null }
    }

    suspend fun refreshToken(): Result<String> {
        return try {
            val response = api.refreshToken()
            if (response.isSuccessful) {
                val token = response.body()?.accessToken
                if (token != null) {
                    tokenManager.saveAccessToken(token)
                    Result.success(token)
                } else {
                    Result.failure(Exception("Нет токена в ответе"))
                }
            } else {
                Result.failure(Exception("Ошибка обновления токена"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProfile(): Result<ProfileResponse> {
        return try {
            val response = api.getProfile()
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Ошибка получения профиля: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() {
        try { api.logout() } catch (_: Exception) {}
        tokenManager.clearAll()
    }

    suspend fun isLoggedIn(): Boolean = tokenManager.isLoggedIn()

    suspend fun getUsername(): String? = tokenManager.getUsername()
}
