package com.twugteam.admin.chat.data.dto.websocket

import kotlinx.serialization.Serializable


enum class OutgoingWebSocketType {
    // this case matter because form server the type is in this format
    NEW_MESSAGE,

}

@Serializable
sealed class OutgoingWebSocketDto(val type: OutgoingWebSocketType) {

    @Serializable
    data class NewMessage(
        val chatId: String,
        val messageId: String,
        val content: String,
    ) : OutgoingWebSocketDto(OutgoingWebSocketType.NEW_MESSAGE)

}