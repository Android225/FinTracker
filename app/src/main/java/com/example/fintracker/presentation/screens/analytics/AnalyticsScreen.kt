package com.example.fintracker.presentation.screens.analytics

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fintracker.R
import com.example.fintracker.presentation.common.components.feedback.EmptyContent
import com.example.fintracker.presentation.common.components.feedback.ErrorSnackbarEffect
import com.example.fintracker.presentation.common.components.feedback.ErrorContent
import com.example.fintracker.presentation.common.components.feedback.LoadingContent
import com.example.fintracker.presentation.common.components.refresh.RefreshableContent
import com.example.fintracker.presentation.common.components.refresh.RefreshOnResumeEffect
import com.example.fintracker.presentation.common.model.UiState
import com.example.fintracker.presentation.navigation.LocalNavController
import com.example.fintracker.presentation.screens.analytics.components.sheets.AnalyticsSheetHost
import com.example.fintracker.presentation.screens.analytics.components.topbar.AnalyticsTopBar

@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel
) {
    val navController = LocalNavController.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val retry = { viewModel.onEvent(AnalyticsEvent.Refresh) }

    ErrorSnackbarEffect(
        errors = viewModel.errors,
        snackbarHostState = snackbarHostState,
        onRetry = retry
    )
    RefreshOnResumeEffect {
        viewModel.onEvent(AnalyticsEvent.ScreenVisible)
    }

    AnalyticsScreenContent(
        state = state.loadState,
        isRefreshing = state.isRefreshing,
        onRefresh = retry,
        onBackClick = { navController.popBackStack() },
        onEvent = viewModel::onEvent,
        snackbarHostState = snackbarHostState
    )
}

@Composable
fun AnalyticsScreenContent(
    state: UiState<AnalyticsContent>,
    onBackClick: () -> Unit,
    onEvent: (AnalyticsEvent) -> Unit,
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = {},
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    var sheet by remember { mutableStateOf<AnalyticsSheet?>(null) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { AnalyticsTopBar(onBackClick = onBackClick) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        RefreshableContent(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier.padding(innerPadding)
        ) {
            when (state) {
                UiState.Loading -> LoadingContent()
                is UiState.Empty -> {
                    val content = state.data
                    if (content == null) {
                        EmptyContent(text = stringResource(R.string.data_for_analytics_not_exist))
                    } else {
                        AnalyticsAvailableContent(
                            content = content,
                            sheet = sheet,
                            onSheetChange = { sheet = it },
                            onEvent = onEvent
                        )
                    }
                }
                is UiState.Error -> ErrorContent(
                    error = state.error,
                    onRetryClick = { onEvent(AnalyticsEvent.Refresh) }
                )
                is UiState.Content -> {
                    AnalyticsAvailableContent(
                        content = state.data,
                        sheet = sheet,
                        onSheetChange = { sheet = it },
                        onEvent = onEvent
                    )
                }
            }
        }
    }
}

// Показывает загруженную или пустую аналитику, не закрывая доступ к фильтрам.
@Composable
private fun AnalyticsAvailableContent(
    content: AnalyticsContent,
    sheet: AnalyticsSheet?,
    onSheetChange: (AnalyticsSheet?) -> Unit,
    onEvent: (AnalyticsEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    AnalyticsLoadedContent(
        content = content,
        modifier = modifier,
        onDonutClick = { onSheetChange(AnalyticsSheet.Detail) },
        onFilterClick = onSheetChange,
        onCurrencySelect = { currency ->
            onEvent(AnalyticsEvent.SelectCurrency(currency))
        }
    )

    sheet?.let { selectedSheet ->
        AnalyticsSheetHost(
            sheet = selectedSheet,
            content = content,
            onEvent = onEvent,
            onSheetChange = onSheetChange,
            onDismiss = { onSheetChange(null) }
        )
    }
}
