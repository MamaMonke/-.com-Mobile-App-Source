package com.itd.app.ui.screens.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itd.app.data.model.HashtagItem
import com.itd.app.data.model.UserSuggestion
import com.itd.app.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ExploreUiState(
    val trendingHashtags: List<HashtagItem> = emptyList(),
    val searchResults: List<UserSuggestion> = emptyList(),
    val searchHashtags: List<HashtagItem> = emptyList(),
    val query: String = "",
    val isLoading: Boolean = false,
    val isSearching: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExploreUiState())
    val uiState: StateFlow<ExploreUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadTrending()
    }

    private fun loadTrending() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            userRepository.getTrendingHashtags().fold(
                onSuccess = { hashtags ->
                    _uiState.value = _uiState.value.copy(
                        trendingHashtags = hashtags,
                        isLoading = false
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

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(query = query)

        searchJob?.cancel()
        if (query.isBlank()) {
            _uiState.value = _uiState.value.copy(
                searchResults = emptyList(),
                searchHashtags = emptyList(),
                isSearching = false
            )
            return
        }

        searchJob = viewModelScope.launch {
            delay(300) // debounce
            _uiState.value = _uiState.value.copy(isSearching = true)
            userRepository.search(query).fold(
                onSuccess = { response ->
                    _uiState.value = _uiState.value.copy(
                        searchResults = response.users,
                        searchHashtags = response.hashtags,
                        isSearching = false
                    )
                },
                onFailure = {
                    // Fallback to user search
                    userRepository.searchUsers(query).onSuccess { users ->
                        _uiState.value = _uiState.value.copy(
                            searchResults = users,
                            isSearching = false
                        )
                    }.onFailure {
                        _uiState.value = _uiState.value.copy(isSearching = false)
                    }
                }
            )
        }
    }
}
