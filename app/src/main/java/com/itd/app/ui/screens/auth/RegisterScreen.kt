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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.itd.app.ui.components.TurnstileWebView
import com.itd.app.ui.theme.*

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit = {},
    onRegisterSuccess: () -> Unit = {},
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) onRegisterSuccess()
    }

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
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ITDBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateToLogin) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = ITDOnSurface)
            }
            Text(
                text = "Регистрация",
                style = MaterialTheme.typography.headlineMedium,
                color = ITDOnSurface,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "итд \u2014 соц сеть Ильи Новки!",
                style = MaterialTheme.typography.bodyLarge,
                color = ITDOnSurfaceVariant
            )
            Text(
                text = "Регистрируйся и пользуйся!",
                style = MaterialTheme.typography.bodyLarge,
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

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Пароль") },
                placeholder = { Text("Придумайте пароль", color = ITDOnSurfaceVariant) },
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
                onTokenReceived = { viewModel.setTurnstileToken(it) }
            )

            state.error?.let { error ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = error, color = ITDError, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.signUp(email, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = !state.isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ITDPrimary,
                    contentColor = ITDOnSurface
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        color = ITDOnSurface,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Зарегистрироваться", fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onNavigateToLogin) {
                Text("Уже есть аккаунт? Войдите", color = ITDPrimary)
            }
        }
    }
}
