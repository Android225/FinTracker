package com.example.fintracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.example.fintracker.presentation.navigation.AppNavGraph
import com.example.fintracker.presentation.navigation.LocalNavController
import com.example.fintracker.presentation.screens.splash.SplashScreen
import com.example.fintracker.presentation.theme.FinTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            FinTrackerTheme {
                var showSplash by remember { mutableStateOf(true) }
                if (showSplash) {
                    SplashScreen(
                        onSplashFinished = { showSplash = false }
                    )
                } else {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        val navController = rememberNavController()
                        CompositionLocalProvider(LocalNavController provides navController) {
                            AppNavGraph()
                        }
                    }
                }
            }
        }
    }
}
