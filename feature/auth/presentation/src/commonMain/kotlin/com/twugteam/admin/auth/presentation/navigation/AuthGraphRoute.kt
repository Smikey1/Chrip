package com.twugteam.admin.auth.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface AuthGraphRoute {
    @Serializable
    data object AuthGraph: AuthGraphRoute

    @Serializable
    data object Login: AuthGraphRoute

    @Serializable
    data object Register: AuthGraphRoute

    @Serializable
    data class RegisterSuccess(val email: String): AuthGraphRoute

    @Serializable
    data class EmailVerification(val token: String): AuthGraphRoute

    @Serializable
    data object ForgotPassword: AuthGraphRoute

    @Serializable
    data object ResetPassword: AuthGraphRoute
}