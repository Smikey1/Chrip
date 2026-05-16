package com.twugteam.admin.chat.data.di

import com.twugteam.admin.chat.database.DatabaseFactory
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformChatDataModule: Module = module {
    single { DatabaseFactory(androidContext()) }
}