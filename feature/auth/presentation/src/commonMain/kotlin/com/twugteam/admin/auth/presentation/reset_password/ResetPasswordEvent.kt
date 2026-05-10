package com.twugteam.admin.auth.presentation.reset_password

import com.twugteam.admin.core.presentation.util.UiText

sealed interface ResetPasswordEvent {
    data class Error(val error: UiText) : ResetPasswordEvent
    data object Success : ResetPasswordEvent
}