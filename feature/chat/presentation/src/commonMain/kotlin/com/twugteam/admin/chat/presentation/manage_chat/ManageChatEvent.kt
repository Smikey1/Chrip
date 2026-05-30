package com.twugteam.admin.chat.presentation.manage_chat

sealed interface ManageChatEvent {
    data object OnMemberAdded : ManageChatEvent
}