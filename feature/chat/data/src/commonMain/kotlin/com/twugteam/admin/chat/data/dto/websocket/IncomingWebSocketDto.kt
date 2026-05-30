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
sealed interface IncomingWebSocketDto {

    @Serializable
    data class NewMessage(
        val id: String,
        val chatId: String,
        val senderId: String,
        val content: String,
        val createdAt: String,
        val type: IncomingWebSocketType = IncomingWebSocketType.NEW_MESSAGE
    ) : IncomingWebSocketDto

    @Serializable
    data class MessageDeleted(
        val chatId: String,
        val messageId: String,
        val type: IncomingWebSocketType = IncomingWebSocketType.MESSAGE_DELETED
    ) : IncomingWebSocketDto

    @Serializable
    data class ProfilePictureUpdated(
        val userId: String,
        val newUrl: String?, // url nullable because user might remove the existing profile pic
        val type: IncomingWebSocketType = IncomingWebSocketType.PROFILE_PICTURE_UPDATED
    ) : IncomingWebSocketDto

    @Serializable
    data class ChatParticipantChanged(
        val chatId: String,
        val type: IncomingWebSocketType = IncomingWebSocketType.CHAT_PARTICIPANTS_CHANGED
    ) : IncomingWebSocketDto
}