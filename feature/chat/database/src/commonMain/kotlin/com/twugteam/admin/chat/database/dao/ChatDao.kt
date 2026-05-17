package com.twugteam.admin.chat.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.twugteam.admin.chat.database.entities.ChatEntity
import com.twugteam.admin.chat.database.entities.ChatInfoEntity
import com.twugteam.admin.chat.database.entities.ChatMessageEntity
import com.twugteam.admin.chat.database.entities.ChatParticipantCrossRef
import com.twugteam.admin.chat.database.entities.ChatParticipantEntity
import com.twugteam.admin.chat.database.entities.ChatWithParticipant
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Upsert
    suspend fun upsertChat(chatEntity: ChatEntity)

    @Upsert
    suspend fun upsertChats(chatEntities: List<ChatEntity>)

    @Query("delete from chatentity where chatId = :chatId")
    suspend fun deleteChatById(chatId: String)

    @Transaction
    suspend fun deleteChatsByIds(chatIds: List<String>) {
        chatIds.forEach { chatId ->
            deleteChatById(chatId)
        }
    }

    @Query("delete from chatentity")
    suspend fun deleteAllChats()

    @Query("select chatId from chatentity")
    suspend fun getAllChatIds(): List<String>

    @Query("select count(*) from chatentity")
    fun getTotalChatCount(): Flow<Int>

    @Query(
        """
        select distinct c.* from chatentity c
        join chatparticipantcrossref cpcr
        on c.chatId = cpcr.chatId
        where cpcr.isUserStillActiveToThisChat = true
        order by c.lastActivityAt desc
    """
    )
    @Transaction
    fun getAllChatsWithActiveParticipants(): Flow<List<ChatWithParticipant>>

    @Query("select * from chatentity order by lastActivityAt desc")
    @Transaction
    fun getChatsWithParticipants(): Flow<List<ChatWithParticipant>>

    @Query("select * from chatentity where chatId = :chatId")
    @Transaction
    suspend fun getChatById(chatId: String): ChatWithParticipant?

    @Query(
        """
        select cp.* from chatparticipantentity cp
        join chatparticipantcrossref cpcr
        on cp.userId = cpcr.userId
        where cpcr.chatId = :chatId and cpcr.isUserStillActiveToThisChat = true
        order by cp.username
    """
    )
    fun getActiveParticipantByChatId(chatId: String): Flow<List<ChatParticipantEntity>>

    @Query("select * from chatentity where chatId = :chatId")
    @Transaction
    fun getChatInfoById(chatId: String): Flow<ChatInfoEntity>?

    @Transaction
    suspend fun upsertChatWithParticipantAndCrossRefs(
        chatEntity: ChatEntity,
        participants: List<ChatParticipantEntity>,
        crossRefs: List<ChatParticipantCrossRef>,
        chatParticipantDao: ChatParticipantDao,
        crossRefDao: ChatParticipantCrossRefDao
    ) {
        upsertChat(chatEntity)
        chatParticipantDao.upsertParticipants(participants)

        val crossRefs = participants.map { participant ->
            ChatParticipantCrossRef(
                chatId = chatEntity.chatId,
                userId = participant.userId,
                isUserStillActiveToThisChat = true
            )
        }
        crossRefDao.upsertCrossRefs(crossRefs)

        crossRefDao.syncChatParticipants(
            chatId = chatEntity.chatId,
            participants = participants
        )
    }

    @Transaction
    suspend fun upsertChatsWithParticipantAndCrossRefs(
        chatWithParticipants: List<ChatWithParticipant>,
        chatParticipantDao: ChatParticipantDao,
        crossRefDao: ChatParticipantCrossRefDao,
        messageDao: ChatMessageDao
    ) {
        upsertChats(chatWithParticipants.map { it.chat })

        val serverChatIds = chatWithParticipants.map { it.chat.chatId }.toSet()
        val localChatIds = getAllChatIds().toSet()
        val chatIdsContainInLocalDeviceButNotServerSide = (localChatIds - serverChatIds).toList()

        chatWithParticipants.forEach { chatWithParticipant ->
            chatWithParticipant.lastMessageView?.run {
                val chatMessageEntity = ChatMessageEntity(
                    messageId = messageId,
                    chatId = chatId,
                    senderId = senderId,
                    content = content,
                    timestamp = timestamp,
                    deliveryStatus = deliveryStatus
                )
                messageDao.upsertMessage(chatMessageEntity)
            }
        }

        val allParticipants = chatWithParticipants.flatMap {
            it.participants
        }
        chatParticipantDao.upsertParticipants(allParticipants)

        val crossRefs = chatWithParticipants.flatMap { chatWithParticipant ->
            chatWithParticipant.participants.map { participant ->
                ChatParticipantCrossRef(
                    chatId = chatWithParticipant.chat.chatId,
                    userId = participant.userId,
                    isUserStillActiveToThisChat = true
                )
            }
        }
        crossRefDao.upsertCrossRefs(crossRefs)

        chatWithParticipants.forEach { chatWithParticipant ->
            crossRefDao.syncChatParticipants(
                chatId = chatWithParticipant.chat.chatId,
                participants = chatWithParticipant.participants
            )
        }

        deleteChatsByIds(chatIdsContainInLocalDeviceButNotServerSide)
    }
}