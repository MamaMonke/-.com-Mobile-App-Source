package com.itd.app.ui.screens.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.itd.app.ui.components.CreatePostBar
import com.itd.app.ui.components.PostCard
import com.itd.app.ui.components.TopClansSection
import com.itd.app.ui.theme.*

@Composable
fun FeedScreen(
    onNavigateToProfile: (String) -> Unit = {},
    onNavigateToPost: (String) -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    viewModel: FeedViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ITDBackground)
    ) {
        @OptIn(ExperimentalMaterial3Api::class)
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "ИТД",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = ITDOnSurface
                        )
                    }
                }

                // Create post bar
                if (state.isLoggedIn) {
                    item {
                        CreatePostBar(
                            userAvatar = state.userAvatar,
                            onPostCreate = { viewModel.createPost(it) }
                        )
                    }
                }

                // Tabs
                item {
                    FeedTabs(
                        activeTab = state.activeTab,
                        onTabChange = { viewModel.switchTab(it) }
                    )
                }

                // Top clans
                if (state.topClans.isNotEmpty()) {
                    item {
                        TopClansSection(clans = state.topClans)
                        HorizontalDivider(color = ITDDivider, thickness = 0.5.dp)
                    }
                }

                // Posts
                if (state.isLoading && state.posts.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = ITDPrimary)
                        }
                    }
                }

                if (state.error != null && state.posts.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = state.error ?: "Ошибка",
                                color = ITDError,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(onClick = { viewModel.loadPosts() }) {
                                Text("Повторить", color = ITDPrimary)
                            }
                        }
                    }
                }

                items(state.posts, key = { it.id }) { post ->
                    PostCard(
                        post = post,
                        onLike = { viewModel.likePost(it) },
                        onComment = { onNavigateToPost(it) },
                        onRepost = { viewModel.repostPost(it) },
                        onProfileClick = { onNavigateToProfile(it) },
                        onPostClick = { onNavigateToPost(it) }
                    )
                }

                // Bottom spacer
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}


@Composable
fun FeedTabs(
    activeTab: String,
    onTabChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ITDSurface)
    ) {
        TabItem(
            title = "Популярное",
            isActive = activeTab == "popular",
            onClick = { onTabChange("popular") },
            modifier = Modifier.weight(1f)
        )
        TabItem(
            title = "Подписки",
            isActive = activeTab == "following",
            onClick = { onTabChange("following") },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun TabItem(
    title: String,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextButton(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = title,
                color = if (isActive) ITDOnSurface else ITDOnSurfaceVariant,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal
            )
        }
        if (isActive) {
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(0.5f),
                thickness = 2.dp,
                color = ITDPrimary
            )
        }
    }
}
