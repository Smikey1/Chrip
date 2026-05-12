package com.twugteam.admin.chat.domain.models

data class ChatParticipant(
    val userId : String,
    val username: String,
    val profilePictureUrl: String?,
) {
    val initial : String
        get() = username.take(2).uppercase()
}