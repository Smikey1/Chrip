package com.twugteam.admin.core.presentation.di

import com.twugteam.admin.core.presentation.util.ScopedStoreViewModelRegistry
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val corePresentationModule = module {
    viewModelOf(::ScopedStoreViewModelRegistry)
}