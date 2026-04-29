package com.twugteam.admin.auth.presentation.login

sealed interface LoginEvent {
    data object Success: LoginEvent
}