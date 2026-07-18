package com.example.fintracker.presentation.common.components.scaffold

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.fintracker.presentation.navigation.LocalNavController
import com.example.fintracker.presentation.navigation.model.NavigationPath

@Composable
fun MainScreenScaffold(
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
            AddTransactionFloatingActionButton()
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            screenContent()
        }
    }
}
