package com.example.fintracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.fintracker.presentation.navigation.AppNavGraph
import com.example.fintracker.presentation.navigation.LocalNavController
import com.example.fintracker.presentation.screens.common.MainCommonScreen
import com.example.fintracker.presentation.theme.FinTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FinTrackerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()

                    // Предоставляем navController через CompositionLocal
                    CompositionLocalProvider(LocalNavController provides navController) {
                        MainCommonScreen {
                            AppNavGraph()
                        }
                    }
                }
            }
        }
    }
}
