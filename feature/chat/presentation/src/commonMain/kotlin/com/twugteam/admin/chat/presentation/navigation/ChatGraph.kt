package com.twugteam.admin.chat.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.twugteam.admin.chat.presentation.chat_list_detail.ChatListDetailAdaptiveScreen

fun NavGraphBuilder.chatGraph(
    navController: NavController
) {
    navigation<ChatGraphRoute.ChatGraph>(
        startDestination = ChatGraphRoute.ChatListDetail
    ) {
        composable<ChatGraphRoute.ChatListDetail> {
            ChatListDetailAdaptiveScreen(
                onConfirmLogoutClick = {
                    // TODO #1: Need to implement Logout User
                }
            )
        }
    }
}