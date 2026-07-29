package com.android.tvapp.ui.list

import com.android.tvapp.MainDispatcherRule
import com.android.tvapp.domain.model.Show
import com.android.tvapp.fake.FakeShowsRepository
import com.android.tvapp.ui.common.UiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ShowListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: FakeShowsRepository

    @Before
    fun setUp() {
        repository = FakeShowsRepository()
    }

    private fun sampleShow(id: Int, title: String) = Show(
        id = id,
        title = title,
        url = "https://example.com/$id",
        posterUrl = null,
        fullPosterUrl = null,
        rating = 8.0,
        summary = null,
        premiered = null
    )

    @Test
    fun `loadShows updates state to success when repository call succeeds`() {
        val shows = listOf(sampleShow(1, "Breaking Bad"))
        repository.showsResult = Result.success(shows)

        val viewModel = ShowListViewModel(repository)

        val state = viewModel.uiState.value
        assertTrue(state is UiState.Success)
        assertEquals(shows, (state as UiState.Success).data)
    }

    @Test
    fun `loadShows updates state to error when repository call fails`() {
        repository.showsResult = Result.failure(Exception("Network error"))

        val viewModel = ShowListViewModel(repository)

        val state = viewModel.uiState.value
        assertTrue(state is UiState.Error)
        assertEquals("Network error", (state as UiState.Error).message)
    }

    @Test
    fun `loadNextPage appends new shows to existing list`() {
        val firstPage = listOf(sampleShow(1, "Breaking Bad"))
        val secondPage = listOf(sampleShow(2, "Better Call Saul"))
        repository.showsResult = Result.success(firstPage)
        repository.nextPageResults[1] = Result.success(secondPage)

        val viewModel = ShowListViewModel(repository)
        viewModel.loadNextPage()

        val state = viewModel.uiState.value
        assertTrue(state is UiState.Success)
        assertEquals(firstPage + secondPage, (state as UiState.Success).data)
    }

    @Test
    fun `loadNextPage keeps existing list when next page is empty`() {
        val firstPage = listOf(sampleShow(1, "Breaking Bad"))
        repository.showsResult = Result.success(firstPage)
        repository.nextPageResults[1] = Result.success(emptyList())

        val viewModel = ShowListViewModel(repository)
        viewModel.loadNextPage()

        val state = viewModel.uiState.value
        assertTrue(state is UiState.Success)
        assertEquals(firstPage, (state as UiState.Success).data)
    }

    @Test
    fun `isLoadingMore is false after loadNextPage finishes`() {
        repository.showsResult = Result.success(listOf(sampleShow(1, "Breaking Bad")))
        repository.nextPageResults[1] = Result.success(listOf(sampleShow(2, "Better Call Saul")))

        val viewModel = ShowListViewModel(repository)
        viewModel.loadNextPage()

        assertEquals(false, viewModel.isLoadingMore.value)
    }
}
