package com.twugteam.admin.chat.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ChatMessageDto(
    val id: String,
    val chatId: String,
    val content: String,
    val senderId: String,
    val createdAt: String
)