package com.twugteam.admin.chat.presentation.chat_list

import com.twugteam.admin.chat.presentation.model.ChatUi
import com.twugteam.admin.core.designsystem.components.avatar.ChatParticipantUi
import com.twugteam.admin.core.presentation.util.UiText

data class ChatListState(
    val chats: List<ChatUi> = emptyList(),
    val error: UiText? = null,
    val localParticipant: ChatParticipantUi? = null,
    val isUserMenuOpen: Boolean = false,
    val showLogoutConfirmationDialog: Boolean = false,
    val selectedChatId: String? = null,
    val isLoadingChat: Boolean = false

)
