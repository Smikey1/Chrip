@file:OptIn(ExperimentalUuidApi::class)

package com.twugteam.admin.core.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocal
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import org.koin.compose.viewmodel.koinViewModel
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

// Because it combines with Bottom Sheet (in Mobile) + Dialog (in Tablet/Desktop) Screens.

@Composable
fun DialogSheetScopedViewModel(
    visible: Boolean,
    scopeId: String = rememberSaveable { Uuid.random().toString() },
    content: @Composable () -> Unit
) {
    val parentViewModelStoreOwner = LocalViewModelStoreOwner.current
        ?: throw IllegalStateException("No Parent ViewModel Owner found")

    val registry = koinViewModel<ScopedStoreViewModelRegistry>(
        viewModelStoreOwner = parentViewModelStoreOwner
    )

    var viewModelStoreOwner by remember { mutableStateOf<ViewModelStoreOwner?>(null) }

    LaunchedEffect(visible,scopeId){
        // 1st case --> visible change to true, and there is no view-model owner doesn't exist,
        // then we create a new viewmodel owner
        if(visible && viewModelStoreOwner == null){
            viewModelStoreOwner = object : ViewModelStoreOwner {
                override val viewModelStore: ViewModelStore
                    get() = registry.getOrCreateViewModelStore(scopeId)

            }
            // 2nd case --> the bottom sheet or dialog is not visible and
            // view-model store owner do exist, then we need to remove it.
        } else if(!visible && viewModelStoreOwner != null){
            registry.clearViewModelStoreFromRegistry(scopeId)
            viewModelStoreOwner = null
        }
    }

    viewModelStoreOwner?.let { currentDialogViewModelStoreOwner ->
        CompositionLocalProvider(LocalViewModelStoreOwner provides currentDialogViewModelStoreOwner){
            content()
        }
    }

}