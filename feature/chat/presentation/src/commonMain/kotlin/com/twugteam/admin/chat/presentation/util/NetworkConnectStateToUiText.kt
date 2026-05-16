package com.twugteam.admin.chat.presentation.util

import com.twugteam.admin.chat.domain.models.NetworkConnectionState
import com.twugteam.admin.core.presentation.util.UiText
import com.twugteam.admin.feature.chat.presentation.Res
import com.twugteam.admin.feature.chat.presentation.network_error
import com.twugteam.admin.feature.chat.presentation.offline
import com.twugteam.admin.feature.chat.presentation.online
import com.twugteam.admin.feature.chat.presentation.reconnecting
import com.twugteam.admin.feature.chat.presentation.unknown_error

fun NetworkConnectionState.toUiText(): UiText {
    val stringRes = when (this){
        NetworkConnectionState.DISCONNECTED -> Res.string.offline
        NetworkConnectionState.CONNECTED -> Res.string.online
        NetworkConnectionState.CONNECTING -> Res.string.reconnecting
        NetworkConnectionState.ERROR_NETWORK -> Res.string.network_error
        NetworkConnectionState.ERROR_UNKNOWN -> Res.string.unknown_error
    }
    return UiText.Resource(stringRes)
}