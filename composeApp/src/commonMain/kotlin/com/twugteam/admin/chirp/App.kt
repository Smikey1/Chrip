package com.twugteam.admin.chirp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.twugteam.admin.auth.presentation.navigation.AuthGraphRoute
import com.twugteam.admin.chat.presentation.navigation.ChatGraphRoute
import com.twugteam.admin.chirp.navigation.IOSDeepLinkListener
import com.twugteam.admin.chirp.navigation.NavigationRoot
import com.twugteam.admin.core.designsystem.theme.ChirpTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App(
    onAuthenticationChecked: () -> Unit = {},
    viewModel: MainViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val navController = rememberNavController()

    IOSDeepLinkListener(
        navController = navController
    )

    LaunchedEffect(state.isCheckingAuth) {
        if (!state.isCheckingAuth) {
            onAuthenticationChecked()
        }
    }

    ChirpTheme {
        if (!state.isCheckingAuth) {
            NavigationRoot(
                navController = navController,
                startDestination = if (state.isLoggedIn) {
                    ChatGraphRoute.ChatGraph
                } else {
                    AuthGraphRoute.AuthGraph
                }
            )
        }
    }
}
