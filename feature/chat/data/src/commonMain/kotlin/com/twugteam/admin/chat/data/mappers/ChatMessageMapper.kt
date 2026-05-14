package com.twugteam.admin.chat.data.mappers

import com.twugteam.admin.chat.data.dto.ChatMessageDto
import com.twugteam.admin.chat.domain.models.ChatMessage
import kotlin.time.Instant

fun ChatMessageDto.toDomain(): ChatMessage {
    return ChatMessage(
        id = id,
        chatId = chatId,
        content = content,
        createdAt = Instant.parse(createdAt),
        senderId = senderId
    )
}