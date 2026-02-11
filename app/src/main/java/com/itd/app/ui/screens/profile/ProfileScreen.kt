package com.itd.app.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.itd.app.ui.components.CreatePostBar
import com.itd.app.ui.components.PostCard
import com.itd.app.ui.components.formatCount
import com.itd.app.ui.screens.feed.TabItem
import com.itd.app.ui.theme.*

@Composable
fun ProfileScreen(
    username: String?,
    onNavigateToSettings: () -> Unit = {},
    onNavigateToProfile: (String) -> Unit = {},
    onNavigateToPost: (String) -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(username) {
        viewModel.loadProfile(username)
    }

    if (state.isLoading && state.user == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ITDBackground),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = ITDPrimary)
        }
        return
    }

    if (state.error != null && state.user == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ITDBackground),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = state.error ?: "Ошибка",
                    color = ITDError,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onNavigateToLogin,
                    colors = ButtonDefaults.buttonColors(containerColor = ITDPrimary)
                ) {
                    Text("Войти")
                }
            }
        }
        return
    }

    val user = state.user ?: return

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ITDBackground)
    ) {
        // Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFE8B5CE),
                                Color(0xFFB5C9E8),
                                Color(0xFFC5E8D5)
                            )
                        )
                    )
            )
        }

        // Profile info
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ITDSurface)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    // Avatar (overlapping banner)
                    Text(
                        text = user.avatar,
                        fontSize = 64.sp,
                        modifier = Modifier.offset(y = (-40).dp)
                    )

                    // Actions
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (state.isOwnProfile) {
                            OutlinedButton(
                                onClick = { /* Edit profile */ },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = ITDOnSurface
                                ),
                                border = ButtonDefaults.outlinedButtonBorder
                            ) {
                                Text("Редактировать")
                            }
                            IconButton(onClick = onNavigateToSettings) {
                                Icon(
                                    Icons.Default.Settings,
                                    contentDescription = "Settings",
                                    tint = ITDOnSurface
                                )
                            }
                        } else {
                            Button(
                                onClick = { viewModel.toggleFollow() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (state.isFollowing) ITDSurfaceVariant else ITDPrimary,
                                    contentColor = ITDOnSurface
                                ),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Text(if (state.isFollowing) "Отписаться" else "Подписаться")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height((-24).dp))

                // Name
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = user.displayName,
                        style = MaterialTheme.typography.headlineMedium,
                        color = ITDOnSurface,
                        fontWeight = FontWeight.Bold
                    )
                    if (user.verified) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            Icons.Default.Verified,
                            contentDescription = "Verified",
                            tint = ITDVerified,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Text(
                    text = "@${user.username}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ITDOnSurfaceVariant
                )

                // Bio
                user.bio?.let { bio ->
                    if (bio.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = bio,
                            style = MaterialTheme.typography.bodyMedium,
                            color = ITDOnSurface
                        )
                    }
                }

                // Created at
                user.createdAt?.let { date ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "📅 Регистрация: ${formatRegistrationDate(date)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = ITDOnSurfaceVariant
                    )
                }

                // Stats
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = "${user.followingCount} Подписки",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ITDOnSurface,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${user.followersCount} Подписчики",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ITDOnSurface,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Tabs
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ITDSurface)
            ) {
                TabItem(
                    title = "Посты",
                    isActive = state.activeTab == "posts",
                    onClick = { viewModel.switchTab("posts") },
                    modifier = Modifier.weight(1f)
                )
                TabItem(
                    title = "Понравившиеся",
                    isActive = state.activeTab == "liked",
                    onClick = { viewModel.switchTab("liked") },
                    modifier = Modifier.weight(1f)
                )
            }
            HorizontalDivider(color = ITDDivider, thickness = 0.5.dp)
        }

        // Create post on own profile
        if (state.isOwnProfile) {
            item {
                CreatePostBar(
                    userAvatar = user.avatar,
                    onPostCreate = { /* Create post */ }
                )
            }
        }

        // Posts
        if (state.posts.isEmpty() && !state.isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Нет постов",
                        color = ITDOnSurfaceVariant,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        items(state.posts, key = { it.id }) { post ->
            PostCard(
                post = post,
                onLike = { viewModel.likePost(it) },
                onComment = { onNavigateToPost(it) },
                onProfileClick = { onNavigateToProfile(it) },
                onPostClick = { onNavigateToPost(it) },
                onDelete = if (state.isOwnProfile) { { viewModel.deletePost(it) } } else null
            )
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

fun formatRegistrationDate(dateString: String): String {
    return try {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        val date = sdf.parse(dateString.substring(0, 10))
        val outFormat = java.text.SimpleDateFormat("MMMM yyyy г.", java.util.Locale("ru"))
        date?.let { outFormat.format(it) } ?: dateString
    } catch (e: Exception) {
        dateString
    }
}
