package com.twugteam.admin.chat.domain.error

import com.twugteam.admin.core.domain.utils.RootError

enum class ConnectionError: RootError {
    NOT_CONNECTED,
    MESSAGE_SEND_FAILED
}

typealias WebSocketConnectionError = ConnectionError
