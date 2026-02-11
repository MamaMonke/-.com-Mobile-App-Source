package com.itd.app.data.model

import com.google.gson.annotations.SerializedName

data class Post(
    @SerializedName("id") val id: String,
    @SerializedName("content") val content: String,
    @SerializedName("spans") val spans: List<Any> = emptyList(),
    @SerializedName("likesCount") val likesCount: Int = 0,
    @SerializedName("commentsCount") val commentsCount: Int = 0,
    @SerializedName("repostsCount") val repostsCount: Int = 0,
    @SerializedName("viewsCount") val viewsCount: Int = 0,
    @SerializedName("authorId") val authorId: String? = null,
    @SerializedName("wallRecipientId") val wallRecipientId: String? = null,
    @SerializedName("wallRecipient") val wallRecipient: PostAuthor? = null,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("author") val author: PostAuthor,
    @SerializedName("attachments") val attachments: List<Attachment> = emptyList(),
    @SerializedName("isLiked") val isLiked: Boolean = false,
    @SerializedName("isReposted") val isReposted: Boolean = false,
    @SerializedName("isOwner") val isOwner: Boolean = false,
    @SerializedName("isViewed") val isViewed: Boolean = false,
    @SerializedName("originalPost") val originalPost: Post? = null
)

data class Pin(
    @SerializedName("slug") val slug: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("description") val description: String? = null
)

data class PostAuthor(
    @SerializedName("id") val id: String? = null,
    @SerializedName("username") val username: String,
    @SerializedName("displayName") val displayName: String,
    @SerializedName("avatar") val avatar: String,
    @SerializedName("verified") val verified: Boolean = false,
    @SerializedName("pin") val pin: Pin? = null
)

data class Attachment(
    @SerializedName("id") val id: String,
    @SerializedName("type") val type: String,
    @SerializedName("url") val url: String,
    @SerializedName("thumbnailUrl") val thumbnailUrl: String? = null,
    @SerializedName("width") val width: Int? = null,
    @SerializedName("height") val height: Int? = null
)

data class PostsData(
    @SerializedName("posts") val posts: List<Post>
)

data class PostsResponse(
    @SerializedName("data") val data: PostsData
)

data class CreatePostRequest(
    @SerializedName("content") val content: String,
    @SerializedName("attachments") val attachments: List<String> = emptyList()
)
