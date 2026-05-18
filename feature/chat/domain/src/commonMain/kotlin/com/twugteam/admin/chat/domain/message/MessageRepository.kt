package com.twugteam.admin.chat.domain.message

import com.twugteam.admin.chat.domain.models.ChatMessageDeliveryStatus
import com.twugteam.admin.core.domain.utils.DataError
import com.twugteam.admin.core.domain.utils.EmptyResult

interface MessageRepository {
    suspend fun updateMessageDeliveryStatus(
        messageId: String,
        status: ChatMessageDeliveryStatus
    ): EmptyResult<DataError.Local>
}