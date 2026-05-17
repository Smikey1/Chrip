package com.twugteam.admin.chat.data.mappers

import com.twugteam.admin.chat.data.dto.ChatDto
import com.twugteam.admin.chat.database.entities.ChatEntity
import com.twugteam.admin.chat.database.entities.ChatParticipantEntity
import com.twugteam.admin.chat.database.entities.ChatWithParticipant
import com.twugteam.admin.chat.database.view.LastMessageView
import com.twugteam.admin.chat.domain.models.Chat
import com.twugteam.admin.chat.domain.models.ChatMessage
import com.twugteam.admin.chat.domain.models.ChatParticipant
import kotlin.time.Instant

fun ChatDto.toDomain(): Chat {
    return Chat(
        id = id,
        participants = participants.map { it.toDomain() },
        lastActivityAt = Instant.parse(lastActivityAt),
        lastMessage = lastMessage?.toDomain()
    )
}

fun ChatWithParticipant.toDomain(): Chat {
    return Chat(
        id = chat.chatId,
        participants = participants.map { it.toDomain() },
        lastActivityAt = Instant.fromEpochMilliseconds(chat.lastActivityAt),
        lastMessage = lastMessageView?.toDomain()
    )
}

fun Chat.toEntity(): ChatEntity {
    return ChatEntity(
        chatId = id,
        lastActivityAt = lastActivityAt.toEpochMilliseconds()
    )
}

fun List<Chat>.toChatWithParticipants(): List<ChatWithParticipant> {
    return this.map { chat ->
        ChatWithParticipant(
            chat = chat.toEntity(),
            participants = chat.participants.toEntities(),
            lastMessageView = chat.lastMessage?.toView()
        )
    }
}
