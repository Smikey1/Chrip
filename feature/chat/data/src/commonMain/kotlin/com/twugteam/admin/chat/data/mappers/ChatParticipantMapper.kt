package com.twugteam.admin.chat.data.mappers

import com.twugteam.admin.chat.data.dto.ChatParticipantDto
import com.twugteam.admin.chat.database.entities.ChatParticipantEntity
import com.twugteam.admin.chat.domain.models.ChatParticipant
import com.twugteam.admin.core.domain.auth.User

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

fun ChatParticipant.toUser(
    email: String,
    hasVerifiedEmail: Boolean
): User {
    return User(
        id = userId,
        username = username,
        profilePictureUrl = profilePictureUrl,
        email = email,
        hasVerifiedEmail = hasVerifiedEmail
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
