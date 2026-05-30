package com.twugteam.admin.chat.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.twugteam.admin.chat.database.entities.ChatParticipantCrossRef
import com.twugteam.admin.chat.database.entities.ChatParticipantEntity

@Dao
interface ChatParticipantCrossRefDao {

    @Upsert
    suspend fun upsertCrossRefs(crossRefs: List<ChatParticipantCrossRef>)

    @Query("select userId from chatparticipantcrossref where chatId=:chatId and isUserStillActiveToThisChat = true")
    suspend fun getActiveParticipantIdsByChat(chatId: String): List<String>

    @Query("SELECT userId FROM chatparticipantcrossref where chatId = :chatId")
    suspend fun getAllParticipantIdsByChat(chatId: String): List<String>

    @Query("""
        update chatparticipantcrossref
        set isUserStillActiveToThisChat = false
        where chatId = :chatId and userId in (:userIds)
    """)
    suspend fun markParticipantAsInactive(chatId: String, userIds: List<String>)

    @Query("""
        update chatparticipantcrossref
        set isUserStillActiveToThisChat = true
        where chatId = :chatId and userId in (:userIds)
    """)
    suspend fun markParticipantAsActive(chatId: String,userIds: List<String>)

    @Transaction
    suspend fun syncChatParticipants(chatId: String, participants: List<ChatParticipantEntity>) {
        if(participants.isEmpty()){
            return
        }

        val serverParticipantIds = participants.map { it.userId }.toSet()
        val allLocalParticipantIds = getAllParticipantIdsByChat(chatId).toSet()
        val activeLocalParticipantIds = getActiveParticipantIdsByChat(chatId).toSet()
        val inactiveLocalParticipantIds = allLocalParticipantIds-activeLocalParticipantIds

        val allParticipantWhoRejoinChatAgain = serverParticipantIds.intersect(inactiveLocalParticipantIds)
        val allParticipantWhoLeaveTheChat = activeLocalParticipantIds - serverParticipantIds

        markParticipantAsActive(chatId,allParticipantWhoRejoinChatAgain.toList())
        markParticipantAsInactive(chatId,allParticipantWhoLeaveTheChat.toList())

        val newParticipantRecentlyJoinTheChat = serverParticipantIds - allLocalParticipantIds

        val newCrossRefs = newParticipantRecentlyJoinTheChat.map { userId ->
            ChatParticipantCrossRef(
                chatId = chatId,
                userId = userId,
                isUserStillActiveToThisChat = true
            )
        }
        upsertCrossRefs(newCrossRefs)
    }
}