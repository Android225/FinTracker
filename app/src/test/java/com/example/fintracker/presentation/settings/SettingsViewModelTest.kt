package com.example.fintracker.presentation.settings

import com.example.fintracker.domain.Category
import com.example.fintracker.domain.repository.CategoriesRepository
import com.example.fintracker.domain.result.DataResult
import com.example.fintracker.test.FakeSettingsRepository
import com.example.fintracker.test.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `changing pin requires current pin and matching confirmation`() = runTest {
        val settingsRepository = FakeSettingsRepository(initialPin = "1234")
        val viewModel = SettingsViewModel(settingsRepository, EmptyCategoriesRepository)
        val collectJob = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.state.collect {}
        }
        runCurrent()

        viewModel.openSheet(SettingsSheet.Pin)
        assertEquals(PinSettingsStep.VerifyCurrent, viewModel.state.value.pinStep)

        viewModel.submitPin("0000")
        runCurrent()
        assertEquals(PinSettingsError.WrongCurrent, viewModel.state.value.pinError)
        assertEquals(PinSettingsStep.VerifyCurrent, viewModel.state.value.pinStep)

        viewModel.submitPin("1234")
        runCurrent()
        assertNull(viewModel.state.value.pinError)
        assertEquals(PinSettingsStep.EnterNew, viewModel.state.value.pinStep)

        viewModel.submitPin("5678")
        assertEquals(PinSettingsStep.ConfirmNew, viewModel.state.value.pinStep)

        viewModel.submitPin("9999")
        assertEquals(PinSettingsError.ConfirmationMismatch, viewModel.state.value.pinError)
        assertEquals("1234", settingsRepository.savedPin)

        viewModel.submitPin("5678")
        runCurrent()
        assertEquals("5678", settingsRepository.savedPin)
        assertEquals(SettingsSheet.Main, viewModel.state.value.sheet)

        collectJob.cancel()
    }

    private object EmptyCategoriesRepository : CategoriesRepository {
        override suspend fun getCategories(): DataResult<List<Category>> =
            DataResult.Success(emptyList())

        override fun observeCategories(): Flow<List<Category>> = flowOf(emptyList())

        override suspend fun getCategories(isIncome: Boolean): DataResult<List<Category>> =
            DataResult.Success(emptyList())
    }
}
