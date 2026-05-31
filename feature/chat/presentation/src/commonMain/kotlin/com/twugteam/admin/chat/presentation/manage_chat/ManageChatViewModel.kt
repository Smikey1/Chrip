@file:OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)

package com.twugteam.admin.chat.presentation.manage_chat

import androidx.compose.foundation.text.input.clearText
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.twugteam.admin.chat.domain.participant.ChatParticipantService
import com.twugteam.admin.chat.domain.chat.ChatRepository
import com.twugteam.admin.chat.presentation.components.create_or_manage_chat.CreateOrManageChatAction
import com.twugteam.admin.chat.presentation.components.create_or_manage_chat.CreateOrManageChatState
import com.twugteam.admin.chat.presentation.mappers.toUi
import com.twugteam.admin.core.domain.utils.DataError
import com.twugteam.admin.core.domain.utils.onFailure
import com.twugteam.admin.core.domain.utils.onSuccess
import com.twugteam.admin.core.presentation.util.UiText
import com.twugteam.admin.core.presentation.util.toUiText
import com.twugteam.admin.feature.chat.presentation.Res
import com.twugteam.admin.feature.chat.presentation.error_participant_not_found
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class ManageChatViewModel(
    private val chatParticipantService: ChatParticipantService,
    private val chatRepository: ChatRepository
) : ViewModel() {

    var hasLoadedInitialData: Boolean = false

    private val _chatId = MutableStateFlow<String?>(null)

    private val eventChannel = Channel<ManageChatEvent>()
    val events = eventChannel.receiveAsFlow()

    private val _state = MutableStateFlow(CreateOrManageChatState())

    private val searchFlow = snapshotFlow { _state.value.searchQueryTextState.text.toString() }
        .debounce { 1.5.seconds }
        .onEach { search ->
            performSearch(search)
        }

    val state = _chatId
        .flatMapLatest { chatId ->
            if (chatId != null) {
                chatRepository.getActiveParticipantByChatId(chatId)
            } else emptyFlow()
        }
        .combine(_state) { participant, currentState ->
            currentState.copy(
                existingChatParticipants = participant.map { it.toUi() }
            )
        }.onStart {
            if (!hasLoadedInitialData) {
                searchFlow.launchIn(viewModelScope)
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = CreateOrManageChatState()
        )

    fun onAction(action: CreateOrManageChatAction) {
        when (action) {
            CreateOrManageChatAction.OnAddParticipantClick -> addParticipantToExistingList()
            CreateOrManageChatAction.OnCreateOrManageChatClick -> addParticipantToChat()
            is CreateOrManageChatAction.ManageChatParticipants.OnSelectChat -> {
                _chatId.update { action.chatId }
            }

            else -> Unit
        }
    }

    private fun addParticipantToExistingList() {
        state.value.currentSearchResult?.let { participantFromSearch ->
            val isAlreadySelected = state.value.selectedChatParticipants.any {
                it.userId == participantFromSearch.userId
            }
            val isAlreadyPartOfChat = state.value.existingChatParticipants.any {
                it.userId == participantFromSearch.userId
            }

            val updatedParticipants = if (isAlreadyPartOfChat || isAlreadySelected) {
                state.value.selectedChatParticipants
            } else state.value.selectedChatParticipants + participantFromSearch

            _state.value.searchQueryTextState.clearText()
            _state.update {
                it.copy(
                    selectedChatParticipants = updatedParticipants,
                    canAddParticipant = false,
                    currentSearchResult = null
                )
            }
        }
    }

    private fun addParticipantToChat() {
        if (state.value.selectedChatParticipants.isEmpty()) {
            return
        }
        val chatId = _chatId.value ?: return

        val selectedParticipants = state.value.selectedChatParticipants
        val selectedUserIds = selectedParticipants.map { it.userId }

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isSubmitting = true,
                    canAddParticipant = false
                )
            }
            chatRepository
                .addParticipantsToChat(chatId, selectedUserIds)
                .onSuccess {
                    _state.update {
                        it.copy(
                            isSubmitting = false
                        )
                    }
                    eventChannel.send(ManageChatEvent.OnMemberAdded)
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isSubmitting = false,
                            submitError = error.toUiText(),
                            canAddParticipant = it.currentSearchResult != null && !it.isSearching
                        )
                    }
                }
        }
    }

    private fun performSearch(searchQuery: String) {
        if (searchQuery.isBlank()) {
            _state.update {
                it.copy(
                    currentSearchResult = null,
                    canAddParticipant = false,
                    searchError = null
                )
            }
            return
        }

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isSearching = true,
                    canAddParticipant = false
                )
            }
            chatParticipantService
                .searchParticipant(searchQuery)
                .onSuccess { chatParticipant ->
                    _state.update {
                        it.copy(
                            isSearching = false,
                            searchError = null,
                            currentSearchResult = chatParticipant.toUi(),
                            canAddParticipant = true
                        )
                    }
                }
                .onFailure { error ->
                    val errorMessage = when (error) {
                        DataError.Remote.NOT_FOUND -> UiText.Resource(Res.string.error_participant_not_found)
                        else -> error.toUiText()
                    }
                    _state.update {
                        it.copy(
                            searchError = errorMessage,
                            canAddParticipant = false,
                            isSearching = false,
                            currentSearchResult = null
                        )
                    }
                }
        }

    }
}