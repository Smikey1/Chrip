package com.twugteam.admin.chat.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.twugteam.admin.chat.database.entities.ChatMessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageDao {
    @Upsert
    suspend fun upsertMessage(message: ChatMessageEntity)

    @Upsert
    suspend fun upsertMessages(messages: List<ChatMessageEntity>)

    @Query("delete from chatmessageentity where messageId = :messageId")
    suspend fun deleteMessageById(messageId: String)

    @Query("delete from chatmessageentity where messageId in (:messageIds)")
    suspend fun deleteMultipleMessageByIds(messageIds: List<String>)


//    // Alternative, By Using Transactions
//    @Transaction
//    suspend fun deleteMultipleMessageByIds(messageIds: List<String>) {
//        messageIds.forEach { messageId ->
//            deleteMessageById(messageId)
//        }
//    }


    @Query("select * from chatmessageentity where messageId = :messageId")
    fun getMessageById(messageId: String): ChatMessageEntity?

    @Query("select * from chatmessageentity where chatId= :chatId order by timestamp desc")
    fun getAllMessagesByChatId(chatId: String): Flow<List<ChatMessageEntity>>

}