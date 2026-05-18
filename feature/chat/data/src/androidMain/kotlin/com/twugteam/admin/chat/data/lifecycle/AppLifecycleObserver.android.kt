package com.twugteam.admin.chat.data.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ProcessLifecycleOwner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn

actual class AppLifecycleObserver {
    actual val isAppInForeground: Flow<Boolean> = callbackFlow<Boolean> {

        val processLifecycleOwner = ProcessLifecycleOwner.get().lifecycle
        val atLeastStarted = processLifecycleOwner.currentState.isAtLeast(Lifecycle.State.STARTED)
        send(atLeastStarted)

        val lifecycleEventObserver = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> trySend(true)
                Lifecycle.Event.ON_STOP -> trySend(false)
                else -> Unit
            }
        }

        processLifecycleOwner.addObserver(lifecycleEventObserver)

        awaitClose {
            processLifecycleOwner.removeObserver(lifecycleEventObserver)
        }

    }.flowOn(Dispatchers.Main)
}