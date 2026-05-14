package com.twugteam.admin.chat.domain.models

data class ChatInfo(
    val messages: List<MessageWithSender>,
    val chat: Chat
)
