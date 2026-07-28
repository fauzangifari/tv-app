package com.android.tvapp.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.tvapp.domain.model.Show
import com.android.tvapp.domain.repository.ShowsRepository
import com.android.tvapp.ui.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ShowListViewModel(
    private val repository: ShowsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<Show>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Show>>> = _uiState.asStateFlow()

    init {
        loadShows()
    }

    fun loadShows() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.getShows()
                .onSuccess { shows -> _uiState.value = UiState.Success(shows) }
                .onFailure { error -> _uiState.value = UiState.Error(error.message ?: "Something went wrong") }
        }
    }
}
