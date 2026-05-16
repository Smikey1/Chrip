package com.twugteam.admin.chat.domain.models

enum class NetworkConnectionState {
    DISCONNECTED,
    CONNECTED,
    CONNECTING,
    ERROR_NETWORK,
    ERROR_UNKNOWN,
}