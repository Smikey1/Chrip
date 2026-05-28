package com.twugteam.admin.chat.domain.message

import com.twugteam.admin.chat.domain.models.ChatMessage
import com.twugteam.admin.core.domain.utils.DataError
import com.twugteam.admin.core.domain.utils.Result

interface ChatMessageService {
    suspend fun fetchMessage(
        chatId: String,
        before: String? = null
    ): Result<List<ChatMessage>, DataError.Remote>
}