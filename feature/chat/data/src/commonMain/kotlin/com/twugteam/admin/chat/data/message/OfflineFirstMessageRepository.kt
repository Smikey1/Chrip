package com.twugteam.admin.chat.data.message

import com.twugteam.admin.chat.data.mappers.toDomain
import com.twugteam.admin.chat.data.mappers.toEntity
import com.twugteam.admin.chat.database.ChirpChatDatabase
import com.twugteam.admin.chat.domain.message.ChatMessageService
import com.twugteam.admin.chat.domain.message.MessageRepository
import com.twugteam.admin.chat.domain.models.ChatMessage
import com.twugteam.admin.chat.domain.models.ChatMessageDeliveryStatus
import com.twugteam.admin.chat.domain.models.MessageWithSender
import com.twugteam.admin.core.data.database.safeDatabaseUpdate
import com.twugteam.admin.core.domain.utils.DataError
import com.twugteam.admin.core.domain.utils.EmptyResult
import com.twugteam.admin.core.domain.utils.Result
import com.twugteam.admin.core.domain.utils.onFailure
import com.twugteam.admin.core.domain.utils.onSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Clock

class OfflineFirstMessageRepository(
    private val db: ChirpChatDatabase,
    private val chatMessageService: ChatMessageService
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
}