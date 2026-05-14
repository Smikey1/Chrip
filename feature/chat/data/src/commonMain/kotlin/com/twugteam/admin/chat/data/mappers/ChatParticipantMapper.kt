package com.twugteam.admin.chat.data.mappers

import com.twugteam.admin.chat.data.dto.ChatParticipantDto
import com.twugteam.admin.chat.domain.models.ChatParticipant

fun ChatParticipantDto.toDomain(): ChatParticipant {
    return ChatParticipant(
        userId = userId,
        username = username,
        profilePictureUrl = profilePictureUrl
    )
}