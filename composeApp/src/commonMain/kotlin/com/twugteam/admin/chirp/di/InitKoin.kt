package com.twugteam.admin.chirp.di

import com.twugteam.admin.auth.presentation.di.authPresentationModule
import com.twugteam.admin.core.data.di.coreDataModule
import com.twugteam.admin.core.presentation.di.corePresentationModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
         config?.invoke(this)
        modules(
            appModule,
            corePresentationModule,
            coreDataModule,
            authPresentationModule
        )
    }
}