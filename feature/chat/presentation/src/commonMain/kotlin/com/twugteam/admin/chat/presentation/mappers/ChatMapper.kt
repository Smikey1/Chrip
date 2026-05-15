package com.twugteam.admin.chat.presentation.mappers

import com.twugteam.admin.chat.domain.models.Chat
import com.twugteam.admin.chat.presentation.model.ChatUi

fun Chat.toUi(localParticipantId: String): ChatUi {
    val (local, others) = participants.partition { it.userId == localParticipantId }
    return ChatUi(
        chatId = id,
        lastMessage = lastMessage,
        otherParticipant = others.map {
            it.toUi()
        },
        localParticipant = local.first().toUi(),
        lastMessageSenderUsername = participants.find { it.userId == lastMessage?.senderId }?.username
    )
}