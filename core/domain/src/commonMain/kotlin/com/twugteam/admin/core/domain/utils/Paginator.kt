package com.twugteam.admin.core.domain.utils

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.newCoroutineContext
import kotlin.coroutines.coroutineContext

class Paginator<Key, Item>(
    private val initialKey: Key,
    private val onLoadUpdated: (Boolean) -> Unit,
    private val onRequest: suspend (nextKey: Key) -> Result<List<Item>, DataError>,
    private val getNextKey: suspend (fromItems: List<Item>) -> Key,
    private val onError: (Throwable?) -> Unit,
    private val onSuccess: suspend (items: List<Item>, newKey: Key) -> Unit

) {
    private var currentKey = initialKey
    private var isMakingRequest = false
    private var lastRequestKey: Key? = null

    suspend fun loadNextItem() {
        if (isMakingRequest) {
            return
        }
        if (currentKey != null && currentKey == lastRequestKey){
            return
        }
        isMakingRequest = true
        lastRequestKey = currentKey
        onLoadUpdated(true)

        try {
            onRequest(currentKey)
                .onSuccess { items ->
                    val newKey = getNextKey(items)
                    onSuccess(items, newKey)
                    currentKey = newKey
                }.onFailure { error ->
                    onError(DataErrorException(error))
                }

        }catch (e: Exception){
            currentCoroutineContext().ensureActive()
            onError(e)
        } finally {
            onLoadUpdated(false)
            isMakingRequest = false
        }
    }

    fun reset(){
        currentKey = initialKey
        lastRequestKey = null
    }
}