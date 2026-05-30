package com.twugteam.admin.chat.presentation.mappers

import com.twugteam.admin.chat.domain.models.ChatParticipant
import com.twugteam.admin.core.designsystem.components.avatar.ChatParticipantUi
import com.twugteam.admin.core.domain.auth.User


fun ChatParticipant.toUi(): ChatParticipantUi {
    return ChatParticipantUi(
        userId = userId,
        username = username,
        imageUrl = profilePictureUrl
    )
}

fun User.toUi(): ChatParticipantUi {
    return ChatParticipantUi(
        userId = id,
        username = username,
        imageUrl = profilePictureUrl
    )
}