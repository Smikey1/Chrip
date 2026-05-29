package com.twugteam.admin.chat.presentation.mappers

import com.twugteam.admin.chat.domain.models.MessageWithSender
import com.twugteam.admin.chat.presentation.model.MessageUi
import com.twugteam.admin.chat.presentation.util.DateUtils

fun MessageWithSender.toUi(
    localUserId: String,
): MessageUi {
    val isFromLocalUser = localUserId == sender.userId
    return if (isFromLocalUser) {
        MessageUi.LocalUserMessage(
            id = message.id,
            content = message.content,
            deliveryStatus = message.deliveryStatus,
            formattedSentTime = DateUtils.formatMessageTime(message.createdAt)
        )
    } else {
        MessageUi.OtherUserMessage(
            id = message.id,
            content = message.content,
            formattedSentTime = DateUtils.formatMessageTime(message.createdAt),
            sender = sender.toUi()
        )
    }
}
