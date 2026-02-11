package com.itd.app.data.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("turnstileToken") val turnstileToken: String? = null
)

data class RegisterRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("turnstileToken") val turnstileToken: String? = null
)

data class AuthResponse(
    @SerializedName("accessToken") val accessToken: String? = null,
    @SerializedName("requiresVerification") val requiresVerification: Boolean = false,
    @SerializedName("email") val email: String? = null,
    @SerializedName("flowToken") val flowToken: String? = null
)

data class VerifyOtpRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("otp") val otp: String,
    @SerializedName("flowToken") val flowToken: String
)

data class ResendOtpRequest(
    @SerializedName("email") val email: String,
    @SerializedName("flowToken") val flowToken: String
)

data class ProfileResponse(
    @SerializedName("authenticated") val authenticated: Boolean,
    @SerializedName("user") val user: User?,
    @SerializedName("banned") val banned: Boolean = false,
    @SerializedName("profileRequired") val profileRequired: Boolean = false
)
