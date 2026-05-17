package com.twugteam.admin.chat.presentation.components.create_or_manage_chat

import androidx.compose.foundation.text.input.TextFieldState
import com.twugteam.admin.core.designsystem.components.avatar.ChatParticipantUi
import com.twugteam.admin.core.presentation.util.UiText

data class CreateOrManageChatState(
    val searchQueryTextState: TextFieldState = TextFieldState(),
    val existingChatParticipants: List<ChatParticipantUi> = emptyList(),
    val selectedChatParticipants: List<ChatParticipantUi> = emptyList(),
    val isSearching: Boolean = false,
    val isSubmitting: Boolean = false,
    val canAddParticipant: Boolean = false,
    val currentSearchResult: ChatParticipantUi? = null,
    val searchError: UiText? = null,
    val submitError: UiText? = null
)