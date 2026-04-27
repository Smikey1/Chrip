package com.twugteam.admin.auth.presentation.navigation

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