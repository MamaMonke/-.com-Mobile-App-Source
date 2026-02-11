package com.itd.app.data.repository

import com.itd.app.data.api.ITDApiService
import com.itd.app.data.model.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val api: ITDApiService
) {
    suspend fun getUser(username: String): Result<User> {
        return try {
            val response = api.getUserByUsername(username)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Пользователь не найден"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTopClans(): Result<List<Clan>> {
        return try {
            val response = api.getTopClans()
            if (response.isSuccessful) {
                Result.success(response.body()?.clans ?: emptyList())
            } else {
                Result.failure(Exception("Ошибка загрузки кланов"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getWhoToFollow(): Result<List<UserSuggestion>> {
        return try {
            val response = api.getWhoToFollow()
            if (response.isSuccessful) {
                Result.success(response.body()?.users ?: emptyList())
            } else {
                Result.failure(Exception("Ошибка загрузки рекомендаций"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun followUser(username: String): Result<Unit> {
        return try {
            val response = api.followUser(username)
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Ошибка подписки"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun unfollowUser(username: String): Result<Unit> {
        return try {
            val response = api.unfollowUser(username)
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Ошибка отписки"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchUsers(query: String): Result<List<UserSuggestion>> {
        return try {
            val response = api.searchUsers(query)
            if (response.isSuccessful) {
                Result.success(response.body()?.users ?: emptyList())
            } else {
                Result.failure(Exception("Ошибка поиска"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun search(query: String): Result<SearchResponse> {
        return try {
            val response = api.search(query)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Ошибка поиска"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTrendingHashtags(): Result<List<HashtagItem>> {
        return try {
            val response = api.getTrendingHashtags()
            if (response.isSuccessful) {
                val body = response.body()
                Result.success(body?.hashtags ?: body?.data ?: emptyList())
            } else {
                Result.failure(Exception("Ошибка загрузки хэштегов"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProfile(updates: Map<String, String>): Result<User> {
        return try {
            val response = api.updateProfile(updates)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Ошибка обновления профиля"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
