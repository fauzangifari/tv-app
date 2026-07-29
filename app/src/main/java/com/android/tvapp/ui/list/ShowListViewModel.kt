package com.android.tvapp.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.tvapp.domain.model.Show
import com.android.tvapp.domain.repository.ShowsRepository
import com.android.tvapp.ui.common.UiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ShowListViewModel(
    private val repository: ShowsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<Show>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Show>>> = _uiState.asStateFlow()

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()

    private var currentPage = 0
    private var endReached = false
    private var loadMoreJob: Job? = null

    init {
        loadShows()
    }

    fun loadShows() {
        currentPage = 0
        endReached = false
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.getShows(currentPage)
                .onSuccess { shows -> _uiState.value = UiState.Success(shows) }
                .onFailure { error -> _uiState.value = UiState.Error(error.message ?: "Something went wrong") }
        }
    }

    fun loadNextPage() {
        val loadedShows = (_uiState.value as? UiState.Success)?.data ?: return
        if (_isLoadingMore.value || endReached || loadMoreJob?.isActive == true) return

        loadMoreJob = viewModelScope.launch {
            _isLoadingMore.value = true
            val nextPage = currentPage + 1
            repository.getShows(nextPage)
                .onSuccess { newShows ->
                    if (newShows.isEmpty()) {
                        endReached = true
                    } else {
                        currentPage = nextPage
                        _uiState.value = UiState.Success(loadedShows + newShows)
                    }
                }
            _isLoadingMore.value = false
        }
    }
}
