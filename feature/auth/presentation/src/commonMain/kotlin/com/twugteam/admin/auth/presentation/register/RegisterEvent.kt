package com.twugteam.admin.auth.presentation.register

import com.twugteam.admin.core.presentation.util.UiText

sealed interface RegisterEvent {
    data class RegistrationSuccess(val email: String) : RegisterEvent
    data class Error(val error: UiText) : RegisterEvent
}