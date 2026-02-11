package com.itd.app.data.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id") val id: String,
    @SerializedName("username") val username: String,
    @SerializedName("displayName") val displayName: String,
    @SerializedName("avatar") val avatar: String,
    @SerializedName("bio") val bio: String? = null,
    @SerializedName("banner") val banner: String? = null,
    @SerializedName("verified") val verified: Boolean = false,
    @SerializedName("pin") val pin: Pin? = null,
    @SerializedName("pinnedPostId") val pinnedPostId: String? = null,
    @SerializedName("wallAccess") val wallAccess: String? = null,
    @SerializedName("likesVisibility") val likesVisibility: String? = null,
    @SerializedName("followersCount") val followersCount: Int = 0,
    @SerializedName("followingCount") val followingCount: Int = 0,
    @SerializedName("postsCount") val postsCount: Int = 0,
    @SerializedName("isFollowing") val isFollowing: Boolean = false,
    @SerializedName("isFollowedBy") val isFollowedBy: Boolean = false,
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("roles") val roles: List<String>? = null
)

data class UserSuggestion(
    @SerializedName("id") val id: String,
    @SerializedName("username") val username: String,
    @SerializedName("displayName") val displayName: String,
    @SerializedName("avatar") val avatar: String,
    @SerializedName("verified") val verified: Boolean = false,
    @SerializedName("followersCount") val followersCount: Int = 0
)

data class SuggestionsResponse(
    @SerializedName("users") val users: List<UserSuggestion>
)

data class Clan(
    @SerializedName("avatar") val avatar: String,
    @SerializedName("memberCount") val memberCount: Int
)

data class TopClansResponse(
    @SerializedName("clans") val clans: List<Clan>
)
