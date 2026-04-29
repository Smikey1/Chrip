package com.twugteam.admin.auth.presentation.di

import com.twugteam.admin.auth.presentation.email_verification.EmailVerificationViewModel
import com.twugteam.admin.auth.presentation.login.LoginViewModel
import com.twugteam.admin.auth.presentation.register.RegisterViewModel
import com.twugteam.admin.auth.presentation.register_success.RegisterSuccessViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val authPresentationModule = module {
    viewModelOf(::RegisterViewModel)
    viewModelOf(::RegisterSuccessViewModel)
    viewModelOf(::EmailVerificationViewModel)
    viewModelOf(::LoginViewModel)
}