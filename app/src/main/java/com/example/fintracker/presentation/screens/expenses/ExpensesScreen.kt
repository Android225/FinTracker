package com.example.fintracker.presentation.screens.expenses

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.fintracker.presentation.navigation.LocalNavController
import com.example.fintracker.presentation.screens.common.MainCommonScreen

@Composable
fun ExpensesScreen(){
    MainCommonScreen{
    }
}

//@Preview
//@Composable
//fun PreviewExpensesScreen(){
//    val navController = rememberNavController()
//    CompositionLocalProvider(
//        LocalNavController provides navController
//    ) {
//        ExpensesScreen()
//    }
//}