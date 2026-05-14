package com.twugteam.admin.chat.data.di

import com.twugteam.admin.chat.data.chat.KtorChatParticipantService
import com.twugteam.admin.chat.domain.chat.ChatParticipantService
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val chatDataModule = module {
    singleOf(::KtorChatParticipantService) bind ChatParticipantService::class
}