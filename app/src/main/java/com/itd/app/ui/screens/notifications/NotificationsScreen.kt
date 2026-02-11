package com.itd.app.ui.screens.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.itd.app.data.model.Notification
import com.itd.app.ui.components.formatTimeAgo
import com.itd.app.ui.screens.feed.TabItem
import com.itd.app.ui.theme.*

@Composable
fun NotificationsScreen(
    onNavigateToProfile: (String) -> Unit = {},
    onNavigateToPost: (String) -> Unit = {},
    viewModel: NotificationsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ITDBackground)
    ) {
        // Header
        Text(
            text = "Уведомления",
            style = MaterialTheme.typography.headlineMedium,
            color = ITDOnSurface,
            modifier = Modifier.padding(16.dp)
        )

        // Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(ITDSurface)
        ) {
            TabItem(
                title = "Все",
                isActive = state.activeTab == "all",
                onClick = { viewModel.switchTab("all") },
                modifier = Modifier.weight(1f)
            )
            TabItem(
                title = "Упоминания",
                isActive = state.activeTab == "mentions",
                onClick = { viewModel.switchTab("mentions") },
                modifier = Modifier.weight(1f)
            )
        }

        HorizontalDivider(color = ITDDivider, thickness = 0.5.dp)

        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = ITDPrimary)
            }
        } else if (state.notifications.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Нет уведомлений",
                    color = ITDOnSurfaceVariant,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn {
                items(state.notifications, key = { it.id }) { notification ->
                    NotificationItem(
                        notification = notification,
                        onProfileClick = onNavigateToProfile,
                        onPostClick = onNavigateToPost
                    )
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun NotificationItem(
    notification: Notification,
    onProfileClick: (String) -> Unit = {},
    onPostClick: (String) -> Unit = {}
) {
    val fromUser = notification.fromUser

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                notification.postId?.let { onPostClick(it) }
                    ?: fromUser?.username?.let { onProfileClick(it) }
            }
            .background(
                if (!notification.isRead) ITDSurfaceVariant.copy(alpha = 0.3f) else ITDSurface
            )
            .padding(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Avatar
        Text(
            text = fromUser?.avatar ?: "🔔",
            fontSize = 32.sp,
            modifier = Modifier.clickable {
                fromUser?.username?.let { onProfileClick(it) }
            }
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row {
                Text(
                    text = fromUser?.displayName ?: "Система",
                    style = MaterialTheme.typography.titleSmall,
                    color = ITDOnSurface,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = getNotificationActionText(notification.type),
                    style = MaterialTheme.typography.bodyMedium,
                    color = ITDOnSurfaceVariant
                )
            }

            notification.postContent?.let { content ->
                if (content.isNotBlank()) {
                    Text(
                        text = content,
                        style = MaterialTheme.typography.bodyMedium,
                        color = ITDOnSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            notification.message?.let { message ->
                if (message.isNotBlank()) {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = ITDOnSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            Text(
                text = formatTimeAgo(notification.createdAt),
                style = MaterialTheme.typography.bodySmall,
                color = ITDOnSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }

    HorizontalDivider(color = ITDDivider, thickness = 0.5.dp)
}

fun getNotificationActionText(type: String): String {
    return when (type) {
        "follow" -> "подписался на вас"
        "like" -> "понравился ваш пост"
        "comment" -> "прокомментировал ваш пост"
        "repost" -> "репостнул ваш пост"
        "mention" -> "упомянул вас"
        "wall_post" -> "написал на вашей стене"
        else -> type
    }
}
