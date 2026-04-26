package com.twugteam.admin.auth.presentation.register

sealed interface RegisterAction {
    data object OnRegisterClick : RegisterAction
}