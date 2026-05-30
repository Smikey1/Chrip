package com.twugteam.admin.chat.domain.message

import com.twugteam.admin.chat.domain.models.ChatMessage
import com.twugteam.admin.chat.domain.models.ChatMessageDeliveryStatus
import com.twugteam.admin.chat.domain.models.MessageWithSender
import com.twugteam.admin.chat.domain.models.OutgoingNewMessage
import com.twugteam.admin.core.domain.utils.DataError
import com.twugteam.admin.core.domain.utils.EmptyResult
import com.twugteam.admin.core.domain.utils.Result
import kotlinx.coroutines.flow.Flow

interface MessageRepository {
    suspend fun updateMessageDeliveryStatus(
        messageId: String,
        status: ChatMessageDeliveryStatus
    ): EmptyResult<DataError.Local>

    suspend fun fetchMessage(
        chatId: String,
        before: String? = null
    ): Result<List<ChatMessage>, DataError>

    fun getMessageForChat(chatId: String): Flow<List<MessageWithSender>>

    suspend fun sendMessage(message: OutgoingNewMessage): EmptyResult<DataError>

    suspend fun retryMessage(messageId: String): EmptyResult<DataError>

    suspend fun deleteMessage(messageId: String): EmptyResult<DataError>
}