package com.twugteam.admin.chirp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.twugteam.admin.auth.presentation.navigation.AuthGraphRoute
import com.twugteam.admin.auth.presentation.navigation.authGraph

@Composable
fun NavigationRoot() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AuthGraphRoute.AuthGraph
    ) {
        authGraph(
            navController = navController,
            onLoginSuccess = {

            }
        )
    }
}