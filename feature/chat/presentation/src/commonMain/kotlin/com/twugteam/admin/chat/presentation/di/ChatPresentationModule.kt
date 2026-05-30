package com.twugteam.admin.chat.presentation.di

import com.twugteam.admin.chat.presentation.chat_detail.ChatDetailViewModel
import com.twugteam.admin.chat.presentation.chat_list.ChatListViewModel
import com.twugteam.admin.chat.presentation.chat_list_detail.ChatListDetailViewModel
import com.twugteam.admin.chat.presentation.create_chat.CreateChatViewModel
import com.twugteam.admin.chat.presentation.manage_chat.ManageChatViewModel
import com.twugteam.admin.chat.presentation.profile.ProfileViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val chatPresentationModule = module {
    viewModelOf(::ChatListDetailViewModel)
    viewModelOf(::CreateChatViewModel)
    viewModelOf(::ManageChatViewModel)
    viewModelOf(::ChatListViewModel)
    viewModelOf(::ChatDetailViewModel)
    viewModelOf(::ProfileViewModel)
}