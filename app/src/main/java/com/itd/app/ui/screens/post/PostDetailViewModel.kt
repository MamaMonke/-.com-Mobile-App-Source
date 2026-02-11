package com.itd.app.ui.screens.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itd.app.data.model.Post
import com.itd.app.data.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PostDetailUiState(
    val post: Post? = null,
    val comments: List<Post> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    private val postRepository: PostRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PostDetailUiState())
    val uiState: StateFlow<PostDetailUiState> = _uiState.asStateFlow()

    fun loadPost(postId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            postRepository.markViewed(postId)
            loadComments(postId)
        }
    }

    private fun loadComments(postId: String) {
        viewModelScope.launch {
            postRepository.getComments(postId).fold(
                onSuccess = { comments ->
                    _uiState.value = _uiState.value.copy(
                        comments = comments,
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

    fun createComment(postId: String, content: String) {
        viewModelScope.launch {
            postRepository.createComment(postId, content).onSuccess { comment ->
                _uiState.value = _uiState.value.copy(
                    comments = _uiState.value.comments + comment
                )
            }
        }
    }

    fun likePost(postId: String) {
        viewModelScope.launch {
            val post = _uiState.value.post
            if (post != null && post.id == postId) {
                val newIsLiked = !post.isLiked
                val newCount = if (newIsLiked) post.likesCount + 1 else post.likesCount - 1
                _uiState.value = _uiState.value.copy(
                    post = post.copy(isLiked = newIsLiked, likesCount = newCount)
                )
                if (newIsLiked) postRepository.likePost(postId)
                else postRepository.unlikePost(postId)
            }
        }
    }
}
