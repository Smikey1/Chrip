package com.twugteam.admin.chat.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import com.twugteam.admin.chat.database.dao.ChatDao
import com.twugteam.admin.chat.database.dao.ChatMessageDao
import com.twugteam.admin.chat.database.dao.ChatParticipantCrossRefDao
import com.twugteam.admin.chat.database.dao.ChatParticipantDao
import com.twugteam.admin.chat.database.entities.ChatEntity
import com.twugteam.admin.chat.database.entities.ChatMessageEntity
import com.twugteam.admin.chat.database.entities.ChatParticipantCrossRef
import com.twugteam.admin.chat.database.entities.ChatParticipantEntity
import com.twugteam.admin.chat.database.view.LastMessageView

@Database(
    entities = [
        ChatEntity::class,
        ChatMessageEntity::class,
        ChatParticipantEntity::class,
        ChatParticipantCrossRef::class
    ],
    views = [LastMessageView::class],
    version = 1,
)

@ConstructedBy(ChirpChatDatabaseConstructor::class)
abstract class ChirpChatDatabase : RoomDatabase() {
    abstract val chatDao: ChatDao
    abstract val chatMessageDao: ChatMessageDao
    abstract val chatParticipantDao: ChatParticipantDao
    abstract val chatParticipantCrossRefDao: ChatParticipantCrossRefDao

    companion object {
        const val DB_NAME = "chirp.db"
    }
}