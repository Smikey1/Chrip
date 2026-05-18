package com.twugteam.admin.chat.domain.chat

import com.twugteam.admin.chat.domain.error.WebSocketConnectionError
import com.twugteam.admin.chat.domain.models.ChatMessage
import com.twugteam.admin.chat.domain.models.NetworkConnectionState
import com.twugteam.admin.core.domain.utils.EmptyResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface ChatRealTimeService {
    val chatMessage: Flow<ChatMessage>
    val connectionState: StateFlow<NetworkConnectionState>
    suspend fun sendChatMessage(message: ChatMessage): EmptyResult<WebSocketConnectionError>
}