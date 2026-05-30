@file:OptIn(ExperimentalCoroutinesApi::class, ExperimentalUuidApi::class)

package com.twugteam.admin.chat.presentation.chat_detail

import androidx.compose.foundation.text.input.clearText
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.twugteam.admin.chat.domain.chat.ChatRealTimeService
import com.twugteam.admin.chat.domain.chat.ChatRepository
import com.twugteam.admin.chat.domain.message.MessageRepository
import com.twugteam.admin.chat.domain.models.Chat
import com.twugteam.admin.chat.domain.models.ChatMessage
import com.twugteam.admin.chat.domain.models.NetworkConnectionState
import com.twugteam.admin.chat.domain.models.OutgoingNewMessage
import com.twugteam.admin.chat.presentation.mappers.toUi
import com.twugteam.admin.chat.presentation.model.MessageUi
import com.twugteam.admin.core.domain.auth.SessionStorage
import com.twugteam.admin.core.domain.utils.DataErrorException
import com.twugteam.admin.core.domain.utils.Paginator
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
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class ChatDetailViewModel(
    private val chatRepository: ChatRepository,
    private val sessionStorage: SessionStorage,
    private val messageRepository: MessageRepository,
    private val webSocketService: ChatRealTimeService
) : ViewModel() {
    private var hasLoadedInitialData = false

    private var currentPaginator: Paginator<String?, ChatMessage>? = null

    private val eventChannel = Channel<ChatDetailEvent>()
    val events = eventChannel.receiveAsFlow()

    private val _chatId = MutableStateFlow<String?>(null)
    private val _chatInfoFlow = _chatId
        .onEach {chatId ->
            if(chatId != null){
                setupPaginatorForChat(chatId)
            } else {
                currentPaginator = null
            }

        }
        .flatMapLatest { chatId ->
            if (chatId != null) {
                chatRepository.getChatInfoById(chatId)
            } else emptyFlow()
        }

    private val canSendMessage = snapshotFlow {
        _state.value.messageTextFieldState.text.toString()
    }.map {
        it.isBlank()
    }.combine(webSocketService.connectionState) { isMessageBlank, connectionState ->
        !isMessageBlank && connectionState == NetworkConnectionState.CONNECTED
    }

    private fun observeCanSendMessage() {
        canSendMessage.onEach { canSend ->
            _state.update {
                it.copy(
                    canSendMessage = canSend
                )
            }
        }.launchIn(viewModelScope)
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
            messages = chatInfo.messages.map { it.toUi(authInfo.user.id) }
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
            observeCanSendMessage()
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
            ChatDetailAction.OnSendMessageClick -> sendMessage()
            is ChatDetailAction.OnRetryClick -> onRetryMessageClick(action.message)
            is ChatDetailAction.OnDeleteMessageClick -> onDeleteMessageClick(action.message)
            ChatDetailAction.OnDismissMessageMenu -> onDismissMessageMenu()
            is ChatDetailAction.OnMessageLongClick -> onMessageLongClick(action.message)
            else -> Unit
        }
    }

    private fun onMessageLongClick(message: MessageUi.LocalUserMessage) {
        _state.update {
            it.copy(
                messageWithMenuOpen = message
            )
        }
    }

    private fun onDismissMessageMenu() {
        _state.update {
            it.copy(
                messageWithMenuOpen = null
            )
        }
    }

    private fun onDeleteMessageClick(message: MessageUi.LocalUserMessage) {
        viewModelScope.launch {
            messageRepository
                .deleteMessage(message.id)
                .onFailure { error ->
                    eventChannel.send(ChatDetailEvent.OnError(error.toUiText()))
                }
        }
    }

    private fun onRetryMessageClick(message: MessageUi.LocalUserMessage) {
        viewModelScope.launch {
            messageRepository
                .retryMessage(message.id)
                .onFailure { error ->
                    eventChannel.send(ChatDetailEvent.OnError(error.toUiText()))
                }
        }
    }

    private fun sendMessage() {
        val currentChatId = _chatId.value
        val messageContent = state.value.messageTextFieldState.text.toString().trim()
        if (currentChatId == null || messageContent.isBlank()) {
            return
        }
        viewModelScope.launch {
            val message = OutgoingNewMessage(
                chatId = currentChatId,
                messageId = Uuid.random().toString(),
                content = messageContent
            )
            messageRepository
                .sendMessage(message)
                .onSuccess {
                    state.value.messageTextFieldState.clearText()
                }
                .onFailure { error ->
                    state.value.messageTextFieldState.clearText()
                    eventChannel.send(ChatDetailEvent.OnError(error.toUiText()))
                }
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

    private fun setupPaginatorForChat(chatId: String){
        currentPaginator = Paginator(
            initialKey = null,
            onLoadUpdated = { isLoading ->
                _state.update {
                    it.copy(
                        isPaginationLoading = isLoading
                    )
                }

            },
            onRequest = {beforeTimestamp ->
                messageRepository.fetchMessage(chatId,beforeTimestamp)
            },
            getNextKey = { messages ->
                messages.minOfOrNull { it.createdAt }.toString()
            },
            onError = {throwable ->
                if(throwable is DataErrorException){
                    eventChannel.send(ChatDetailEvent.OnError(throwable.error.toUiText()))
                }
            },
            onSuccess = { messages, _->
                _state.update {
                    it.copy(
                        isPaginationEndReached = messages.isEmpty()
                    )
                }
            }
        )
        _state.update {
            it.copy(
                isPaginationEndReached = false,
                isPaginationLoading = false
            )
        }
        viewModelScope.launch {
            currentPaginator?.loadNextItem()
        }
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