package com.twugteam.admin.core.data.di

import com.twugteam.admin.core.data.auth.KtorAuthService
import com.twugteam.admin.core.data.logging.KermitLogger
import com.twugteam.admin.core.data.networking.HttpClientFactory
import com.twugteam.admin.core.domain.auth.AuthService
import com.twugteam.admin.core.domain.logging.ChripLogger
import io.ktor.client.HttpClient
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformCoreDataModule: Module

val coreDataModule = module {
    includes(platformCoreDataModule)
    single<ChripLogger> { KermitLogger }

    single<HttpClient> {
        HttpClientFactory(get()).create(get())
    }

    singleOf(::KtorAuthService) bind AuthService::class
}