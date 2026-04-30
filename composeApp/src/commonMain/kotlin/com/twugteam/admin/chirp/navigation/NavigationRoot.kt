package com.twugteam.admin.chirp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.twugteam.admin.auth.presentation.navigation.AuthGraphRoute
import com.twugteam.admin.auth.presentation.navigation.authGraph
import com.twugteam.admin.chat.presentation.chat_list.ChatListScreenRoute
import com.twugteam.admin.chat.presentation.navigation.ChatGraphRoute

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
                navController.navigate(ChatGraphRoute.ChatGraph){
                    popUpTo(AuthGraphRoute.AuthGraph){
                        inclusive = true
                    }
                }
            }
        )
        composable<ChatGraphRoute.ChatGraph> {
            ChatListScreenRoute()
        }
    }
}