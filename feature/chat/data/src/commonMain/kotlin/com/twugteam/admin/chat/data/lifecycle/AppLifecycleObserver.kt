package com.twugteam.admin.chat.data.lifecycle

import kotlinx.coroutines.flow.Flow

expect class AppLifecycleObserver {
    val isAppInForeground: Flow<Boolean>
}