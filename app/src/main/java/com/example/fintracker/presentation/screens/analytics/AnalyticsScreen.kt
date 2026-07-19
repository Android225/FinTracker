package com.example.fintracker.presentation.screens.analytics

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.fintracker.presentation.common.components.feedback.EmptyContent
import com.example.fintracker.presentation.common.components.feedback.ErrorContent
import com.example.fintracker.presentation.common.components.feedback.LoadingContent
import com.example.fintracker.presentation.common.model.UiState
import com.example.fintracker.presentation.navigation.LocalNavController
import com.example.fintracker.presentation.screens.analytics.components.sheets.AnalyticsSheetHost
import com.example.fintracker.presentation.screens.analytics.components.topbar.AnalyticsTopBar
import com.example.fintracker.presentation.screens.analytics.model.AnalyticsContent
import com.example.fintracker.presentation.screens.analytics.model.AnalyticsEvent
import com.example.fintracker.presentation.screens.analytics.model.AnalyticsSheet

// Подключает ViewModel и навигацию экрана аналитики.
@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel
) {
    val navController = LocalNavController.current
    val state by viewModel.uiState.collectAsState()

    AnalyticsScreenContent(
        state = state,
        onBackClick = { navController.popBackStack() },
        onEvent = viewModel::onEvent
    )
}

// Отображает аналитику для текущего состояния загрузки.
@Composable
fun AnalyticsScreenContent(
    state: UiState<AnalyticsContent>,
    onBackClick: () -> Unit,
    onEvent: (AnalyticsEvent) -> Unit
) {
    var sheet by remember { mutableStateOf<AnalyticsSheet?>(null) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { AnalyticsTopBar(onBackClick = onBackClick) }
    ) { innerPadding ->
        when (state) {
            UiState.Loading -> LoadingContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
            UiState.Empty -> EmptyContent(
                text = "Данных для аналитики пока нет",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
            is UiState.Error -> ErrorContent(
                error = state.error,
                onRetryClick = { onEvent(AnalyticsEvent.Refresh) },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
            is UiState.Content -> {
                AnalyticsLoadedContent(
                    content = state.data,
                    modifier = Modifier.padding(innerPadding),
                    onDonutClick = { sheet = AnalyticsSheet.Detail },
                    onFilterClick = { sheet = it }
                )

                sheet?.let { selectedSheet ->
                    AnalyticsSheetHost(
                        sheet = selectedSheet,
                        content = state.data,
                        onEvent = onEvent,
                        onSheetChange = { sheet = it },
                        onDismiss = { sheet = null }
                    )
                }
            }
        }
    }
}
