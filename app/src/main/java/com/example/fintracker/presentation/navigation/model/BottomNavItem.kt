package com.example.fintracker.presentation.navigation.model

import androidx.annotation.StringRes
import com.example.fintracker.R

enum class BottomNavItem(
    val iconRes:Int,
    @param:StringRes val label: Int,
    val route:NavigationPath
) {
    EXPENSES(
        iconRes = R.drawable.expenses,
        label = R.string.bottom_nav_expenses,
        route = NavigationPath.EXPENSES
    ),
    INCOME(
        iconRes = R.drawable.income,
        label = R.string.bottom_nav_income,
        route = NavigationPath.INCOME
    ),
    ACCOUNTS(
        iconRes = R.drawable.accounts,
        label = R.string.bottom_nav_account,
        route = NavigationPath.ACCOUNTS
    )
}
