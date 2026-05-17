package com.twugteam.admin.chat.domain.models

import kotlin.time.Instant

data class ChatMessage(
    val id: String,
    val chatId: String,
    val senderId: String,
    val content: String,
    val createdAt: Instant,
    val deliveryStatus: ChatMessageDeliveryStatus
)
