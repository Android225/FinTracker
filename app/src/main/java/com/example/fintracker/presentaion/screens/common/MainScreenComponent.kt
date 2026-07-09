package com.example.fintracker.presentaion.screens.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier

@Composable
fun MainCommonScreen(
    screenContent: @Composable () -> Unit
) {
    Scaffold(
        bottomBar = {

        },
        topBar = {

        }
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