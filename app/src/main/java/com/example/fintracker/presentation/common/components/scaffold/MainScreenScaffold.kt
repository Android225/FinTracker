package com.example.fintracker.presentation.common.components.scaffold

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.fintracker.presentation.navigation.LocalNavController
import com.example.fintracker.presentation.common.components.feedback.NetworkStatusBanner
import com.example.fintracker.presentation.navigation.model.NavigationPath
import java.time.LocalDate

@Composable
fun MainScreenScaffold(
    selectedDate: LocalDate,
    snackbarHostState: SnackbarHostState,
    onAddClick: () -> Unit,
    screenContent: @Composable () -> Unit
) {
    val navController = LocalNavController.current

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        bottomBar = {
            MainBottomNavigationBar()
        },
        topBar = {
            MainTopAppBar(
                selectedDate = selectedDate,
                onMenuClick = {},
                onAnalyticsClick = {
                    navController.navigate(NavigationPath.ANALYTICS.route) {
                        launchSingleTop = true
                    }
                },
                onFilterClick = {}
            )
        },
        floatingActionButton = {
            AddTransactionFloatingActionButton(onClick = onAddClick)
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NetworkStatusBanner()
            Box(modifier = Modifier.weight(1f)) {
                screenContent()
            }
        }
    }
}
