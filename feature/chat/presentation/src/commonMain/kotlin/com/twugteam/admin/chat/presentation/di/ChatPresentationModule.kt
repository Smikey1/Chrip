package com.twugteam.admin.chat.presentation.di

import com.twugteam.admin.chat.presentation.chat_list.ChatListViewModel
import com.twugteam.admin.chat.presentation.chat_list_detail.ChatListDetailViewModel
import com.twugteam.admin.chat.presentation.create_chat.CreateChatViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val chatPresentationModule = module {
    viewModelOf(::ChatListDetailViewModel)
    viewModelOf(::CreateChatViewModel)
    viewModelOf(::ChatListViewModel)
}