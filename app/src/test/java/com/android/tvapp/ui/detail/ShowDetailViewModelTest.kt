package com.android.tvapp.ui.detail

import com.android.tvapp.MainDispatcherRule
import com.android.tvapp.domain.model.Show
import com.android.tvapp.fake.FakeShowsRepository
import com.android.tvapp.ui.common.UiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ShowDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: FakeShowsRepository
    private lateinit var viewModel: ShowDetailViewModel

    @Before
    fun setUp() {
        repository = FakeShowsRepository()
        viewModel = ShowDetailViewModel(repository)
    }

    private fun sampleShow(id: Int) = Show(
        id = id,
        title = "Breaking Bad",
        url = "https://example.com/$id",
        posterUrl = null,
        fullPosterUrl = null,
        rating = 9.5,
        summary = null,
        premiered = null
    )

    @Test
    fun `loadShowDetail updates state to success when repository call succeeds`() {
        val show = sampleShow(1)
        repository.showDetailResult = Result.success(show)

        viewModel.loadShowDetail(1)

        val state = viewModel.uiState.value
        assertTrue(state is UiState.Success)
        assertEquals(show, (state as UiState.Success).data)
    }

    @Test
    fun `loadShowDetail updates state to error when repository call fails`() {
        repository.showDetailResult = Result.failure(Exception("Show not found"))

        viewModel.loadShowDetail(1)

        val state = viewModel.uiState.value
        assertTrue(state is UiState.Error)
        assertEquals("Show not found", (state as UiState.Error).message)
    }
}
