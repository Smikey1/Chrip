package com.twugteam.admin.chat.presentation.model

import com.twugteam.admin.chat.domain.models.ChatMessage
import com.twugteam.admin.core.designsystem.components.avatar.ChatParticipantUi

data class ChatUi(
    val chatId: String,
    val localParticipant: ChatParticipantUi,
    val otherParticipant: List<ChatParticipantUi>,
    val lastMessage: ChatMessage?,
    val lastMessageSenderUsername: String?
)
