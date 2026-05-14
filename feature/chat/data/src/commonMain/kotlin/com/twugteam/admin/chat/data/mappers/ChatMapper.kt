package com.twugteam.admin.chat.data.mappers

import com.twugteam.admin.chat.data.dto.ChatDto
import com.twugteam.admin.chat.domain.models.Chat
import kotlin.time.Instant

fun ChatDto.toDomain(): Chat {
    return Chat(
        id = id,
        participants = participants.map { it.toDomain() },
        lastActivityAt = Instant.parse(lastActivityAt),
        lastMessage = lastMessage?.toDomain()
    )
}