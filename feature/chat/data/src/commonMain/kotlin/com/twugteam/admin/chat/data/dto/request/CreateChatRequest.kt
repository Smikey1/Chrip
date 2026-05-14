package com.twugteam.admin.chat.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class CreateChatRequest(
    val otherUserIds: List<String>
)
