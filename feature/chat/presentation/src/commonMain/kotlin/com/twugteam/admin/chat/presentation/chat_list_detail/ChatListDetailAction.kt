package com.twugteam.admin.chat.presentation.chat_list_detail

sealed interface ChatListDetailAction {
    data class OnChatClick(val chatId: String?): ChatListDetailAction
    data object OnProfileSettingClick: ChatListDetailAction
    data object OnCreateChatClick: ChatListDetailAction
    data object OnManageChatClick: ChatListDetailAction
    data object OnDismissClick: ChatListDetailAction

}