package com.twugteam.admin.auth.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import com.twugteam.admin.auth.presentation.email_verification.EmailVerificationRoot
import com.twugteam.admin.auth.presentation.forgot_password.ForgotPasswordScreenRoot
import com.twugteam.admin.auth.presentation.login.LoginScreenRoot
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
                    navController.navigate(AuthGraphRoute.Login) {
                        popUpTo(AuthGraphRoute.RegisterSuccess){
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable<AuthGraphRoute.EmailVerification>(
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "https://chirp.pl-coding.com/api/auth/verify?token={token}"
                },
                navDeepLink {
                    uriPattern = "chirp://chirp.pl-coding.com/api/auth/verify?token={token}"
                }
            )
        ) {
            EmailVerificationRoot(
                onLoginClick = {
                    navController.navigate(AuthGraphRoute.Login) {
                        popUpTo(AuthGraphRoute.EmailVerification){
                            inclusive = true
                        }
                    }
                },
                onCloseClick = {
                    navController.navigateUp()
                }
            )
        }

        composable<AuthGraphRoute.Login> {
            LoginScreenRoot(
                onLoginSuccess = onLoginSuccess,
                onForgotPasswordClick = {
                    navController.navigate(AuthGraphRoute.ForgotPassword)
                },
                onCreateAccountClick = {
                    navController.navigate(AuthGraphRoute.Register)
                }
            )
        }

        composable<AuthGraphRoute.ForgotPassword> {
            ForgotPasswordScreenRoot(

            )
        }
    }
}