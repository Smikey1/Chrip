package com.twugteam.admin.auth.presentation.register_success

import com.twugteam.admin.core.presentation.util.UiText

data class RegisterSuccessState(
    val registeredEmail: String = "",
    val isResendingVerificationEmail: Boolean = false,
    val resendVerificationError: UiText? = null
)