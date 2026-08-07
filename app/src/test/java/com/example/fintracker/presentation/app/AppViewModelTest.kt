package com.example.fintracker.presentation.app

import com.example.fintracker.test.FakeSettingsRepository
import com.example.fintracker.test.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AppViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `five rejected pins temporarily block further attempts`() = runTest {
        val viewModel = AppViewModel(FakeSettingsRepository(initialPin = "1234"))
        val collectJob = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        runCurrent()
        assertTrue(viewModel.uiState.value.requiresUnlock)

        repeat(5) {
            viewModel.verifyPin("0000")
            runCurrent()
        }

        assertEquals(30, viewModel.uiState.value.lockoutRemainingSeconds)
        viewModel.verifyPin("1234")
        runCurrent()
        assertTrue(viewModel.uiState.value.requiresUnlock)

        advanceTimeBy(30_000)
        runCurrent()
        assertEquals(0, viewModel.uiState.value.lockoutRemainingSeconds)

        viewModel.verifyPin("1234")
        runCurrent()
        assertFalse(viewModel.uiState.value.requiresUnlock)

        collectJob.cancel()
    }
}
