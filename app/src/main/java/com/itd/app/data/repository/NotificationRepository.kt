package com.itd.app.data.repository

import com.itd.app.data.api.ITDApiService
import com.itd.app.data.model.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    private val api: ITDApiService
) {
    suspend fun getNotifications(limit: Int = 20, cursor: String? = null, type: String? = null): Result<List<Notification>> {
        return try {
            val response = api.getNotifications(limit, cursor, type)
            if (response.isSuccessful) {
                val body = response.body()
                Result.success(body?.notifications ?: body?.data ?: emptyList())
            } else {
                Result.failure(Exception("Ошибка загрузки уведомлений: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getNotificationCount(): Result<Int> {
        return try {
            val response = api.getNotificationCount()
            if (response.isSuccessful) {
                Result.success(response.body()?.count ?: 0)
            } else {
                Result.success(0)
            }
        } catch (e: Exception) {
            Result.success(0)
        }
    }

    suspend fun markAllRead(): Result<Unit> {
        return try {
            val response = api.markAllNotificationsRead()
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Ошибка"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun markRead(ids: List<String>): Result<Unit> {
        return try {
            val response = api.markNotificationsRead(mapOf("ids" to ids))
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Ошибка"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
