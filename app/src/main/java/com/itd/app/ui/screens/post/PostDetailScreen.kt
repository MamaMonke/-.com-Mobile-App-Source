package com.itd.app.ui.screens.post

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.itd.app.ui.components.PostCard
import com.itd.app.ui.theme.*

@Composable
fun PostDetailScreen(
    postId: String,
    onNavigateBack: () -> Unit = {},
    onNavigateToProfile: (String) -> Unit = {},
    viewModel: PostDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var commentText by remember { mutableStateOf("") }

    LaunchedEffect(postId) {
        viewModel.loadPost(postId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ITDBackground)
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(ITDSurface)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = ITDOnSurface
                )
            }
            Text(
                text = "Пост",
                style = MaterialTheme.typography.titleLarge,
                color = ITDOnSurface,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        // Content
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            // Post itself
            state.post?.let { post ->
                item {
                    PostCard(
                        post = post,
                        onLike = { viewModel.likePost(it) },
                        onProfileClick = onNavigateToProfile
                    )
                }
            }

            // Comments header
            item {
                Text(
                    text = "Комментарии",
                    style = MaterialTheme.typography.titleMedium,
                    color = ITDOnSurface,
                    modifier = Modifier.padding(16.dp)
                )
            }

            if (state.isLoading) {
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

            if (state.comments.isEmpty() && !state.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Нет комментариев",
                            color = ITDOnSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            items(state.comments, key = { it.id }) { comment ->
                PostCard(
                    post = comment,
                    onProfileClick = onNavigateToProfile
                )
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }
        }

        // Comment input
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(ITDSurface)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = commentText,
                onValueChange = { commentText = it },
                placeholder = {
                    Text("Написать комментарий...", color = ITDOnSurfaceVariant)
                },
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = ITDOnSurface,
                    unfocusedTextColor = ITDOnSurface,
                    focusedBorderColor = ITDPrimary,
                    unfocusedBorderColor = ITDDivider,
                    cursorColor = ITDPrimary,
                    focusedContainerColor = ITDInputBackground,
                    unfocusedContainerColor = ITDInputBackground
                ),
                shape = RoundedCornerShape(20.dp),
                maxLines = 3
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (commentText.isNotBlank()) {
                        viewModel.createComment(postId, commentText)
                        commentText = ""
                    }
                },
                enabled = commentText.isNotBlank()
            ) {
                Icon(
                    Icons.Default.Send,
                    contentDescription = "Send",
                    tint = if (commentText.isNotBlank()) ITDPrimary else ITDOnSurfaceVariant
                )
            }
        }
    }
}
