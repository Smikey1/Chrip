package com.twugteam.admin.chat.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.twugteam.admin.chat.database.entities.ChatEntity
import com.twugteam.admin.chat.database.entities.ChatInfoEntity
import com.twugteam.admin.chat.database.entities.ChatParticipantEntity
import com.twugteam.admin.chat.database.entities.ChatWithParticipant
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Upsert
    suspend fun upsertChat(chatEntity: ChatEntity): ChatDao

    @Upsert
    suspend fun upsertChats(chatEntities: List<ChatEntity>): ChatDao

    @Query("delete from chatentity where chatId = :chatId")
    suspend fun deleteChatById(chatId: String)

    @Transaction
    suspend fun deleteChatsByIds(chatIds: List<String> ) {
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

    @Query("select * from chatentity order by lastActivityAt desc")
    fun getChatsWithParticipants(): Flow<List<ChatWithParticipant>>

    @Query("select * from chatentity where chatId = :chatId")
    suspend fun getChatById(chatId: String): ChatWithParticipant?

    @Query("""
        select cp.* from chatparticipantentity cp
        join chatparticipantcrossref cpcr
        on cp.userId = cpcr.userId
        where cpcr.chatId = :chatId and cpcr.isUserStillActiveToThisChat = true
        order by cp.username
    """)
    suspend fun getActiveParticipantByChatId(chatId: String): Flow<List<ChatParticipantEntity>>

    @Query("select * from chatentity where chatId = :chatId")
    fun getChatInfoById(chatId: String): Flow<ChatInfoEntity>?
}