package com.example.fintracker.presentation.screens.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import com.example.fintracker.domain.BottomNavItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.fintracker.presentation.navigation.LocalNavController
import androidx.compose.runtime.getValue

@Composable
fun BottomNavigationBar(
    modifier: Modifier = Modifier
) {
    val navController = LocalNavController.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
  
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
    ) {
        BottomNavItem.entries.forEach { item ->
            val isSelected = currentRoute == item.route.name

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (currentRoute != item.route.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(id = item.iconRes),
                        contentDescription = stringResource(item.label),
                        //modifier = Modifier.size(24.dp)
                    )
                },
                label = {  Text(text = stringResource(item.label)) }

            )
        }
    }
}