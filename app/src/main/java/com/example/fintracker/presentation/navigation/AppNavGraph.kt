package com.example.fintracker.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.fintracker.presentation.screens.expenses.ExpensesScreen

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier
) {
    val navController = LocalNavController.current

    NavHost(
        navController = navController,
        startDestination = Screen.Expenses.route,
        modifier = modifier
    ) {
        composable(Screen.Expenses.route) {
            ExpensesScreen()
        }
        composable(Screen.Income.route) {
          //  IncomeScreen()
        }
        composable(Screen.Accounts.route) {
            // AccountsScreen()
        }
    }
}