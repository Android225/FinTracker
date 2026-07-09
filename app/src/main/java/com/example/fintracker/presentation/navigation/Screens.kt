package com.example.fintracker.presentation.navigation

sealed class Screen(val route: String) {
    data object Expenses : Screen("expenses")
    data object Income : Screen("income")
    data object Accounts : Screen("accounts")
}