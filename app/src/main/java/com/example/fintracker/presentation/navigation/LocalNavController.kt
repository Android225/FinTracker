package com.example.fintracker.presentation.navigation

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController

val LocalNavController = staticCompositionLocalOf<NavHostController> {
    error("NavController not provided. Make sure to wrap your app with CompositionLocalProvider.")
}
