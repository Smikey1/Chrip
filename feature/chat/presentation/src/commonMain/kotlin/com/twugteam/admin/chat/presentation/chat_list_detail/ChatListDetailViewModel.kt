package com.twugteam.admin.chat.presentation.chat_list_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class ChatListDetailViewModel : ViewModel() {

    private var hasLoadedInitialData = false
    private val _state = MutableStateFlow(ChatListDetailState())
    val state = _state
        .onEach {
            if (!hasLoadedInitialData) {

                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = ChatListDetailState()
        )


    fun onAction(action: ChatListDetailAction) {
        when (action) {
            is ChatListDetailAction.OnChatClick -> {
                _state.update {
                    it.copy(
                        selectedChatId = action.chatId
                    )
                }
            }

            ChatListDetailAction.OnCreateChatClick -> {
                _state.update {
                    it.copy(
                        dialogState = DialogState.CreateChat
                    )
                }
            }

            ChatListDetailAction.OnDismissClick -> {
                _state.update {
                    it.copy(
                        dialogState = DialogState.Hidden
                    )
                }
            }

            is ChatListDetailAction.OnManageChatClick -> {
                state.value.selectedChatId?.let { selectedChatId ->
                    _state.update {
                        it.copy(
                            dialogState = DialogState.ManageChat(selectedChatId)
                        )
                    }
                }
            }

            ChatListDetailAction.OnProfileSettingClick -> {
                _state.update {
                    it.copy(
                        dialogState = DialogState.Profile
                    )
                }
            }
        }
    }
}