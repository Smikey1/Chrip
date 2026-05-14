package com.twugteam.admin.chat.domain.chat

import com.twugteam.admin.chat.domain.models.ChatParticipant
import com.twugteam.admin.core.domain.utils.DataError
import com.twugteam.admin.core.domain.utils.Result

interface ChatParticipantService {
    suspend fun searchParticipant(searchQuery: String): Result<ChatParticipant, DataError.Remote>
}