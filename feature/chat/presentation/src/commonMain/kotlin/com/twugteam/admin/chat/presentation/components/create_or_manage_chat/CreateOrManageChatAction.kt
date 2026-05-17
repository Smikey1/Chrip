package com.twugteam.admin.chat.presentation.components.create_or_manage_chat

sealed interface CreateOrManageChatAction {
    data object OnCreateOrManageChatClick : CreateOrManageChatAction
    data object OnAddParticipantClick : CreateOrManageChatAction
    data object OnDismissDialog : CreateOrManageChatAction

    sealed interface ManageChatParticipants : CreateOrManageChatAction {
        data class OnSelectChat(val chatId: String?) : ManageChatParticipants
    }
}