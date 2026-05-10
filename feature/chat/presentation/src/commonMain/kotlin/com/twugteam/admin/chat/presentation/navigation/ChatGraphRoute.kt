package com.twugteam.admin.chat.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface ChatGraphRoute {
    @Serializable
    data object ChatGraph: ChatGraphRoute

}