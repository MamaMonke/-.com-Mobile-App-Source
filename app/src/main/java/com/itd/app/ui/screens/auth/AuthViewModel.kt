package com.itd.app.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itd.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val showOtp: Boolean = false,
    val otpEmail: String = "",
    val otpFlowToken: String = "",
    val otpPassword: String = "",
    val otpResendCooldown: Int = 0,
    val turnstileToken: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun setTurnstileToken(token: String) {
        _uiState.value = _uiState.value.copy(turnstileToken = token)
    }

    fun signIn(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Заполните все поля")
            return
        }
        val token = _uiState.value.turnstileToken
        if (token.isNullOrBlank()) {
            _uiState.value = _uiState.value.copy(error = "Дождитесь загрузки проверки")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = authRepository.signIn(email, password, token)
            if (result.success) {
                if (result.requiresOtp) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        showOtp = true,
                        otpEmail = result.email ?: email,
                        otpFlowToken = result.flowToken ?: "",
                        otpPassword = password,
                        otpResendCooldown = 60
                    )
                    startResendCooldown()
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true
                    )
                }
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = result.error ?: "Ошибка входа"
                )
            }
        }
    }

    fun verifyOtp(otp: String) {
        if (otp.length != 6) {
            _uiState.value = _uiState.value.copy(error = "Введите 6-значный код")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = authRepository.verifyOtp(
                email = _uiState.value.otpEmail,
                password = _uiState.value.otpPassword,
                otp = otp,
                flowToken = _uiState.value.otpFlowToken
            )
            if (result.success) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSuccess = true
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = result.error ?: "Неверный код"
                )
            }
        }
    }

    fun resendOtp() {
        if (_uiState.value.otpResendCooldown > 0) return
        viewModelScope.launch {
            authRepository.resendOtp(
                _uiState.value.otpEmail,
                _uiState.value.otpFlowToken
            ).onSuccess {
                _uiState.value = _uiState.value.copy(otpResendCooldown = 60)
                startResendCooldown()
            }.onFailure {
                _uiState.value = _uiState.value.copy(error = "Не удалось отправить код")
            }
        }
    }

    private fun startResendCooldown() {
        viewModelScope.launch {
            while (_uiState.value.otpResendCooldown > 0) {
                kotlinx.coroutines.delay(1000)
                _uiState.value = _uiState.value.copy(
                    otpResendCooldown = _uiState.value.otpResendCooldown - 1
                )
            }
        }
    }

    fun backFromOtp() {
        _uiState.value = _uiState.value.copy(
            showOtp = false,
            error = null,
            otpFlowToken = "",
            otpPassword = ""
        )
    }

    fun signUp(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Заполните все поля")
            return
        }
        val token = _uiState.value.turnstileToken
        if (token.isNullOrBlank()) {
            _uiState.value = _uiState.value.copy(error = "Дождитесь загрузки проверки")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = authRepository.signUp(email, password, token)
            if (result.success) {
                if (result.requiresOtp) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        showOtp = true,
                        otpEmail = result.email ?: email,
                        otpFlowToken = result.flowToken ?: "",
                        otpPassword = password,
                        otpResendCooldown = 60
                    )
                    startResendCooldown()
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true
                    )
                }
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = result.error ?: "Ошибка регистрации"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
