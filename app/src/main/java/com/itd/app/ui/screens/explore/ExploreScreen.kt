package com.itd.app.ui.screens.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.itd.app.ui.components.formatCount
import com.itd.app.ui.theme.*

@Composable
fun ExploreScreen(
    onNavigateToProfile: (String) -> Unit = {},
    viewModel: ExploreViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ITDBackground)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "ИТД",
                fontSize = 24.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = ITDOnSurface
            )
        }

        // Search bar
        OutlinedTextField(
            value = state.query,
            onValueChange = { viewModel.onSearchQueryChange(it) },
            placeholder = {
                Text("Поиск пользователей и хэштегов", color = ITDOnSurfaceVariant)
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = ITDOnSurfaceVariant
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = ITDOnSurface,
                unfocusedTextColor = ITDOnSurface,
                focusedBorderColor = ITDPrimary,
                unfocusedBorderColor = ITDDivider,
                cursorColor = ITDPrimary,
                focusedContainerColor = ITDSurfaceVariant,
                unfocusedContainerColor = ITDSurfaceVariant
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (state.query.isBlank()) {
            // Trending hashtags
            Text(
                text = "Популярные хэштеги",
                style = MaterialTheme.typography.headlineMedium,
                color = ITDOnSurface,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = ITDPrimary)
                }
            } else {
                LazyColumn {
                    itemsIndexed(state.trendingHashtags) { index, hashtag ->
                        HashtagRow(
                            rank = index + 1,
                            hashtag = hashtag.displayTag,
                            postsCount = hashtag.displayCount
                        )
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        } else {
            // Search results
            if (state.isSearching) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = ITDPrimary)
                }
            } else {
                LazyColumn {
                    if (state.searchResults.isNotEmpty()) {
                        item {
                            Text(
                                text = "Пользователи",
                                style = MaterialTheme.typography.titleMedium,
                                color = ITDOnSurfaceVariant,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                        itemsIndexed(state.searchResults) { _, user ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onNavigateToProfile(user.username) }
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = user.avatar, fontSize = 32.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = user.displayName,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = ITDOnSurface
                                        )
                                        if (user.verified) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(
                                                Icons.Default.Verified,
                                                contentDescription = null,
                                                tint = ITDVerified,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "@${user.username}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = ITDOnSurfaceVariant
                                    )
                                }
                                Text(
                                    text = "${formatCount(user.followersCount)} подп.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ITDOnSurfaceVariant
                                )
                            }
                            HorizontalDivider(color = ITDDivider, thickness = 0.5.dp)
                        }
                    }

                    if (state.searchHashtags.isNotEmpty()) {
                        item {
                            Text(
                                text = "Хэштеги",
                                style = MaterialTheme.typography.titleMedium,
                                color = ITDOnSurfaceVariant,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                        itemsIndexed(state.searchHashtags) { index, hashtag ->
                            HashtagRow(
                                rank = index + 1,
                                hashtag = hashtag.displayTag,
                                postsCount = hashtag.displayCount
                            )
                        }
                    }

                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
fun HashtagRow(rank: Int, hashtag: String, postsCount: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = rank.toString(),
            style = MaterialTheme.typography.titleMedium,
            color = ITDOnSurfaceVariant,
            modifier = Modifier.width(32.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (hashtag.startsWith("#")) hashtag else "#$hashtag",
                style = MaterialTheme.typography.titleMedium,
                color = ITDOnSurface
            )
            if (postsCount > 0) {
                Text(
                    text = "${formatCount(postsCount)} постов",
                    style = MaterialTheme.typography.bodySmall,
                    color = ITDOnSurfaceVariant
                )
            }
        }
    }
    HorizontalDivider(color = ITDDivider, thickness = 0.5.dp)
}
