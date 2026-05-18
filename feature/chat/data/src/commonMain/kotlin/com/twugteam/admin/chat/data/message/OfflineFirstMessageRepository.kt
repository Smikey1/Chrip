package com.twugteam.admin.chat.data.message

import com.twugteam.admin.chat.database.ChirpChatDatabase
import com.twugteam.admin.chat.domain.message.MessageRepository
import com.twugteam.admin.chat.domain.models.ChatMessageDeliveryStatus
import com.twugteam.admin.core.data.database.safeDatabaseUpdate
import com.twugteam.admin.core.domain.utils.DataError
import com.twugteam.admin.core.domain.utils.EmptyResult
import kotlin.time.Clock

class OfflineFirstMessageRepository(
    private val db: ChirpChatDatabase
): MessageRepository {
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
}