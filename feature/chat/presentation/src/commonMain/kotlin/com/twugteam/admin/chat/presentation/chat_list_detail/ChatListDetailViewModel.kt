package com.twugteam.admin.chat.presentation.chat_list_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.twugteam.admin.chat.domain.chat.ChatRealTimeService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class ChatListDetailViewModel(
    private val chatRealTimeService: ChatRealTimeService
) : ViewModel() {

    private var hasLoadedInitialData = false
    private val _state = MutableStateFlow(ChatListDetailState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                chatRealTimeService.chatMessage.launchIn(viewModelScope)
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
            is ChatListDetailAction.OnSelectClick -> {
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