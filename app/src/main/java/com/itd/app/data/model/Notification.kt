package com.itd.app.data.model

import com.google.gson.annotations.SerializedName

data class Notification(
    @SerializedName("id") val id: String,
    @SerializedName("type") val type: String,
    @SerializedName("message") val message: String? = null,
    @SerializedName("postId") val postId: String? = null,
    @SerializedName("postContent") val postContent: String? = null,
    @SerializedName("fromUser") val fromUser: NotificationUser? = null,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("isRead") val isRead: Boolean = false
)

data class NotificationUser(
    @SerializedName("id") val id: String? = null,
    @SerializedName("username") val username: String,
    @SerializedName("displayName") val displayName: String,
    @SerializedName("avatar") val avatar: String,
    @SerializedName("verified") val verified: Boolean = false
)

data class NotificationCountResponse(
    @SerializedName("count") val count: Int
)

data class NotificationsResponse(
    @SerializedName("notifications") val notifications: List<Notification> = emptyList(),
    @SerializedName("data") val data: List<Notification>? = null
)

data class HashtagItem(
    @SerializedName("tag") val tag: String? = null,
    @SerializedName("hashtag") val hashtag: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("postsCount") val postsCount: Int = 0,
    @SerializedName("count") val count: Int? = null
) {
    val displayTag: String get() = tag ?: hashtag ?: name ?: ""
    val displayCount: Int get() = if (postsCount > 0) postsCount else (count ?: 0)
}

data class TrendingHashtagsResponse(
    @SerializedName("hashtags") val hashtags: List<HashtagItem> = emptyList(),
    @SerializedName("data") val data: List<HashtagItem>? = null
)

data class SearchResponse(
    @SerializedName("users") val users: List<UserSuggestion> = emptyList(),
    @SerializedName("hashtags") val hashtags: List<HashtagItem> = emptyList()
)

data class FileUploadResponse(
    @SerializedName("id") val id: String,
    @SerializedName("url") val url: String
)
