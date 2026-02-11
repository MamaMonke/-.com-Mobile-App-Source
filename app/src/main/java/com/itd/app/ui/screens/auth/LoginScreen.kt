package com.itd.app.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.itd.app.ui.components.TurnstileWebView
import com.itd.app.ui.theme.*

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit = {},
    onLoginSuccess: () -> Unit = {},
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) onLoginSuccess()
    }

    if (state.showOtp) {
        OtpScreen(
            email = state.otpEmail,
            isLoading = state.isLoading,
            error = state.error,
            cooldown = state.otpResendCooldown,
            onVerify = { viewModel.verifyOtp(it) },
            onResend = { viewModel.resendOtp() },
            onBack = { viewModel.backFromOtp() }
        )
    } else {
        LoginForm(
            isLoading = state.isLoading,
            error = state.error,
            onSignIn = { email, password -> viewModel.signIn(email, password) },
            onNavigateToRegister = onNavigateToRegister,
            onTurnstileToken = { viewModel.setTurnstileToken(it) }
        )
    }
}

@Composable
private fun LoginForm(
    isLoading: Boolean,
    error: String?,
    onSignIn: (String, String) -> Unit,
    onNavigateToRegister: () -> Unit,
    onTurnstileToken: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val inputColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = ITDOnSurface,
        unfocusedTextColor = ITDOnSurface,
        focusedBorderColor = ITDPrimary,
        unfocusedBorderColor = ITDDivider,
        cursorColor = ITDPrimary,
        focusedLabelColor = ITDPrimary,
        unfocusedLabelColor = ITDOnSurfaceVariant,
        focusedContainerColor = ITDInputBackground,
        unfocusedContainerColor = ITDInputBackground
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ITDBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "ИТД",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = ITDOnSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Добро пожаловать в итд!",
                style = MaterialTheme.typography.headlineSmall,
                color = ITDOnSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Электронная почта") },
                placeholder = { Text("example@mail.com", color = ITDOnSurfaceVariant) },
                modifier = Modifier.fillMaxWidth(),
                colors = inputColors,
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Пароль") },
                placeholder = { Text("Введите пароль", color = ITDOnSurfaceVariant) },
                modifier = Modifier.fillMaxWidth(),
                colors = inputColors,
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null,
                            tint = ITDOnSurfaceVariant
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            TurnstileWebView(
                onTokenReceived = onTurnstileToken
            )

            error?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = it, color = ITDError, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onSignIn(email, password) },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ITDPrimary,
                    contentColor = ITDOnSurface
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = ITDOnSurface,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Войти", fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onNavigateToRegister) {
                Text("Нет аккаунта? Зарегистрироваться", color = ITDPrimary)
            }
        }
    }
}

@Composable
fun OtpScreen(
    email: String,
    isLoading: Boolean,
    error: String?,
    cooldown: Int,
    onVerify: (String) -> Unit,
    onResend: () -> Unit,
    onBack: () -> Unit
) {
    var otpDigits by remember { mutableStateOf(List(6) { "" }) }
    val focusRequesters = remember { List(6) { FocusRequester() } }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ITDBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = ITDOnSurface)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Подтверждение",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = ITDOnSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Мы отправили код подтверждения на",
                style = MaterialTheme.typography.bodyMedium,
                color = ITDOnSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Text(
                text = email,
                style = MaterialTheme.typography.bodyMedium,
                color = ITDOnSurface,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 0..5) {
                    OutlinedTextField(
                        value = otpDigits[i],
                        onValueChange = { value ->
                            val digit = value.filter { it.isDigit() }.take(1)
                            val newDigits = otpDigits.toMutableList()
                            newDigits[i] = digit
                            otpDigits = newDigits
                            if (digit.isNotEmpty() && i < 5) {
                                focusRequesters[i + 1].requestFocus()
                            }
                            if (newDigits.all { it.isNotEmpty() }) {
                                onVerify(newDigits.joinToString(""))
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(focusRequesters[i]),
                        textStyle = LocalTextStyle.current.copy(
                            textAlign = TextAlign.Center,
                            fontSize = 20.sp,
                            color = ITDOnSurface
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ITDPrimary,
                            unfocusedBorderColor = ITDDivider,
                            cursorColor = ITDPrimary,
                            focusedContainerColor = ITDInputBackground,
                            unfocusedContainerColor = ITDInputBackground
                        ),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }

            error?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = it, color = ITDError, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { onVerify(otpDigits.joinToString("")) },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                enabled = !isLoading && otpDigits.joinToString("").length == 6,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ITDPrimary,
                    contentColor = ITDOnSurface
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = ITDOnSurface,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Подтвердить", fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (cooldown > 0) {
                Text(
                    text = "Отправить код повторно через $cooldown сек",
                    style = MaterialTheme.typography.bodySmall,
                    color = ITDOnSurfaceVariant
                )
            } else {
                TextButton(onClick = onResend) {
                    Text("Отправить код ещё раз", color = ITDPrimary)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = onBack) {
                Text("Назад", color = ITDOnSurfaceVariant)
            }
        }
    }
}
