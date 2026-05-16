package com.twugteam.admin.chat.data.di

import com.twugteam.admin.chat.database.DatabaseFactory
import org.koin.dsl.module

actual val platformChatDataModule = module {
    single { DatabaseFactory() }
}