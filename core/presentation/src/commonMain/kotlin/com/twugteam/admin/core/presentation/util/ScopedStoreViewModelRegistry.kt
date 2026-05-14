package com.twugteam.admin.core.presentation.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore

class ScopedStoreViewModelRegistry : ViewModel() {
    private var stores = mutableMapOf<String, ViewModelStore>()

    fun getOrCreateViewModelStore(id: String): ViewModelStore {
        return stores.getOrPut(
            key = id,
            defaultValue = {
                // Initialize new ViewModel Store
                ViewModelStore()
            }
        )
    }

    fun clearViewModelStoreFromRegistry(id: String) {
        val removedViewModelStore = stores.remove(id)
        // We need to make sure, it needs to be clear when the bottom sheet or dialog is onDismiss
        removedViewModelStore?.clear()
    }

    override fun onCleared() {
        super.onCleared()
        stores.values.forEach { viewModelStore ->
            viewModelStore.clear()
        }
        stores.clear()
    }
}