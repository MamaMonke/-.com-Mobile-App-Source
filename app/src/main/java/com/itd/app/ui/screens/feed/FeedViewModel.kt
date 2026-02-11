package com.itd.app.ui.screens.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itd.app.data.model.Clan
import com.itd.app.data.model.Post
import com.itd.app.data.model.UserSuggestion
import com.itd.app.data.repository.AuthRepository
import com.itd.app.data.repository.PostRepository
import com.itd.app.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FeedUiState(
    val posts: List<Post> = emptyList(),
    val topClans: List<Clan> = emptyList(),
    val suggestions: List<UserSuggestion> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val activeTab: String = "popular",
    val userAvatar: String = "👾",
    val isLoggedIn: Boolean = false
)

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val isLoggedIn = authRepository.isLoggedIn()
            _uiState.value = _uiState.value.copy(isLoggedIn = isLoggedIn)

            if (isLoggedIn) {
                authRepository.getProfile().onSuccess { profile ->
                    profile.user?.let { user ->
                        _uiState.value = _uiState.value.copy(userAvatar = user.avatar)
                    }
                }
            }

            loadPosts()
            loadTopClans()
            loadSuggestions()
        }
    }

    fun loadPosts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = _uiState.value.posts.isEmpty())
            postRepository.getPosts(tab = _uiState.value.activeTab).fold(
                onSuccess = { posts ->
                    _uiState.value = _uiState.value.copy(
                        posts = posts,
                        isLoading = false,
                        isRefreshing = false,
                        error = null
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isRefreshing = false,
                        error = error.message
                    )
                }
            )
        }
    }

    private fun loadTopClans() {
        viewModelScope.launch {
            userRepository.getTopClans().onSuccess { clans ->
                _uiState.value = _uiState.value.copy(topClans = clans)
            }
        }
    }

    private fun loadSuggestions() {
        viewModelScope.launch {
            userRepository.getWhoToFollow().onSuccess { suggestions ->
                _uiState.value = _uiState.value.copy(suggestions = suggestions)
            }
        }
    }

    fun switchTab(tab: String) {
        if (tab != _uiState.value.activeTab) {
            _uiState.value = _uiState.value.copy(activeTab = tab, posts = emptyList())
            loadPosts()
        }
    }

    fun refresh() {
        _uiState.value = _uiState.value.copy(isRefreshing = true)
        loadPosts()
        loadTopClans()
    }

    fun createPost(content: String) {
        viewModelScope.launch {
            postRepository.createPost(content).onSuccess { post ->
                _uiState.value = _uiState.value.copy(
                    posts = listOf(post) + _uiState.value.posts
                )
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

                if (newIsLiked) {
                    postRepository.likePost(postId)
                } else {
                    postRepository.unlikePost(postId)
                }
            }
        }
    }

    fun repostPost(postId: String) {
        viewModelScope.launch {
            postRepository.repostPost(postId)
        }
    }
}
