package com.twugteam.admin.auth.presentation.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.twugteam.admin.auth.presentation.register.RegisterScreenRoot
import com.twugteam.admin.auth.presentation.register_success.RegisterSuccessScreenRoot

fun NavGraphBuilder.authGraph(
    navController: NavHostController,
    onLoginSuccess: () -> Unit,
) {
    navigation<AuthGraphRoute.AuthGraph>(
        startDestination = AuthGraphRoute.Register
    ) {
        composable<AuthGraphRoute.Register> {
            RegisterScreenRoot(
                onRegisterSuccess = { email ->
                    navController.navigate(AuthGraphRoute.RegisterSuccess(email))
                },
                onLoginClick = {
                    navController.navigate(AuthGraphRoute.Login)
                }
            )
        }

        composable<AuthGraphRoute.RegisterSuccess> {
            RegisterSuccessScreenRoot(
                onLoginClick = {
                    navController.navigate(AuthGraphRoute.Login)
                }
            )
        }

        composable<AuthGraphRoute.Login> {

        }
    }
}