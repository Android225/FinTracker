package com.example.fintracker.presentation.screens.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.fintracker.domain.NavigationPath
import com.example.fintracker.presentation.navigation.LocalNavController

@Composable
fun MainCommonScreen(
    screenContent: @Composable () -> Unit
) {
    val navController = LocalNavController.current

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        bottomBar = {
            BottomNavigationBar()
        },
        topBar = {
            TopAppBar(
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
            FloatingActionButton()
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
