package com.twugteam.admin.chat.data.dto.websocket

import kotlinx.serialization.Serializable


enum class IncomingWebSocketType {
    // this case matter because form server the type is in this format
    NEW_MESSAGE,
    MESSAGE_DELETED,
    PROFILE_PICTURE_UPDATED,
    CHAT_PARTICIPANTS_CHANGED
}

@Serializable
sealed class IncomingWebSocketDto(val type: IncomingWebSocketType) {

    @Serializable
    data class NewMessage(
        val id: String,
        val chatId: String,
        val senderId: String,
        val content: String,
        val createdAt: String
    ) : IncomingWebSocketDto(IncomingWebSocketType.NEW_MESSAGE)

    @Serializable
    data class MessageDeleted(
        val chatId: String,
        val messageId: String
    ) : IncomingWebSocketDto(IncomingWebSocketType.MESSAGE_DELETED)

    @Serializable
    data class ProfilePictureUpdated(
        val userId: String,
        val newUrl: String? // url nullable because user might remove the existing profile pic
    ) : IncomingWebSocketDto(IncomingWebSocketType.PROFILE_PICTURE_UPDATED)

    @Serializable
    data class ChatParticipantChanged(
        val chatId: String
    ) : IncomingWebSocketDto(IncomingWebSocketType.CHAT_PARTICIPANTS_CHANGED)
}