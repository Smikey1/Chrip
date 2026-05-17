package com.twugteam.admin.chat.domain.chat

import com.twugteam.admin.chat.domain.models.Chat
import com.twugteam.admin.chat.domain.models.ChatInfo
import com.twugteam.admin.chat.domain.models.ChatParticipant
import com.twugteam.admin.core.domain.utils.DataError
import com.twugteam.admin.core.domain.utils.EmptyResult
import com.twugteam.admin.core.domain.utils.Result
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getChats(): Flow<List<Chat>>

    fun getChatInfoById(chatId: String): Flow<ChatInfo>
    fun getActiveParticipantByChatId(chatId: String): Flow<List<ChatParticipant>>

    suspend fun fetchChatById(chatId: String): EmptyResult<DataError.Remote>

    suspend fun fetchChats(): Result<List<Chat>, DataError.Remote>

    suspend fun createChat(otherUserIds: List<String>): Result<Chat, DataError.Remote>

    suspend fun leaveChat(chatId: String): EmptyResult<DataError.Remote>

    suspend fun addParticipantsToChat(
        chatId: String,
        otherUserIds: List<String>
    ): Result<Chat, DataError.Remote>
}