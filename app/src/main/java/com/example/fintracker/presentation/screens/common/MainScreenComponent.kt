package com.example.fintracker.presentation.screens.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun MainCommonScreen(
    screenContent: @Composable () -> Unit
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        bottomBar = {
            BottomNavigationBar()
        },
        topBar = {
            TopAppBar(
                onMenuClick = {},
                onAnalyticsClick = {},
                onFilterClick = {}
            )
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