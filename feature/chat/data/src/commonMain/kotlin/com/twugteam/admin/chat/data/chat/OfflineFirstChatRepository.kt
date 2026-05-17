package com.twugteam.admin.chat.data.chat

import com.twugteam.admin.chat.data.mappers.toDomain
import com.twugteam.admin.chat.data.mappers.toEntities
import com.twugteam.admin.chat.data.mappers.toEntity
import com.twugteam.admin.chat.data.mappers.toView
import com.twugteam.admin.chat.database.ChirpChatDatabase
import com.twugteam.admin.chat.database.entities.ChatWithParticipant
import com.twugteam.admin.chat.domain.chat.ChatRepository
import com.twugteam.admin.chat.domain.chat.ChatService
import com.twugteam.admin.chat.domain.models.Chat
import com.twugteam.admin.core.domain.utils.DataError
import com.twugteam.admin.core.domain.utils.Result
import com.twugteam.admin.core.domain.utils.onSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OfflineFirstChatRepository(
    private val chatService: ChatService,
    private val db: ChirpChatDatabase
) : ChatRepository {
    override fun getChats(): Flow<List<Chat>> {
        return db.chatDao.getAllChatsWithActiveParticipants().map { chatWithParticipants ->
            chatWithParticipants.map { chatWithParticipant ->
                chatWithParticipant.toDomain()
            }
        }
    }

    override suspend fun fetchChats(): Result<List<Chat>, DataError.Remote> {
        return chatService
            .fetchChats()
            .onSuccess { chats ->
                val chatWithParticipants = chats.map { chat ->
                    ChatWithParticipant(
                        chat = chat.toEntity(),
                        participants = chat.participants.toEntities(),
                        lastMessageView = chat.lastMessage?.toView()
                    )
                }
                db.chatDao.upsertChatsWithParticipantAndCrossRefs(
                    chatWithParticipants = chatWithParticipants,
                    chatParticipantDao = db.chatParticipantDao,
                    crossRefDao = db.chatParticipantCrossRefDao,
                    messageDao = db.chatMessageDao
                )

            }
    }

}