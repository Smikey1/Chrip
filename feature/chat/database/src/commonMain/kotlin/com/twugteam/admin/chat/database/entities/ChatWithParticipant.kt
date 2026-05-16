package com.twugteam.admin.chat.database.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.twugteam.admin.chat.database.view.LastMessageView

data class ChatWithParticipant(
    @Embedded
    val chat: ChatEntity,
    @Relation(
        parentColumn = "chatId",
        entityColumn = "userId",
        associateBy = Junction(ChatParticipantCrossRef::class)
    )
    val participants: List<ChatParticipantEntity>,

    @Relation(
        parentColumn = "chatId",
        entityColumn = "chatId",
        entity = LastMessageView::class
    )
    val lastMessageView: LastMessageView?
)


data class ChatInfoEntity(
    @Embedded
    val chat: ChatEntity,
    @Relation(
        parentColumn = "chatId",
        entityColumn = "userId",
        associateBy = Junction(ChatParticipantCrossRef::class)
    )
    val participants: List<ChatParticipantEntity>,

    @Relation(
        parentColumn = "chatId",
        entityColumn = "chatId",
        entity = ChatMessageEntity::class
    )
    val messageWithSenders: List<MessageWithSender>
)
