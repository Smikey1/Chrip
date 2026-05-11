package com.twugteam.admin.auth.presentation.forgot_password

import androidx.compose.foundation.text.input.TextFieldState
import com.twugteam.admin.core.presentation.util.UiText

data class ForgotPasswordState(
    val emailTextFieldState: TextFieldState = TextFieldState(),
    val isLoading: Boolean = false,
    val errorText: UiText? = null,
    val isEmailSentSuccessfully: Boolean = false,
    val canSubmit: Boolean = false
)