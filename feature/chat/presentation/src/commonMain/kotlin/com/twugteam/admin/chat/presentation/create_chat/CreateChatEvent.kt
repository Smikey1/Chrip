package com.twugteam.admin.chat.presentation.create_chat

import com.twugteam.admin.chat.domain.models.Chat

sealed interface CreateChatEvent {
    data class OnChatCreated(val chat: Chat) : CreateChatEvent
}