package com.twugteam.admin.chirp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.navigation.NavController
import androidx.navigation.NavUri

@Composable
fun IOSDeepLinkListener(
    navController: NavController
) {
    DisposableEffect(Unit) {
        IOSExternalUriHandler.uriListener = { uri ->
            navController.navigate(NavUri(uri))
        }
        onDispose {
            IOSExternalUriHandler.uriListener = null
        }
    }
}