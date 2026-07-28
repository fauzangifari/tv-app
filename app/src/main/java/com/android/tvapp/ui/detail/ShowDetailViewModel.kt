package com.android.tvapp.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.tvapp.domain.model.Show
import com.android.tvapp.domain.repository.ShowsRepository
import com.android.tvapp.ui.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ShowDetailViewModel(
    private val repository: ShowsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<Show>>(UiState.Loading)
    val uiState: StateFlow<UiState<Show>> = _uiState.asStateFlow()

    fun loadShowDetail(id: Int) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.getShowDetail(id)
                .onSuccess { show -> _uiState.value = UiState.Success(show) }
                .onFailure { error -> _uiState.value = UiState.Error(error.message ?: "Something went wrong") }
        }
    }
}
