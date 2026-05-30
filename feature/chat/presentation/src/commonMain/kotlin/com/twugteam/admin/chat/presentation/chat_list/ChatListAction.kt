package com.twugteam.admin.chat.presentation.chat_list

import com.twugteam.admin.chat.presentation.model.ChatUi

sealed interface ChatListAction {
    data object OnProfileSettingCLick : ChatListAction
    data object OnUserAvatarClick : ChatListAction
    data object OnDismissUserMenu : ChatListAction
    data object OnConfirmLogoutClick : ChatListAction
    data object OnDismissLogoutDialog : ChatListAction
    data object OnCreateChatClick : ChatListAction
    data class OnSelectClick(val chatId: String?) : ChatListAction
}