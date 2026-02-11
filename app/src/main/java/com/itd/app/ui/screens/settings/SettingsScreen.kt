package com.itd.app.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.itd.app.ui.theme.*

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit = {},
    onLogout: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ITDBackground)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Close",
                    tint = ITDOnSurface
                )
            }
            Text(
                text = "Настройки",
                style = MaterialTheme.typography.headlineMedium,
                color = ITDOnSurface,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Settings items
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = ITDSurface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column {
                SettingsItem(
                    icon = Icons.Default.Person,
                    title = "Профиль",
                    subtitle = "Имя, юзернейм, био, пины",
                    onClick = { /* Navigate to profile edit */ }
                )
                HorizontalDivider(color = ITDDivider, thickness = 0.5.dp)

                SettingsItem(
                    icon = Icons.Default.Palette,
                    title = "Оформление",
                    subtitle = "Тема, снег",
                    onClick = { /* Navigate to appearance settings */ }
                )
                HorizontalDivider(color = ITDDivider, thickness = 0.5.dp)

                SettingsItem(
                    icon = Icons.Default.Lock,
                    title = "Безопасность",
                    subtitle = "Смена пароля",
                    onClick = { /* Navigate to security */ }
                )
                HorizontalDivider(color = ITDDivider, thickness = 0.5.dp)

                SettingsItem(
                    icon = Icons.Default.VisibilityOff,
                    title = "Приватность",
                    subtitle = "Записи на стене",
                    onClick = { /* Navigate to privacy */ }
                )
                HorizontalDivider(color = ITDDivider, thickness = 0.5.dp)

                SettingsItem(
                    icon = Icons.Default.Notifications,
                    title = "Уведомления",
                    subtitle = "Звук, лайки, комментарии",
                    onClick = { /* Navigate to notification settings */ }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Logout
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clickable { showLogoutDialog = true },
            colors = CardDefaults.cardColors(containerColor = ITDSurface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Logout",
                    tint = ITDError,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Выйти",
                    style = MaterialTheme.typography.titleMedium,
                    color = ITDError
                )
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Выйти из аккаунта?", color = ITDOnSurface) },
            text = { Text("Вы уверены, что хотите выйти?", color = ITDOnSurfaceVariant) },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    viewModel.logout()
                    onLogout()
                }) {
                    Text("Выйти", color = ITDError)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Отмена", color = ITDPrimary)
                }
            },
            containerColor = ITDSurface
        )
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = ITDOnSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = ITDOnSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = ITDOnSurfaceVariant
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = ITDOnSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
    }
}
