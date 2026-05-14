package com.twugteam.admin.chat.presentation.mappers

import com.twugteam.admin.chat.domain.models.ChatParticipant
import com.twugteam.admin.core.designsystem.components.avatar.ChatParticipantUi


fun ChatParticipant.toUi(): ChatParticipantUi {
    return ChatParticipantUi(
        userId = userId,
        username = username,
        imageUrl = profilePictureUrl
    )
}