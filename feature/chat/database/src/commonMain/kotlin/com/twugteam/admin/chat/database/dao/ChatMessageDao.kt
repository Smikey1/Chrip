package com.twugteam.admin.chat.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.twugteam.admin.chat.database.entities.ChatMessageEntity
import com.twugteam.admin.chat.database.entities.MessageWithSender
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

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
    suspend fun getMessageById(messageId: String): ChatMessageEntity?

    @Query("select * from chatmessageentity where chatId= :chatId order by timestamp desc")
    fun getAllMessagesByChatId(chatId: String): Flow<List<MessageWithSender>>

    @Query("select * from chatmessageentity where chatId= :chatId order by timestamp desc limit :limit")
    fun getLimitedMessagesByChatId(chatId: String, limit: Int): Flow<List<ChatMessageEntity>>

    @Query(
        """
        update chatmessageentity
        set deliveryStatus = :deliveryStatus, deliveryStatusTimestamp= :timestamp
        where messageId = :messageId
    """
    )
    suspend fun updateDeliveryStatus(messageId: String, deliveryStatus: String, timestamp: Long)

    @Transaction
    suspend fun upsertMessageAndSyncIfNeeded(
        chatId: String,
        serverMessages: List<ChatMessageEntity>,
        pageSize: Int,
        shouldSync: Boolean = false
    ) {
        val localMessages = getLimitedMessagesByChatId(chatId = chatId, limit = pageSize).first()

        upsertMessages(serverMessages)
        if (!shouldSync) {
            return
        }
        val serverMessagesIds = serverMessages.map { it.messageId }.toSet()
        val deletedMessageIdsServerSide = localMessages.filter {
            val missingOnServer = it.messageId !in serverMessagesIds
            val isSent = it.deliveryStatus == "SENT"
            missingOnServer && isSent
        }

        deleteMultipleMessageByIds(messageIds = deletedMessageIdsServerSide.map { it.messageId })
    }

}