package com.itd.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.itd.app.data.model.Post
import com.itd.app.ui.theme.*

@Composable
fun PostCard(
    post: Post,
    onLike: (String) -> Unit = {},
    onComment: (String) -> Unit = {},
    onRepost: (String) -> Unit = {},
    onProfileClick: (String) -> Unit = {},
    onPostClick: (String) -> Unit = {},
    onDelete: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onPostClick(post.id) },
        colors = CardDefaults.cardColors(containerColor = ITDSurface),
        shape = RoundedCornerShape(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Author row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Emoji avatar
                Text(
                    text = post.author.avatar,
                    fontSize = 32.sp,
                    modifier = Modifier.clickable { onProfileClick(post.author.username) }
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onProfileClick(post.author.username) }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = post.author.displayName,
                            style = MaterialTheme.typography.titleMedium,
                            color = ITDOnSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (post.author.verified) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Verified",
                                tint = ITDVerified,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Text(
                        text = formatTimeAgo(post.createdAt),
                        style = MaterialTheme.typography.bodySmall,
                        color = ITDOnSurfaceVariant
                    )
                }

                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Menu",
                            tint = ITDOnSurfaceVariant
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        if (post.isOwner && onDelete != null) {
                            DropdownMenuItem(
                                text = { Text("Удалить") },
                                onClick = {
                                    showMenu = false
                                    onDelete(post.id)
                                }
                            )
                        }
                    }
                }
            }

            // Content
            if (post.content.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = post.content,
                    style = MaterialTheme.typography.bodyLarge,
                    color = ITDOnSurface
                )
            }

            // Attachments (images)
            if (post.attachments.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                post.attachments.forEach { attachment ->
                    if (attachment.type == "image") {
                        AsyncImage(
                            model = attachment.url,
                            contentDescription = "Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .heightIn(max = 400.dp),
                            contentScale = ContentScale.FillWidth
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }

            // Actions row
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Like
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onLike(post.id) }
                ) {
                    Icon(
                        imageVector = if (post.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (post.isLiked) ITDLike else ITDOnSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    if (post.likesCount > 0) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = formatCount(post.likesCount),
                            style = MaterialTheme.typography.bodySmall,
                            color = if (post.isLiked) ITDLike else ITDOnSurfaceVariant
                        )
                    }
                }

                // Comment
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onComment(post.id) }
                ) {
                    Icon(
                        imageVector = Icons.Default.ChatBubbleOutline,
                        contentDescription = "Comment",
                        tint = ITDOnSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    if (post.commentsCount > 0) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = formatCount(post.commentsCount),
                            style = MaterialTheme.typography.bodySmall,
                            color = ITDOnSurfaceVariant
                        )
                    }
                }

                // Repost
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onRepost(post.id) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Repeat,
                        contentDescription = "Repost",
                        tint = if (post.isReposted) ITDRepost else ITDOnSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    if (post.repostsCount > 0) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = formatCount(post.repostsCount),
                            style = MaterialTheme.typography.bodySmall,
                            color = if (post.isReposted) ITDRepost else ITDOnSurfaceVariant
                        )
                    }
                }

                // Views
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = "Views",
                        tint = ITDOnSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    if (post.viewsCount > 0) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = formatCount(post.viewsCount),
                            style = MaterialTheme.typography.bodySmall,
                            color = ITDOnSurfaceVariant
                        )
                    }
                }
            }
        }

        HorizontalDivider(color = ITDDivider, thickness = 0.5.dp)
    }
}

fun formatCount(count: Int): String {
    return when {
        count >= 1_000_000 -> String.format("%.1fM", count / 1_000_000.0)
        count >= 1_000 -> String.format("%.1fK", count / 1_000.0)
        else -> count.toString()
    }
}

fun formatTimeAgo(dateString: String): String {
    return try {
        val cleanDate = dateString
            .replace("T", " ")
            .replace(Regex("\\.[0-9]+"), "")
            .replace("Z", "")
            .replace(Regex("\\+\\d{2}$"), "")
            .trim()

        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
        sdf.timeZone = java.util.TimeZone.getTimeZone("UTC")
        val date = sdf.parse(cleanDate) ?: return dateString

        val now = System.currentTimeMillis()
        val diff = now - date.time

        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        when {
            seconds < 60 -> "только что"
            minutes < 60 -> "${minutes}м назад"
            hours < 24 -> "${hours}ч назад"
            days < 7 -> "${days}д назад"
            else -> {
                val outFormat = java.text.SimpleDateFormat("dd MMM", java.util.Locale("ru"))
                outFormat.format(date)
            }
        }
    } catch (e: Exception) {
        dateString
    }
}
