package com.itd.app.data.repository

import com.itd.app.data.api.ITDApiService
import com.itd.app.data.model.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepository @Inject constructor(
    private val api: ITDApiService
) {
    suspend fun getPosts(tab: String = "popular", limit: Int = 20, cursor: String? = null): Result<List<Post>> {
        return try {
            val response = api.getPosts(limit = limit, tab = tab, cursor = cursor)
            if (response.isSuccessful) {
                Result.success(response.body()?.data?.posts ?: emptyList())
            } else {
                Result.failure(Exception("Ошибка загрузки постов: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserPosts(username: String, limit: Int = 20, sort: String = "new", cursor: String? = null): Result<List<Post>> {
        return try {
            val response = api.getUserPosts(username, limit, sort, cursor)
            if (response.isSuccessful) {
                Result.success(response.body()?.data?.posts ?: emptyList())
            } else {
                Result.failure(Exception("Ошибка загрузки постов: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createPost(content: String, attachmentIds: List<String> = emptyList()): Result<Post> {
        return try {
            val response = api.createPost(CreatePostRequest(content, attachmentIds))
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Ошибка создания поста: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun likePost(postId: String): Result<Unit> {
        return try {
            val response = api.likePost(postId)
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Ошибка"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun unlikePost(postId: String): Result<Unit> {
        return try {
            val response = api.unlikePost(postId)
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Ошибка"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun repostPost(postId: String): Result<Unit> {
        return try {
            val response = api.repostPost(postId)
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Ошибка"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deletePost(postId: String): Result<Unit> {
        return try {
            val response = api.deletePost(postId)
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Ошибка"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun markViewed(postId: String) {
        try {
            api.markPostViewed(postId)
        } catch (_: Exception) {}
    }

    suspend fun getComments(postId: String, limit: Int = 20, cursor: String? = null): Result<List<Post>> {
        return try {
            val response = api.getComments(postId, limit, cursor)
            if (response.isSuccessful) {
                Result.success(response.body()?.data?.posts ?: emptyList())
            } else {
                Result.failure(Exception("Ошибка загрузки комментариев"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createComment(postId: String, content: String): Result<Post> {
        return try {
            val response = api.createComment(postId, CreatePostRequest(content))
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Ошибка создания комментария"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadFile(file: File): Result<FileUploadResponse> {
        return try {
            val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("file", file.name, requestBody)
            val response = api.uploadFile(part)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Ошибка загрузки файла"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
