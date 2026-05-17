package com.twugteam.admin.chat.domain.chat

import com.twugteam.admin.chat.domain.models.Chat
import com.twugteam.admin.core.domain.utils.DataError
import com.twugteam.admin.core.domain.utils.Result
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getChats(): Flow<List<Chat>>

    suspend fun fetchChats(): Result<List<Chat>, DataError.Remote>
}