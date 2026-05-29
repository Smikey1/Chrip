package com.twugteam.admin.chat.data.message

import com.twugteam.admin.chat.data.dto.websocket.OutgoingWebSocketDto
import com.twugteam.admin.chat.data.dto.websocket.WebSocketMessageDto
import com.twugteam.admin.chat.data.mappers.toDomain
import com.twugteam.admin.chat.data.mappers.toEntity
import com.twugteam.admin.chat.data.mappers.toWebSocketDto
import com.twugteam.admin.chat.data.network.KtorWebSocketConnector
import com.twugteam.admin.chat.database.ChirpChatDatabase
import com.twugteam.admin.chat.domain.message.ChatMessageService
import com.twugteam.admin.chat.domain.message.MessageRepository
import com.twugteam.admin.chat.domain.models.ChatMessage
import com.twugteam.admin.chat.domain.models.ChatMessageDeliveryStatus
import com.twugteam.admin.chat.domain.models.MessageWithSender
import com.twugteam.admin.chat.domain.models.OutgoingNewMessage
import com.twugteam.admin.core.data.database.safeDatabaseUpdate
import com.twugteam.admin.core.domain.auth.SessionStorage
import com.twugteam.admin.core.domain.utils.DataError
import com.twugteam.admin.core.domain.utils.EmptyResult
import com.twugteam.admin.core.domain.utils.Result
import com.twugteam.admin.core.domain.utils.onFailure
import com.twugteam.admin.core.domain.utils.onSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlin.time.Clock

class OfflineFirstMessageRepository(
    private val db: ChirpChatDatabase,
    private val chatMessageService: ChatMessageService,
    private val sessionStorage: SessionStorage,
    private val webSocketConnector: KtorWebSocketConnector,
    private val applicationScope: CoroutineScope,
    private val json: Json
) : MessageRepository {
    override suspend fun updateMessageDeliveryStatus(
        messageId: String,
        status: ChatMessageDeliveryStatus
    ): EmptyResult<DataError.Local> {
        return safeDatabaseUpdate {
            db.chatMessageDao.updateDeliveryStatus(
                messageId = messageId,
                deliveryStatus = status.name,
                timestamp = Clock.System.now().toEpochMilliseconds()
            )
        }
    }

    override suspend fun fetchMessage(
        chatId: String,
        before: String?
    ): Result<List<ChatMessage>, DataError> {
        return chatMessageService
            .fetchMessage(chatId, before)
            .onSuccess { messages ->
                return safeDatabaseUpdate {
                    db.chatMessageDao.upsertMessageAndSyncIfNeeded(
                        chatId = chatId,
                        serverMessages = messages.map { it.toEntity() },
                        pageSize = ChatMessageConstant.PAGE_SIZE,
                        shouldSync = before == null,
                        // only sync for most recent message and page size
                        // no need to sync very older message/ before message
                    )
                    messages
                }
            }
    }

    override fun getMessageForChat(chatId: String): Flow<List<MessageWithSender>> {
        return db
            .chatMessageDao
            .getAllMessagesByChatId(chatId)
            .map { messageEntities ->
                messageEntities.map {
                    it.toDomain()
                }
            }
    }

    override suspend fun sendMessage(message: OutgoingNewMessage): EmptyResult<DataError> {
        return safeDatabaseUpdate {
            val localUser = sessionStorage.observeAuthInfo().firstOrNull()?.user
                ?: return Result.Failure(DataError.Local.NOT_FOUND)
            val outgoingNewMessageDto = message.toWebSocketDto()
            val messageEntity = message.toEntity(
                senderId = localUser.id,
                deliveryStatus = ChatMessageDeliveryStatus.SENT
            )
            db.chatMessageDao.upsertMessage(messageEntity)
            return webSocketConnector
                .sendMessage(outgoingNewMessageDto.toJsonPayload())
                .onFailure { error ->
                    applicationScope.launch {
                        db.chatMessageDao.updateDeliveryStatus(
                            messageId = messageEntity.messageId,
                            deliveryStatus = ChatMessageDeliveryStatus.FAILED.name,
                            timestamp = Clock.System.now().toEpochMilliseconds()

                        )
                    }.join()
                }
        }
    }

    override suspend fun retryMessage(messageId: String): EmptyResult<DataError> {
        return safeDatabaseUpdate {
            val message = db.chatMessageDao.getMessageById(messageId)
                ?: return Result.Failure(DataError.Local.NOT_FOUND)

            db.chatMessageDao.updateDeliveryStatus(
                messageId = messageId,
                deliveryStatus = ChatMessageDeliveryStatus.SENDING.name,
                timestamp = Clock.System.now().toEpochMilliseconds()
            )
            val outgoingNewMessage = OutgoingWebSocketDto.NewMessage(
                chatId = message.chatId,
                content = message.content,
                messageId = messageId
            )
            webSocketConnector
                .sendMessage(outgoingNewMessage.toJsonPayload())
                .onFailure { error ->
                    applicationScope.launch {
                        db.chatMessageDao.updateDeliveryStatus(
                            messageId = message.chatId,
                            deliveryStatus = ChatMessageDeliveryStatus.FAILED.name,
                            timestamp = Clock.System.now().toEpochMilliseconds()

                        )
                    }.join()
                }
        }
    }

    override suspend fun deleteMessage(messageId: String): EmptyResult<DataError> {
        return chatMessageService
            .deleteMessage(messageId)
            .onSuccess {
                applicationScope.launch {
                    safeDatabaseUpdate {
                        db.chatMessageDao.deleteMessageById(messageId)
                    }
                }.join()
            }
    }

    private fun OutgoingWebSocketDto.NewMessage.toJsonPayload(): String {
        val webSocketMessageDto = WebSocketMessageDto(
            type = type.name,
            payload = json.encodeToString(this)
        )
        return json.encodeToString(webSocketMessageDto)
    }
}