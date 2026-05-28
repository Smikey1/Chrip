package com.twugteam.admin.chat.data.di

import com.twugteam.admin.chat.data.lifecycle.AppLifecycleObserver
import com.twugteam.admin.chat.data.network.ConnectionErrorHandler
import com.twugteam.admin.chat.data.network.ConnectivityObserver
import com.twugteam.admin.chat.database.DatabaseFactory
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val platformChatDataModule = module {
    single { DatabaseFactory() }

    singleOf(::AppLifecycleObserver)
    singleOf(::ConnectivityObserver)
    singleOf(::ConnectionErrorHandler)
}