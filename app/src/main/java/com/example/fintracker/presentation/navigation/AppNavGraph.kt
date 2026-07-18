package com.example.fintracker.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.fintracker.domain.NavigationPath
import com.example.fintracker.presentation.screens.accounts.AccountsScreen
import com.example.fintracker.presentation.screens.analytics.AnalyticsScreen
import com.example.fintracker.presentation.screens.analytics.AnalyticsViewModel
import com.example.fintracker.presentation.screens.expenses.ExpensesScreen
import com.example.fintracker.presentation.screens.income.IncomesScreen

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier
) {
    val navController = LocalNavController.current
    val analyticsViewModel: AnalyticsViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = NavigationPath.EXPENSES.route,
        modifier = modifier
    ) {
        composable(NavigationPath.EXPENSES.route) {
            ExpensesScreen()
        }
        composable(NavigationPath.INCOME.route) {
            IncomesScreen()
        }
        composable(NavigationPath.ACCOUNTS.route) {
            AccountsScreen()
        }
        composable(NavigationPath.ANALYTICS.route) {
            AnalyticsScreen(viewModel = analyticsViewModel)
        }
    }
}
