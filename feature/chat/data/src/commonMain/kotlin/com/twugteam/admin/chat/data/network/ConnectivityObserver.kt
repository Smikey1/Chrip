package com.twugteam.admin.chat.data.network

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

expect class ConnectivityObserver {
    val isConnectedToNetwork: Flow<Boolean>
}