package com.twugteam.admin.auth.presentation.register_success

sealed interface RegisterSuccessEvent {
    data object ResendVerificationEmailSuccess: RegisterSuccessEvent
}