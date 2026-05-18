package com.twugteam.admin.chat.data.chat

import com.twugteam.admin.chat.data.dto.websocket.WebSocketMessageDto
import com.twugteam.admin.chat.data.mappers.toNewMessageDto
import com.twugteam.admin.chat.data.network.KtorWebSocketConnector
import com.twugteam.admin.chat.database.ChirpChatDatabase
import com.twugteam.admin.chat.domain.chat.ChatRealTimeService
import com.twugteam.admin.chat.domain.chat.ChatRepository
import com.twugteam.admin.chat.domain.error.WebSocketConnectionError
import com.twugteam.admin.chat.domain.message.MessageRepository
import com.twugteam.admin.chat.domain.models.ChatMessage
import com.twugteam.admin.chat.domain.models.ChatMessageDeliveryStatus
import com.twugteam.admin.chat.domain.models.NetworkConnectionState
import com.twugteam.admin.core.domain.auth.SessionStorage
import com.twugteam.admin.core.domain.utils.EmptyResult
import com.twugteam.admin.core.domain.utils.onFailure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.json.Json

class WebSocketChatService(
    private val webSocketConnector: KtorWebSocketConnector,
    private val chatRepository: ChatRepository,
    private val sessionStorage: SessionStorage,
    private val json: Json,
    private val db: ChirpChatDatabase,
    private val messageRepository: MessageRepository,
) : ChatRealTimeService {
    override val chatMessage: Flow<ChatMessage>
        get() = TODO()

    override val connectionState: StateFlow<NetworkConnectionState> =
        webSocketConnector.connectionState

    override suspend fun sendChatMessage(message: ChatMessage): EmptyResult<WebSocketConnectionError> {
        val outgoingDto = message.toNewMessageDto()
        val webSocketMessageDto = WebSocketMessageDto(
            type = outgoingDto.type.name,
            payload = json.encodeToString(outgoingDto)
        )
        val rawJsonPayload = json.encodeToString(webSocketMessageDto)
        return webSocketConnector
            .sendMessage(rawJsonPayload)
            .onFailure { error ->
                messageRepository.updateMessageDeliveryStatus(
                    messageId = message.id,
                    status = ChatMessageDeliveryStatus.FAILED
                )
            }
    }
}