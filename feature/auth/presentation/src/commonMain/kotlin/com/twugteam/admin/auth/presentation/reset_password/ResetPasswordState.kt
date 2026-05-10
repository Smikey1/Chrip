package com.twugteam.admin.auth.presentation.reset_password

import androidx.compose.foundation.text.input.TextFieldState
import com.twugteam.admin.core.presentation.util.UiText

data class ResetPasswordState(
    val passwordTextFieldState: TextFieldState = TextFieldState(),
    val isLoading: Boolean = false,
    val errorText: UiText? = null,
    val isPasswordVisible: Boolean = false,
    val isPasswordResetSuccessful: Boolean = false,
    val canSubmit: Boolean = false
)