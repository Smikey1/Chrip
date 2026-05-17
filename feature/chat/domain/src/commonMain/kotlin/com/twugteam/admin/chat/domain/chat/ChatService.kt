package com.twugteam.admin.chat.domain.chat

import com.twugteam.admin.chat.domain.models.Chat
import com.twugteam.admin.core.domain.utils.DataError
import com.twugteam.admin.core.domain.utils.EmptyResult
import com.twugteam.admin.core.domain.utils.Result

interface ChatService {
    suspend fun createChat(otherUserIds: List<String>): Result<Chat, DataError.Remote>

    suspend fun fetchChats(): Result<List<Chat>, DataError.Remote>

    suspend fun fetchChatById(chatId: String): Result<Chat, DataError.Remote>

    suspend fun leaveChat(chatId: String): EmptyResult<DataError.Remote>

    suspend fun addParticipantsToChat(
        chatId: String,
        otherUserIds: List<String>
    ): Result<Chat, DataError.Remote>
}