package com.itd.app.data.api

import com.itd.app.data.model.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ITDApiService {

    // ============ AUTH ============

    @POST("api/v1/auth/refresh")
    suspend fun refreshToken(): Response<AuthResponse>

    @POST("api/v1/auth/sign-in")
    suspend fun signIn(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/v1/auth/sign-up")
    suspend fun signUp(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("api/v1/auth/verify-otp")
    suspend fun verifyOtp(@Body request: VerifyOtpRequest): Response<AuthResponse>

    @POST("api/v1/auth/resend-otp")
    suspend fun resendOtp(@Body request: ResendOtpRequest): Response<Unit>

    @POST("api/v1/auth/logout")
    suspend fun logout(): Response<Unit>

    // ============ PROFILE ============

    @GET("api/profile")
    suspend fun getProfile(): Response<ProfileResponse>

    @GET("api/users/{username}")
    suspend fun getUserByUsername(@Path("username") username: String): Response<User>

    @GET("api/users/me")
    suspend fun getCurrentUser(): Response<User>

    @PUT("api/users/me")
    suspend fun updateProfile(@Body updates: Map<String, String>): Response<User>

    @GET("api/users/me/pins")
    suspend fun getMyPins(): Response<List<String>>

    @PUT("api/users/me/pin")
    suspend fun setPin(@Body body: Map<String, String?>): Response<Unit>

    @PUT("api/users/me/privacy")
    suspend fun updatePrivacy(@Body body: Map<String, String>): Response<Unit>

    @GET("api/users/me/blocked")
    suspend fun getBlockedUsers(): Response<List<User>>

    // ============ POSTS ============

    @GET("api/posts")
    suspend fun getPosts(
        @Query("limit") limit: Int = 20,
        @Query("tab") tab: String = "popular",
        @Query("cursor") cursor: String? = null
    ): Response<PostsResponse>

    @GET("api/posts/user/{username}")
    suspend fun getUserPosts(
        @Path("username") username: String,
        @Query("limit") limit: Int = 20,
        @Query("sort") sort: String = "new",
        @Query("cursor") cursor: String? = null
    ): Response<PostsResponse>

    @POST("api/posts")
    suspend fun createPost(@Body request: CreatePostRequest): Response<Post>

    @POST("api/posts/{id}/view")
    suspend fun markPostViewed(@Path("id") postId: String): Response<Unit>

    @POST("api/posts/{id}/like")
    suspend fun likePost(@Path("id") postId: String): Response<Unit>

    @DELETE("api/posts/{id}/like")
    suspend fun unlikePost(@Path("id") postId: String): Response<Unit>

    @POST("api/posts/{id}/repost")
    suspend fun repostPost(@Path("id") postId: String): Response<Unit>

    @DELETE("api/posts/{id}")
    suspend fun deletePost(@Path("id") postId: String): Response<Unit>

    @GET("api/posts/{id}/comments")
    suspend fun getComments(
        @Path("id") postId: String,
        @Query("limit") limit: Int = 20,
        @Query("cursor") cursor: String? = null
    ): Response<PostsResponse>

    @POST("api/posts/{id}/comments")
    suspend fun createComment(
        @Path("id") postId: String,
        @Body request: CreatePostRequest
    ): Response<Post>

    // ============ USERS ============

    @GET("api/users/stats/top-clans")
    suspend fun getTopClans(): Response<TopClansResponse>

    @GET("api/users/suggestions/who-to-follow")
    suspend fun getWhoToFollow(): Response<SuggestionsResponse>

    @POST("api/users/{username}/follow")
    suspend fun followUser(@Path("username") username: String): Response<Unit>

    @DELETE("api/users/{username}/follow")
    suspend fun unfollowUser(@Path("username") username: String): Response<Unit>

    @GET("api/users/{username}/followers")
    suspend fun getFollowers(
        @Path("username") username: String,
        @Query("limit") limit: Int = 20,
        @Query("cursor") cursor: String? = null
    ): Response<SuggestionsResponse>

    @GET("api/users/{username}/following")
    suspend fun getFollowing(
        @Path("username") username: String,
        @Query("limit") limit: Int = 20,
        @Query("cursor") cursor: String? = null
    ): Response<SuggestionsResponse>

    // ============ SEARCH ============

    @GET("api/search")
    suspend fun search(@Query("q") query: String): Response<SearchResponse>

    @GET("api/users/search")
    suspend fun searchUsers(@Query("q") query: String): Response<SuggestionsResponse>

    @GET("api/hashtags/trending")
    suspend fun getTrendingHashtags(): Response<TrendingHashtagsResponse>

    @GET("api/hashtags")
    suspend fun searchHashtags(@Query("q") query: String): Response<TrendingHashtagsResponse>

    @GET("api/hashtags/{tag}/posts")
    suspend fun getHashtagPosts(
        @Path("tag") tag: String,
        @Query("limit") limit: Int = 20,
        @Query("cursor") cursor: String? = null
    ): Response<PostsResponse>

    // ============ NOTIFICATIONS ============

    @GET("api/notifications/")
    suspend fun getNotifications(
        @Query("limit") limit: Int = 20,
        @Query("cursor") cursor: String? = null,
        @Query("type") type: String? = null
    ): Response<NotificationsResponse>

    @GET("api/notifications/count")
    suspend fun getNotificationCount(): Response<NotificationCountResponse>

    @POST("api/notifications/read-all")
    suspend fun markAllNotificationsRead(): Response<Unit>

    @POST("api/notifications/{id}/read")
    suspend fun markNotificationRead(@Path("id") id: String): Response<Unit>

    @POST("api/notifications/read-batch")
    suspend fun markNotificationsRead(@Body ids: Map<String, List<String>>): Response<Unit>

    @GET("api/notifications/settings")
    suspend fun getNotificationSettings(): Response<Map<String, Any>>

    // ============ FILES ============

    @Multipart
    @POST("api/files/upload")
    suspend fun uploadFile(@Part file: MultipartBody.Part): Response<FileUploadResponse>
}
