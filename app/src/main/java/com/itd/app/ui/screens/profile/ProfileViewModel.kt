package com.itd.app.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itd.app.data.model.Post
import com.itd.app.data.model.User
import com.itd.app.data.repository.AuthRepository
import com.itd.app.data.repository.PostRepository
import com.itd.app.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val user: User? = null,
    val posts: List<Post> = emptyList(),
    val isLoading: Boolean = false,
    val isOwnProfile: Boolean = false,
    val error: String? = null,
    val activeTab: String = "posts",
    val isFollowing: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val postRepository: PostRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun loadProfile(username: String?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val targetUsername = username ?: authRepository.getUsername()
            if (targetUsername == null) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Необходимо войти в аккаунт"
                )
                return@launch
            }

            val myUsername = authRepository.getUsername()
            val isOwn = targetUsername == myUsername

            userRepository.getUser(targetUsername).fold(
                onSuccess = { user ->
                    _uiState.value = _uiState.value.copy(
                        user = user,
                        isOwnProfile = isOwn,
                        isFollowing = user.isFollowing,
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

            loadPosts(targetUsername)
        }
    }

    private fun loadPosts(username: String) {
        viewModelScope.launch {
            postRepository.getUserPosts(username).onSuccess { posts ->
                _uiState.value = _uiState.value.copy(posts = posts)
            }
        }
    }

    fun switchTab(tab: String) {
        _uiState.value = _uiState.value.copy(activeTab = tab)
    }

    fun toggleFollow() {
        val user = _uiState.value.user ?: return
        viewModelScope.launch {
            val isCurrentlyFollowing = _uiState.value.isFollowing
            _uiState.value = _uiState.value.copy(
                isFollowing = !isCurrentlyFollowing,
                user = user.copy(
                    followersCount = user.followersCount + if (isCurrentlyFollowing) -1 else 1
                )
            )

            if (isCurrentlyFollowing) {
                userRepository.unfollowUser(user.username)
            } else {
                userRepository.followUser(user.username)
            }
        }
    }

    fun likePost(postId: String) {
        viewModelScope.launch {
            val posts = _uiState.value.posts.toMutableList()
            val index = posts.indexOfFirst { it.id == postId }
            if (index != -1) {
                val post = posts[index]
                val newIsLiked = !post.isLiked
                val newCount = if (newIsLiked) post.likesCount + 1 else post.likesCount - 1
                posts[index] = post.copy(isLiked = newIsLiked, likesCount = newCount)
                _uiState.value = _uiState.value.copy(posts = posts)

                if (newIsLiked) postRepository.likePost(postId)
                else postRepository.unlikePost(postId)
            }
        }
    }

    fun deletePost(postId: String) {
        viewModelScope.launch {
            postRepository.deletePost(postId).onSuccess {
                _uiState.value = _uiState.value.copy(
                    posts = _uiState.value.posts.filter { it.id != postId }
                )
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}
