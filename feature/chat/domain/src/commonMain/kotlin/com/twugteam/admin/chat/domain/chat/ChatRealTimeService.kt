package com.twugteam.admin.chat.domain.chat

import com.twugteam.admin.chat.domain.models.ChatMessage
import com.twugteam.admin.chat.domain.models.NetworkConnectionState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface ChatRealTimeService {
    val chatMessage: Flow<ChatMessage>
    val connectionState: StateFlow<NetworkConnectionState>
}