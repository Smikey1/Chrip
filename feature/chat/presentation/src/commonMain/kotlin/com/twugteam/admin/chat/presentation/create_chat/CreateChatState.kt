package com.twugteam.admin.chat.presentation.create_chat

import androidx.compose.foundation.text.input.TextFieldState
import com.twugteam.admin.core.designsystem.components.avatar.ChatParticipantUi
import com.twugteam.admin.core.presentation.util.UiText

data class CreateChatState(
    val searchQueryTextState: TextFieldState = TextFieldState(),
    val selectedChatParticipants: List<ChatParticipantUi> = emptyList(),
    val isAddingParticipant: Boolean = false,
    val isCreatingChat: Boolean = false,
    val isLoadingParticipant: Boolean = false,
    val canAddParticipant: Boolean = false,
    val currentSearchResult: ChatParticipantUi? = null,
    val searchError: UiText? = null
)