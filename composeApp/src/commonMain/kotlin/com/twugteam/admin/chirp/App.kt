package com.twugteam.admin.chirp

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.twugteam.admin.chirp.navigation.IOSDeepLinkListener
import com.twugteam.admin.chirp.navigation.NavigationRoot
import com.twugteam.admin.core.designsystem.theme.ChirpTheme

@Composable
fun App() {
    val navController = rememberNavController()
    IOSDeepLinkListener(
        navController = navController
    )
    ChirpTheme {
        NavigationRoot(
            navController = navController
        )
    }
}
