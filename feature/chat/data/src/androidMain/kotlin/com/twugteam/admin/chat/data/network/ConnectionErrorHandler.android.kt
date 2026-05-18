package com.twugteam.admin.chat.data.network

import com.twugteam.admin.chat.domain.models.NetworkConnectionState
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.websocket.WebSocketException
import io.ktor.network.sockets.SocketTimeoutException
import kotlinx.io.EOFException
import java.net.SocketException
import java.net.UnknownHostException
import javax.net.ssl.SSLException

actual class ConnectionErrorHandler {
    actual fun getConnectionStateForError(cause: Throwable): NetworkConnectionState {
        return when (cause) {
            is ClientRequestException,
            is WebSocketException,
            is SocketException,
            is SocketTimeoutException,
            is UnknownHostException,
            is SSLException,
            is EOFException -> NetworkConnectionState.ERROR_NETWORK

            else -> NetworkConnectionState.ERROR_UNKNOWN
        }
    }

    actual fun transformIOSException(exception: Throwable): Throwable {
        return exception
    }

    actual fun isRetriableError(cause: Throwable): Boolean {
        return when (cause) {
            is WebSocketException,
            is SocketException,
            is SocketTimeoutException,
            is EOFException -> true

            else -> false
        }
    }
}