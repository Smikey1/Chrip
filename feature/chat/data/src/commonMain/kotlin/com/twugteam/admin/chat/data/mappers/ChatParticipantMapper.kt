package com.twugteam.admin.chat.data.mappers

import com.twugteam.admin.chat.data.dto.ChatParticipantDto
import com.twugteam.admin.chat.database.entities.ChatParticipantEntity
import com.twugteam.admin.chat.domain.models.ChatParticipant

fun ChatParticipantDto.toDomain(): ChatParticipant {
    return ChatParticipant(
        userId = userId,
        username = username,
        profilePictureUrl = profilePictureUrl
    )
}

fun ChatParticipantEntity.toDomain(): ChatParticipant {
    return ChatParticipant(
        userId = userId,
        username = username,
        profilePictureUrl = profilePictureUrl
    )
}

fun ChatParticipant.toEntity(): ChatParticipantEntity {
    return ChatParticipantEntity(
        userId = userId,
        username = username,
        profilePictureUrl = profilePictureUrl
    )
}

fun List<ChatParticipant>.toEntities(): List<ChatParticipantEntity> {
    return this.map { chatParticipant ->
        ChatParticipantEntity(
            userId = chatParticipant.userId,
            username = chatParticipant.username,
            profilePictureUrl = chatParticipant.profilePictureUrl
        )
    }
}
