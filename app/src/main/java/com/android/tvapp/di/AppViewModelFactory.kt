package com.android.tvapp.di

import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.android.tvapp.data.repository.ShowsRepositoryImpl
import com.android.tvapp.ui.detail.ShowDetailViewModel
import com.android.tvapp.ui.list.ShowListViewModel

object AppViewModelFactory {

    private val repository by lazy { ShowsRepositoryImpl(NetworkModule.apiService) }

    val factory = viewModelFactory {
        initializer { ShowListViewModel(repository) }
        initializer { ShowDetailViewModel(repository) }
    }
}
