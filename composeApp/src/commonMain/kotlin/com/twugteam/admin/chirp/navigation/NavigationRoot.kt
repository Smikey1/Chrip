package com.twugteam.admin.chirp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.twugteam.admin.auth.presentation.navigation.AuthGraphRoute
import com.twugteam.admin.auth.presentation.navigation.authGraph

@Composable
fun NavigationRoot(
    navController: NavHostController
) {
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