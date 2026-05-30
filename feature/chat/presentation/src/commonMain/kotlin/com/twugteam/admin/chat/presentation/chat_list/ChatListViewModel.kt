package com.twugteam.admin.chat.presentation.chat_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.twugteam.admin.chat.domain.chat.ChatRepository
import com.twugteam.admin.chat.presentation.mappers.toUi
import com.twugteam.admin.core.domain.auth.SessionStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatListViewModel(
    private val chatRepository: ChatRepository,
    private val sessionStorage: SessionStorage,
) : ViewModel() {

    var hasLoadedInitialData: Boolean = false

    private val _state = MutableStateFlow(ChatListState())
    val state = combine(
        _state,
        chatRepository.getChats(),
        sessionStorage.observeAuthInfo()
    ) { currentState, chats, authInfo ->
        if (authInfo == null) {
            return@combine ChatListState()
        }
        currentState.copy(
            chats = chats.map { it.toUi(authInfo.user.id) },
            localParticipant = authInfo.user.toUi()
        )
    }.onStart {
        if (!hasLoadedInitialData) {
            loadChats()
            hasLoadedInitialData = true
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = ChatListState()
    )

    fun onAction(action: ChatListAction) {
        when (action) {
            is ChatListAction.OnSelectClick -> {
                _state.update {
                    it.copy(
                        selectedChatId = action.chatId
                    )
                }
            }

            else -> Unit
        }
    }

    private fun loadChats() {
        viewModelScope.launch {
            chatRepository.fetchChats()
        }
    }

}
