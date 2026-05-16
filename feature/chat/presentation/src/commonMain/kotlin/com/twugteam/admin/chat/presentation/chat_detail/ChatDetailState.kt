package com.twugteam.admin.chat.presentation.chat_detail

import androidx.compose.foundation.text.input.TextFieldState
import com.twugteam.admin.chat.domain.models.NetworkConnectionState
import com.twugteam.admin.chat.presentation.model.ChatUi
import com.twugteam.admin.chat.presentation.model.MessageUi
import com.twugteam.admin.core.presentation.util.UiText

data class ChatDetailState(
    val messageTextFieldState: TextFieldState = TextFieldState(),
    val chatUi: ChatUi? = null,
    val isInitialMessagesLoading: Boolean = false,
    val messages: List<MessageUi> = emptyList(),
    val error: UiText? = null,
    val isChatOptionMenuOpen: Boolean = false,
    val canSendMessage: Boolean = false,
    val isPaginationLoading: Boolean = false,
    val paginationError: UiText? = null,
    val isPaginationEndReached: Boolean = false,
    val bannerState: BannerState = BannerState(),
    val isNearBottomInMessageList: Boolean = false,
    val networkConnectionState: NetworkConnectionState = NetworkConnectionState.DISCONNECTED
)

data class BannerState(
    val formattedDate: UiText? = null,
    val isVisible: Boolean = false
)