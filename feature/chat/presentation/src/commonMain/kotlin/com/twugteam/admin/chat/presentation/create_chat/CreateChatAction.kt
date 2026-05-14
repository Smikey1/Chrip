package com.twugteam.admin.chat.presentation.create_chat

sealed interface CreateChatAction {
    data object OnCreateChatClick: CreateChatAction
    data object OnAddParticipantClick: CreateChatAction
    data object OnDismissDialog: CreateChatAction
}