package com.example.fintracker.presentation.common.model

import com.example.fintracker.domain.error.AppError
import com.example.fintracker.domain.result.DataResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ScreenUiStateTest {
    @Test
    fun `initial failure becomes error state`() = runTest {
        val state = MutableStateFlow(ScreenUiState<String>())
        val errors = mutableListOf<AppError>()

        state.updateFrom(
            result = DataResult.Failure(AppError.Network),
            previousState = UiState.Loading,
            isEmpty = String::isEmpty,
            onRefreshError = errors::add
        )

        assertEquals(UiState.Error(AppError.Network), state.value.loadState)
        assertEquals(false, state.value.isRefreshing)
        assertEquals(emptyList<AppError>(), errors)
    }

    @Test
    fun `refresh failure preserves content and emits error`() = runTest {
        val content = UiState.Content("cached")
        val state = MutableStateFlow(ScreenUiState(loadState = content))
        val errors = mutableListOf<AppError>()
        val previousState = state.startRefreshing()

        assertEquals(true, state.value.isRefreshing)

        state.updateFrom(
            result = DataResult.Failure(AppError.Server),
            previousState = previousState,
            isEmpty = String::isEmpty,
            onRefreshError = errors::add
        )

        assertEquals(content, state.value.loadState)
        assertEquals(false, state.value.isRefreshing)
        assertEquals(listOf(AppError.Server), errors)
    }

    @Test
    fun `successful empty data becomes empty state`() = runTest {
        val state = MutableStateFlow(ScreenUiState<List<Int>>())

        state.updateFrom(
            result = DataResult.Success(emptyList()),
            previousState = UiState.Loading,
            isEmpty = List<Int>::isEmpty,
            onRefreshError = {}
        )

        assertEquals(UiState.Empty(emptyList<Int>()), state.value.loadState)
    }

    @Test
    fun `successful refresh replaces old content and stops refreshing`() = runTest {
        val state = MutableStateFlow(ScreenUiState(loadState = UiState.Content("old")))
        val previousState = state.startRefreshing()

        state.updateFrom(
            result = DataResult.Success("new"),
            previousState = previousState,
            isEmpty = String::isEmpty,
            onRefreshError = {}
        )

        assertEquals(UiState.Content("new"), state.value.loadState)
        assertEquals(false, state.value.isRefreshing)
    }
}
