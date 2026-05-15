package com.twugteam.admin.chat.presentation.chat_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.twugteam.admin.chat.presentation.model.ChatUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

class ChatListViewModel : ViewModel() {

    var hasLoadedInitialData: Boolean = false

    private val _state = MutableStateFlow(ChatListState())
    val state = _state
        .onEach {
            if (!hasLoadedInitialData) {

                hasLoadedInitialData = true
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = ChatListState()
        )

    fun onAction(action: ChatListAction) {
        when (action) {
            else -> Unit
        }
    }


}
