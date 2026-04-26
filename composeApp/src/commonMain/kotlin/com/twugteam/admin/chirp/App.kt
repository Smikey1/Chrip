package com.twugteam.admin.chirp

import androidx.compose.runtime.Composable
import com.twugteam.admin.auth.presentation.register.RegisterScreenRoot
import com.twugteam.admin.core.designsystem.theme.ChirpTheme

@Composable
fun App(
) {
    ChirpTheme {
        RegisterScreenRoot()
    }
}