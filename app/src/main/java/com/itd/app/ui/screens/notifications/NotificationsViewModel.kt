package com.itd.app.ui.screens.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itd.app.data.model.Notification
import com.itd.app.data.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NotificationsUiState(
    val notifications: List<Notification> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val activeTab: String = "all",
    val unreadCount: Int = 0
)

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    init {
        loadNotifications()
        loadUnreadCount()
    }

    fun loadNotifications() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val type = if (_uiState.value.activeTab == "mentions") "mention" else null
            notificationRepository.getNotifications(type = type).fold(
                onSuccess = { notifications ->
                    _uiState.value = _uiState.value.copy(
                        notifications = notifications,
                        isLoading = false,
                        error = null
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message
                    )
                }
            )
        }
    }

    fun loadUnreadCount() {
        viewModelScope.launch {
            notificationRepository.getNotificationCount().onSuccess { count ->
                _uiState.value = _uiState.value.copy(unreadCount = count)
            }
        }
    }

    fun switchTab(tab: String) {
        if (tab != _uiState.value.activeTab) {
            _uiState.value = _uiState.value.copy(activeTab = tab, notifications = emptyList())
            loadNotifications()
        }
    }

    fun markAllRead() {
        viewModelScope.launch {
            notificationRepository.markAllRead().onSuccess {
                _uiState.value = _uiState.value.copy(unreadCount = 0)
                loadNotifications()
            }
        }
    }
}
