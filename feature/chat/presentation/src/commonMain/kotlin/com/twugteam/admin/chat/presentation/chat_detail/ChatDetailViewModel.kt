@file:OptIn(ExperimentalCoroutinesApi::class)

package com.twugteam.admin.chat.presentation.chat_detail

import androidx.compose.foundation.text.input.clearText
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.twugteam.admin.chat.domain.chat.ChatRepository
import com.twugteam.admin.chat.presentation.mappers.toUi
import com.twugteam.admin.core.domain.auth.SessionStorage
import com.twugteam.admin.core.domain.utils.onFailure
import com.twugteam.admin.core.domain.utils.onSuccess
import com.twugteam.admin.core.presentation.util.toUiText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatDetailViewModel(
    private val chatRepository: ChatRepository,
    private val sessionStorage: SessionStorage,
) : ViewModel() {
    private var hasLoadedInitialData = false

    private val eventChannel = Channel<ChatDetailEvent>()
    val events = eventChannel.receiveAsFlow()

    private val _chatId = MutableStateFlow<String?>(null)
    private val _chatInfoFlow = _chatId
        .flatMapLatest { chatId ->
            if (chatId != null) {
                chatRepository.getChatInfoById(chatId)
            } else emptyFlow()
        }

    private val _state = MutableStateFlow(ChatDetailState())

    private val stateWithMessages = combine(
        _state,
        _chatInfoFlow,
        sessionStorage.observeAuthInfo(),
    ) { currentState, chatInfo, authInfo ->
        if (authInfo == null) {
            return@combine ChatDetailState()
        }
        currentState.copy(
            chatUi = chatInfo.chat.toUi(authInfo.user.id),
        )
    }

    val state = _chatId.flatMapLatest { chatId ->
        if (chatId != null) {
            stateWithMessages
        } else _state
    }.onStart {
        if (!hasLoadedInitialData) {

            hasLoadedInitialData = true
        }
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = ChatDetailState()
        )

    fun onAction(action: ChatDetailAction) {
        when (action) {
            is ChatDetailAction.OnSelectChat -> switchChat(action.chatId)
            ChatDetailAction.OnLeaveChatClick -> onLeaveChatClick()
            ChatDetailAction.OnBackClick -> TODO()
            ChatDetailAction.OnChatMembersClick -> TODO()
            ChatDetailAction.OnChatMenuClick -> TODO()
            ChatDetailAction.OnChatOptionsClick -> onChatOptionClick()
            is ChatDetailAction.OnDeleteMessageClick -> TODO()
            ChatDetailAction.OnDismissChatOptions -> onDismissChatOptionClick()
            ChatDetailAction.OnDismissMessageMenu -> TODO()
            is ChatDetailAction.OnMessageLongClick -> TODO()
            is ChatDetailAction.OnRetryClick -> TODO()
            ChatDetailAction.OnScrollToTop -> TODO()
            ChatDetailAction.OnSendMessageClick -> TODO()
        }
    }

    private fun onChatOptionClick() {
        _state.update {
            it.copy(
                isChatOptionMenuOpen = true
            )
        }
    }

    private fun onDismissChatOptionClick() {
        _state.update {
            it.copy(
                isChatOptionMenuOpen = false
            )
        }
    }

    private fun switchChat(chatId: String?) {
        _chatId.update { chatId }
        chatId?.let {
            viewModelScope.launch {
                chatRepository.fetchChatById(chatId)
            }
        }
    }

    private fun onLeaveChatClick() {
        val currentlySelectedChatId = _chatId.value ?: return
        _state.update {
            it.copy(
                isChatOptionMenuOpen = false
            )
        }
        viewModelScope.launch {
            chatRepository
                .leaveChat(currentlySelectedChatId)
                .onSuccess {
                    _state.value.messageTextFieldState.clearText()
                    _chatId.update { null }
                    _state.update {
                        it.copy(
                            chatUi = null,
                            messages = emptyList(),
                            bannerState = BannerState()
                        )
                    }
                }
                .onFailure { error ->
                    ChatDetailEvent.OnError(error.toUiText())
                }
        }
    }
}