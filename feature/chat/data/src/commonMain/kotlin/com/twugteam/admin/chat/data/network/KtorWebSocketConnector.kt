@file:OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)

package com.twugteam.admin.chat.data.network

import com.twugteam.admin.chat.data.dto.websocket.WebSocketMessageDto
import com.twugteam.admin.chat.data.lifecycle.AppLifecycleObserver
import com.twugteam.admin.chat.domain.error.WebSocketConnectionError
import com.twugteam.admin.chat.domain.models.NetworkConnectionState
import com.twugteam.admin.core.data.networking.UrlConstraints
import com.twugteam.admin.core.domain.auth.SessionStorage
import com.twugteam.admin.core.domain.logging.ChripLogger
import com.twugteam.admin.core.domain.utils.EmptyResult
import com.twugteam.admin.core.domain.utils.Result
import com.twugteam.admin.feature.chat.data.BuildKonfig
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.header
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readText
import io.ktor.websocket.send
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.seconds

class KtorWebSocketConnector(
    private val json: Json,
    private val logger: ChripLogger,
    private val httpClient: HttpClient,
    private val sessionStorage: SessionStorage,
    private val applicationScope: CoroutineScope,
    private val lifecycleObserver: AppLifecycleObserver,
    private val connectivityObserver: ConnectivityObserver,
    private val connectionRetryHandler: ConnectionRetryHandler,
    private val connectionErrorHandler: ConnectionErrorHandler
) {

    private var currentWebSocketSession: WebSocketSession? = null

    private val _connectionState = MutableStateFlow(NetworkConnectionState.DISCONNECTED)
    val connectionState = _connectionState.asStateFlow()

    private val isConnected = connectivityObserver
        .isConnectedToNetwork
        .debounce(1.seconds)
        .stateIn(
            scope = applicationScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = false
        )

    private val isAppInForeground = lifecycleObserver
        .isAppInForeground
        .onEach { isInForeground ->
            if (isInForeground) {
                connectionRetryHandler.resetDelay()
            }
        }
        .stateIn(
            scope = applicationScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = false
        )

    val messagesFlow = combine(
        isConnected,
        isAppInForeground,
        sessionStorage.observeAuthInfo(),
    ) { isConnected, isAppInForeground, authInfo ->
        when {
            !isConnected -> {
                logger.info("Device is disconnected from active network, disconnecting the web socket session")
                _connectionState.value = NetworkConnectionState.ERROR_NETWORK
                currentWebSocketSession?.close()
                currentWebSocketSession = null
                null
            }

            !isAppInForeground -> {
                logger.info("App is in background, disconnecting the web socket session")
                _connectionState.value = NetworkConnectionState.DISCONNECTED
                currentWebSocketSession?.close()
                currentWebSocketSession = null
                null
            }

            authInfo == null -> {
                logger.info("No Authentication details, Clearing Session Info and disconnecting")
                _connectionState.value = NetworkConnectionState.DISCONNECTED
                currentWebSocketSession?.close()
                currentWebSocketSession = null
                connectionRetryHandler.resetDelay()
                null
            }

            else -> {
                logger.info("App is in foreground, valid network connection and had auth details ")
                logger.info("Establishing Web Sockets Connection ...")

                if (_connectionState.value !in listOf(
                        NetworkConnectionState.CONNECTING,
                        NetworkConnectionState.CONNECTED
                    )
                ) {
                    _connectionState.value = NetworkConnectionState.CONNECTING
                }
                authInfo
            }
        }
    }.flatMapLatest { authInfo ->
        if (authInfo == null) {
            flowOf()
        } else {
            createWebSocketFlow(authInfo.accessToken)
                // Below catch block will transform error to platform specific compatibility
                .catch { exception ->
                    logger.error("Exception in WebSocket", exception)

                    // close session is WS exception happens
                    currentWebSocketSession?.close()
                    currentWebSocketSession = null

                    val transformedException =
                        connectionErrorHandler.transformIOSException(exception)
                    throw transformedException
                }.retryWhen { cause, attempt ->
                    logger.info("Connection Failed on Attempt: $attempt")
                    val shouldRetry = connectionRetryHandler.shouldRetry(cause, attempt)

                    if (shouldRetry) {
                        _connectionState.value = NetworkConnectionState.CONNECTING
                        connectionRetryHandler.applyRetryDelay(attempt)
                    }

                    shouldRetry

                    // if shouldRetry is true then it will re-subscribe to the original create WS flow
                    // once it re-subscribe the flow, then it will again re-establish WS connection
                }.catch { exception ->
                    // Here, all the error are not retriable (retry-able), such as cancellation
                    //exception. But, network error are retriable.
                    logger.error("Unhandled Not Retriable WS Error", exception)
                    _connectionState.value =
                        connectionErrorHandler.getConnectionStateForError(exception)

                }
        }
    }

    private fun createWebSocketFlow(accessToken: String) = callbackFlow {
        _connectionState.value = NetworkConnectionState.CONNECTING

        currentWebSocketSession = httpClient.webSocketSession(
            urlString = "${UrlConstraints.WEBSOCKET_BASE_URL}/chat"
        ) {
            header("Authorization", "Bearer $accessToken")
            header("x-api-key", BuildKonfig.API_KEY)

        }
        currentWebSocketSession?.let { activeWebSocketSession ->
            _connectionState.value = NetworkConnectionState.CONNECTED

            // this buffer is necessary to cache 100 msg/sec if 100 message arrived at once
            // extra safety for future scalable apps
            activeWebSocketSession
                .incoming
                .consumeAsFlow()
                .buffer(capacity = 100)
                .collect { frame ->
                    when (frame) {
                        is Frame.Text -> {
                            val textData = frame.readText()
                            logger.info("Received Raw Text Frame --> $textData")
                            val messageDto = json.decodeFromString<WebSocketMessageDto>(textData)
                            send(messageDto)
                        }

                        is Frame.Ping -> activeWebSocketSession.send(Frame.Pong(frame.data))
                        else -> Unit
                    }
                }
        } ?: throw Exception("Failed to established Web Socket Connection")

        awaitClose {
            launch(start = CoroutineStart.ATOMIC) {
                withContext(NonCancellable) {
                    logger.info("Disconnecting from current web socket session...")
                    _connectionState.value = NetworkConnectionState.DISCONNECTED
                    currentWebSocketSession?.close()
                    currentWebSocketSession = null
                }
            }
        }
    }

    suspend fun sendMessage(message: String): EmptyResult<WebSocketConnectionError> {
        val connectionState = connectionState.value

        if (currentWebSocketSession == null || connectionState != NetworkConnectionState.CONNECTED) {
            return Result.Failure(WebSocketConnectionError.NOT_CONNECTED)
        }
        return try {
            currentWebSocketSession?.send(message)
            Result.Success(Unit)
        } catch (e: Exception) {
//            if (e is CancellationException) throw e
            currentCoroutineContext().ensureActive()
            logger.error("Unable to send a message", e)
            Result.Failure(WebSocketConnectionError.MESSAGE_SEND_FAILED)
        }
    }

}