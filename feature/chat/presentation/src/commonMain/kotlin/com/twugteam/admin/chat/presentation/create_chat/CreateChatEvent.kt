package com.twugteam.admin.chat.presentation.create_chat

import com.twugteam.admin.chat.domain.models.Chat

sealed interface CreateChatEvent {
    data class ChatCreated(val chat: Chat) : CreateChatEvent
}