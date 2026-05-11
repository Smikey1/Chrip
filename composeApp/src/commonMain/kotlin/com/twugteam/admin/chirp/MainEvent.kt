package com.twugteam.admin.chirp

sealed interface MainEvent {
    data object OnSessionExpired : MainEvent
}