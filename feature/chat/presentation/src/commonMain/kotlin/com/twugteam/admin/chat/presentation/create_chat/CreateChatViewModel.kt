package com.twugteam.admin.chat.presentation.create_chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class CreateChatViewModel : ViewModel() {

    var hasLoadedInitialData: Boolean = false

    private val eventChannel = Channel<CreateChatEvent>()
    val events = eventChannel.receiveAsFlow()

    private val _state = MutableStateFlow(CreateChatState())
    val state = _state
        .onEach {
            if (!hasLoadedInitialData) {

                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = CreateChatState()
        )

    fun onAction(action: CreateChatAction) {
        when (action) {
            CreateChatAction.OnDismissDialog -> {
                _state.update {
                    it.copy(

                    )
                }
            }

            else -> {}
        }
    }
}