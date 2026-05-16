package com.twugteam.admin.chat.database.view

import androidx.room.DatabaseView

@DatabaseView(
    viewName = "last_message_view_per_chat",
    value = """
        select cm1.* from chatmessageentity cm1
        join ( select chatId, MAX(timestamp) as max_timestamp
        from chatmessageentity group by chatId ) as cm2
        on cm1.chatId = cm2.chatId and cm1.timestamp = cm2.max_timestamp
    """
)
data class LastMessageView(
    val messageId: String,
    val chatId: String,
    val senderId: String,
    val content: String,
    val timestamp: Long
)