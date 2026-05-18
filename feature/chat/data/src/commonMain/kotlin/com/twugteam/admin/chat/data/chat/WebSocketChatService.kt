package com.twugteam.admin.chat.data.chat

import com.twugteam.admin.chat.data.dto.websocket.IncomingWebSocketDto
import com.twugteam.admin.chat.data.dto.websocket.IncomingWebSocketType
import com.twugteam.admin.chat.data.dto.websocket.WebSocketMessageDto
import com.twugteam.admin.chat.data.mappers.toDomain
import com.twugteam.admin.chat.data.mappers.toEntity
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.serialization.json.Json

class WebSocketChatService(
    private val webSocketConnector: KtorWebSocketConnector,
    private val chatRepository: ChatRepository,
    private val sessionStorage: SessionStorage,
    private val json: Json,
    private val db: ChirpChatDatabase,
    private val messageRepository: MessageRepository,
    private val applicationScope: CoroutineScope
) : ChatRealTimeService {
    override val chatMessage: Flow<ChatMessage> = webSocketConnector
        .messagesFlow
        .mapNotNull {
            parseIncomingMessage(it)
        }
        .onEach {
            handleIncomingMessage(it)
        }.filterIsInstance<IncomingWebSocketDto.NewMessage>()
        .mapNotNull {
            db.chatMessageDao.getMessageById(it.id)?.toDomain()
        }
        .shareIn(
            scope = applicationScope,
            started = SharingStarted.WhileSubscribed(5_000L),
        )

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

    private fun parseIncomingMessage(webSocketMessageDto: WebSocketMessageDto): IncomingWebSocketDto? {
        return when (webSocketMessageDto.type) {
            IncomingWebSocketType.NEW_MESSAGE.name -> {
                json.decodeFromString<IncomingWebSocketDto.NewMessage>(webSocketMessageDto.payload)
            }

            IncomingWebSocketType.CHAT_PARTICIPANTS_CHANGED.name -> {
                json.decodeFromString<IncomingWebSocketDto.ChatParticipantChanged>(
                    webSocketMessageDto.payload
                )
            }

            IncomingWebSocketType.PROFILE_PICTURE_UPDATED.name -> {
                json.decodeFromString<IncomingWebSocketDto.ProfilePictureUpdated>(
                    webSocketMessageDto.payload
                )
            }

            IncomingWebSocketType.MESSAGE_DELETED.name -> {
                json.decodeFromString<IncomingWebSocketDto.MessageDeleted>(webSocketMessageDto.payload)
            }

            else -> null
        }
    }

    private suspend fun handleIncomingMessage(messageDto: IncomingWebSocketDto) {
        when (messageDto) {
            is IncomingWebSocketDto.ChatParticipantChanged -> handleChatParticipantChange(messageDto)
            is IncomingWebSocketDto.MessageDeleted -> handleMessageDeleted(messageDto)
            is IncomingWebSocketDto.NewMessage -> handleNewMessage(messageDto)
            is IncomingWebSocketDto.ProfilePictureUpdated -> handleProfilePictureUpdate(messageDto)
        }
    }

    private suspend fun handleNewMessage(messageDto: IncomingWebSocketDto.NewMessage) {
        val chatExists = db.chatDao.getChatById(messageDto.chatId) != null
        if (!chatExists) {
            chatRepository.fetchChatById(messageDto.chatId)
        }
        val entity = messageDto.toEntity()
        db.chatMessageDao.upsertMessage(entity)
    }

    private suspend fun handleChatParticipantChange(messageDto: IncomingWebSocketDto.ChatParticipantChanged) {
        // refetching the chat will with fetch up-to-date chatParticipant
        // And it updated the db which automatically trigger db flow will emit a new emission
        chatRepository.fetchChatById(messageDto.chatId)
    }

    private suspend fun handleProfilePictureUpdate(messageDto: IncomingWebSocketDto.ProfilePictureUpdated) {
        db.chatParticipantDao.updateProfilePicture(
            userId = messageDto.userId,
            newUrl = messageDto.newUrl
        )

        val authInfo = sessionStorage.observeAuthInfo().firstOrNull()
        if (authInfo != null) {
            sessionStorage.setAuthInfo(
                authInfo = authInfo.copy(
                    user = authInfo.user.copy(profilePictureUrl = messageDto.newUrl)
                )
            )
        }
    }

    private suspend fun handleMessageDeleted(messageDto: IncomingWebSocketDto.MessageDeleted) {
        db.chatMessageDao.deleteMessageById(messageDto.messageId)
    }
}