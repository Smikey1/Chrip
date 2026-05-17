package com.twugteam.admin.chat.database

import androidx.room.RoomDatabaseConstructor


/*
This object is necessary to fix the error below:
--> The @Database class must be annotated with @ConstructedBy since the source is targeting non-Android platforms.

And Under the hood, room will generate the actual for database constructor for targeting other platform.
 */

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object ChirpChatDatabaseConstructor: RoomDatabaseConstructor<ChirpChatDatabase> {
    override fun initialize(): ChirpChatDatabase
}