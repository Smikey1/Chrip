package com.twugteam.admin.chat.data.mappers

import com.twugteam.admin.chat.data.dto.ChatDto
import com.twugteam.admin.chat.database.entities.ChatEntity
import com.twugteam.admin.chat.database.entities.ChatInfoEntity
import com.twugteam.admin.chat.database.entities.ChatWithParticipant
import com.twugteam.admin.chat.database.entities.MessageWithSender
import com.twugteam.admin.chat.domain.models.Chat
import com.twugteam.admin.chat.domain.models.ChatInfo
import com.twugteam.admin.chat.domain.models.ChatMessage
import com.twugteam.admin.chat.domain.models.ChatMessageDeliveryStatus
import com.twugteam.admin.chat.domain.models.ChatParticipant
import kotlin.time.Instant

typealias DataMessageWithSender = MessageWithSender
typealias DomainMessageWithSender = com.twugteam.admin.chat.domain.models.MessageWithSender

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

fun ChatEntity.toDomain(
    participants: List<ChatParticipant>,
    lastMessage: ChatMessage? = null
): Chat {
    return Chat(
        id = chatId,
        lastActivityAt = Instant.fromEpochMilliseconds(lastActivityAt),
        participants = participants,
        lastMessage = lastMessage
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

fun DataMessageWithSender.toDomain(): DomainMessageWithSender {
    return DomainMessageWithSender(
        message = message.tDomain(),
        sender = sender.toDomain(),
        deliveryStatus = ChatMessageDeliveryStatus.valueOf(message.deliveryStatus)
    )
}

fun ChatInfoEntity.toDomain(): ChatInfo {
    return ChatInfo(
        chat = chat.toDomain(
            participants = participants.map { it.toDomain() },
        ),
        messages = messageWithSenders.map {
            it.toDomain()
        }
    )
}
