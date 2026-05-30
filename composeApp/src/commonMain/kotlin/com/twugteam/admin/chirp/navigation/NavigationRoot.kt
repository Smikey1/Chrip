package com.twugteam.admin.chirp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.twugteam.admin.auth.presentation.navigation.AuthGraphRoute
import com.twugteam.admin.auth.presentation.navigation.authGraph
import com.twugteam.admin.chat.presentation.navigation.ChatGraphRoute
import com.twugteam.admin.chat.presentation.navigation.chatGraph

@Composable
fun NavigationRoot(
    startDestination: Any,
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        authGraph(
            navController = navController,
            onLoginSuccess = {
                navController.navigate(ChatGraphRoute.ChatGraph) {
                    popUpTo(AuthGraphRoute.AuthGraph) {
                        inclusive = true
                    }
                }
            }
        )
        chatGraph(
            navController = navController
        )
    }
}