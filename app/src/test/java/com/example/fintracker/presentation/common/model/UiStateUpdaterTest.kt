package com.example.fintracker.presentation.common.model

import com.example.fintracker.domain.error.AppError
import com.example.fintracker.domain.result.DataResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class UiStateUpdaterTest {
    @Test
    fun `initial failure becomes error state`() = runTest {
        val state = MutableStateFlow(RefreshableUiState<String>())
        val effects = mutableListOf<ScreenEffect.ShowError>()

        state.updateFrom(
            result = DataResult.Failure(AppError.Network),
            previousState = UiState.Loading,
            isEmpty = String::isEmpty,
            onRefreshError = effects::add
        )

        assertEquals(UiState.Error(AppError.Network), state.value.dataState)
        assertEquals(false, state.value.isRefreshing)
        assertEquals(emptyList<ScreenEffect>(), effects)
    }

    @Test
    fun `refresh failure preserves content and emits error effect`() = runTest {
        val content = UiState.Content("cached")
        val state = MutableStateFlow(RefreshableUiState(dataState = content))
        val effects = mutableListOf<ScreenEffect.ShowError>()
        val previousState = state.startRefreshing()

        assertEquals(true, state.value.isRefreshing)

        state.updateFrom(
            result = DataResult.Failure(AppError.Server),
            previousState = previousState,
            isEmpty = String::isEmpty,
            onRefreshError = effects::add
        )

        assertEquals(content, state.value.dataState)
        assertEquals(false, state.value.isRefreshing)
        assertEquals(listOf(ScreenEffect.ShowError(AppError.Server)), effects)
    }

    @Test
    fun `successful empty data becomes empty state`() = runTest {
        val state = MutableStateFlow(RefreshableUiState<List<Int>>())

        state.updateFrom(
            result = DataResult.Success(emptyList()),
            previousState = UiState.Loading,
            isEmpty = List<Int>::isEmpty,
            onRefreshError = {}
        )

        assertEquals(UiState.Empty(emptyList<Int>()), state.value.dataState)
    }
}
