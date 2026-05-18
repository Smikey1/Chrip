package com.twugteam.admin.chat.data.network

import com.twugteam.admin.chat.domain.models.NetworkConnectionState

expect class ConnectionErrorHandler {
    fun getConnectionStateForError(cause: Throwable): NetworkConnectionState
    fun transformIOSException(exception: Throwable): Throwable
    fun isRetriableError(cause: Throwable): Boolean
}