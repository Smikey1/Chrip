package com.twugteam.admin.auth.presentation.forgot_password

import com.twugteam.admin.core.presentation.util.UiText

sealed interface ForgotPasswordEvent {
    data class Error(val error: UiText) : ForgotPasswordEvent
    data object Success : ForgotPasswordEvent
}