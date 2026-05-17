package com.twugteam.admin.chat.presentation.chat_detail

import com.twugteam.admin.core.presentation.util.UiText

sealed interface ChatDetailEvent {
    data object OnChatLeft: ChatDetailEvent
    data class OnError(val error: UiText): ChatDetailEvent
}