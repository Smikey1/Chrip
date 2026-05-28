@file:OptIn(ExperimentalCoroutinesApi::class)

package com.twugteam.admin.chat.presentation.chat_detail

import androidx.compose.foundation.text.input.clearText
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.twugteam.admin.chat.domain.chat.ChatRealTimeService
import com.twugteam.admin.chat.domain.chat.ChatRepository
import com.twugteam.admin.chat.domain.message.MessageRepository
import com.twugteam.admin.chat.domain.models.NetworkConnectionState
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
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatDetailViewModel(
    private val chatRepository: ChatRepository,
    private val sessionStorage: SessionStorage,
    private val messageRepository: MessageRepository,
    private val webSocketService: ChatRealTimeService
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
            observeConnectionState()
            observeChatMessages()
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
            ChatDetailAction.OnChatMembersClick -> onDismissChatOptionClick()
            ChatDetailAction.OnChatOptionsClick -> onChatOptionClick()
            ChatDetailAction.OnDismissChatOptions -> onDismissChatOptionClick()
            else -> Unit
        }
    }

    private fun observeChatMessage() {
        _chatId
            .flatMapLatest { chatId ->
                if (chatId != null) {
                    messageRepository.getMessageForChat(chatId)
                } else emptyFlow()
            }
            .launchIn(viewModelScope)
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

    private fun observeChatMessages() {
        val currentMessage = state
            .map {
                it.messages
            }
            .distinctUntilChanged()

        val newMessagesList = _chatId
            .flatMapLatest { chatId ->
                if (chatId != null) {
                    messageRepository.getMessageForChat(chatId)
                } else emptyFlow()
            }
            .combine(sessionStorage.observeAuthInfo()) { messages, authInfo ->
                if (authInfo == null) {
                    return@combine messages
                }
                _state.update {
                    it.copy(
                        messages = messages.map { it.toUi(authInfo.user.id) }
                    )
                }
                messages
            }

        val isNearBottom = state.map { it.isNearBottomInMessageList }.distinctUntilChanged()

        combine(
            currentMessage,
            newMessagesList,
            isNearBottom
        ) { currentMessage, newMessages, isNearBottom ->
            val lastCurrentMessageId = currentMessage.lastOrNull()?.id
            val lastNewMessageId = newMessages.lastOrNull()?.message?.id
            if (lastNewMessageId != lastCurrentMessageId && isNearBottom) {
                eventChannel.send(ChatDetailEvent.OnNewMessage)
            }
        }.launchIn(viewModelScope)
    }

    private fun observeConnectionState() {
        webSocketService
            .connectionState
            .onEach { connectionState ->
                if (connectionState == NetworkConnectionState.CONNECTED) {
                    _chatId.value?.let { chatId ->
                        messageRepository.fetchMessage(chatId, null)
                    }
                }

                _state.update {
                    it.copy(
                        networkConnectionState = connectionState
                    )
                }
            }
            .launchIn(viewModelScope)
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