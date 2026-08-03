package com.example.fintracker.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.fintracker.presentation.navigation.model.NavigationPath
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

    NavHost(
        navController = navController,
        startDestination = NavigationPath.EXPENSES.route,
        route = NavigationPath.ROOT.route,
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
        composable(NavigationPath.ANALYTICS.route) { backStackEntry ->
            val rootEntry = remember(backStackEntry) {
                navController.getBackStackEntry(NavigationPath.ROOT.route)
            }
            val viewModel: AnalyticsViewModel = hiltViewModel(rootEntry)
            AnalyticsScreen(viewModel = viewModel)
        }
    }
}
