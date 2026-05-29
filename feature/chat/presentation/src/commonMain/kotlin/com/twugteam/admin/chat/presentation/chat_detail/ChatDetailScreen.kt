@file:OptIn(ExperimentalComposeUiApi::class)

package com.twugteam.admin.chat.presentation.chat_detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twugteam.admin.chat.domain.models.ChatMessageDeliveryStatus
import com.twugteam.admin.chat.presentation.chat_detail.component.ChatDetailHeader
import com.twugteam.admin.chat.presentation.chat_detail.component.InputMessageBox
import com.twugteam.admin.chat.presentation.chat_detail.component.MessageList
import com.twugteam.admin.chat.presentation.components.ChatHeader
import com.twugteam.admin.chat.presentation.components.EmptySection
import com.twugteam.admin.chat.presentation.model.MessageUi
import com.twugteam.admin.core.designsystem.components.avatar.ChatParticipantUi
import com.twugteam.admin.core.designsystem.theme.ChirpTheme
import com.twugteam.admin.core.designsystem.theme.extended
import com.twugteam.admin.core.presentation.util.ObserveAsEvents
import com.twugteam.admin.core.presentation.util.UiText
import com.twugteam.admin.core.presentation.util.clearFocusOnTapOutside
import com.twugteam.admin.core.presentation.util.getCurrentDeviceConfiguration
import com.twugteam.admin.feature.chat.presentation.Res
import com.twugteam.admin.feature.chat.presentation.no_chat_selected
import com.twugteam.admin.feature.chat.presentation.select_a_chat
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ChatDetailScreenRoot(
    chatId: String?,
    isDetailScreenPresent: Boolean,
    onBack: () -> Unit,
    onChatMemberClick: () -> Unit,
    viewModel: ChatDetailViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val snackbarState = remember { SnackbarHostState() }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is ChatDetailEvent.OnChatLeft -> onBack()
            is ChatDetailEvent.OnError -> snackbarState.showSnackbar(event.error.asStringAsync())
            is ChatDetailEvent.OnNewMessage -> {

            }
        }
    }

    LaunchedEffect(chatId) {
        viewModel.onAction(ChatDetailAction.OnSelectChat(chatId))
    }

    val scope = rememberCoroutineScope()

    BackHandler(
        enabled = !isDetailScreenPresent
    ) {
        scope.launch {
            // Add artificial delay to prevent details back animation from showing
            // an unselected chat at the moment we go back
            delay(300)
            viewModel.onAction(ChatDetailAction.OnSelectChat(null))
        }
        onBack()
    }

    ChatDetailScreenRootScreen(
        isDetailScreenPresent = isDetailScreenPresent,
        state = state,
        onAction = { action ->
            when (action) {
                is ChatDetailAction.OnBackClick -> onBack()
                is ChatDetailAction.OnChatMembersClick -> onChatMemberClick()
                else -> Unit
            }
            viewModel.onAction(action)
        },
        snackbarHostState = snackbarState
    )
}

@Composable
private fun ChatDetailScreenRootScreen(
    state: ChatDetailState,
    onAction: (ChatDetailAction) -> Unit,
    snackbarHostState: SnackbarHostState,
    isDetailScreenPresent: Boolean
) {
    val currentDeviceConfiguration = getCurrentDeviceConfiguration()
    val messageListState = rememberLazyListState()

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = if (!currentDeviceConfiguration.isWideScreen) {
            MaterialTheme.colorScheme.surface
        } else MaterialTheme.colorScheme.extended.surfaceLower,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .clearFocusOnTapOutside()
                .padding(innerPadding)
                .then(
                    if (currentDeviceConfiguration.isWideScreen) {
                        Modifier.padding(horizontal = 8.dp)
                    } else Modifier
                )
        ) {
            Column(
                modifier = Modifier,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                DynamicRoundedCornerColumn(
                    isCornerRounded = currentDeviceConfiguration.isWideScreen,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    if (state.chatUi == null) {
                        EmptySection(
                            title = stringResource(Res.string.no_chat_selected),
                            description = stringResource(Res.string.select_a_chat),
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        ChatHeader {
                            ChatDetailHeader(
                                chatUi = state.chatUi,
                                isDetailScreenPresent = isDetailScreenPresent,
                                isChatOptionMenuOpen = state.isChatOptionMenuOpen,
                                modifier = Modifier.fillMaxWidth(),
                                onChatOptionClick = {
                                    onAction(ChatDetailAction.OnChatOptionsClick)
                                },
                                onDismissChatOption = {
                                    onAction(ChatDetailAction.OnDismissChatOptions)
                                },
                                onBackClick = {
                                    onAction(ChatDetailAction.OnBackClick)
                                },
                                onLeaveChatClick = {
                                    onAction(ChatDetailAction.OnLeaveChatClick)
                                },
                                onManageClick = {
                                    onAction(ChatDetailAction.OnChatMembersClick)
                                }
                            )
                        }
                        MessageList(
                            messages = state.messages,
                            listState = messageListState,
                            messageWithMenuOpen = state.messageWithMenuOpen,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            onDeleteMessageClick = {
                                onAction(ChatDetailAction.OnDeleteMessageClick(it))
                            },
                            onMessageRetryClick = {
                                onAction(ChatDetailAction.OnRetryClick(it))
                            },
                            onDismissMessageMenu = {
                                onAction(ChatDetailAction.OnDismissMessageMenu)
                            },
                            onMessageLongClick = {
                                onAction(ChatDetailAction.OnMessageLongClick(it))
                            },
                        )
                        AnimatedVisibility(
                            visible = !currentDeviceConfiguration.isWideScreen
                        ) {
                            InputMessageBox(
                                messageTextFieldState = state.messageTextFieldState,
                                connectionState = state.networkConnectionState,
                                isSendButtonEnabled = state.canSendMessage,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        horizontal = 16.dp,
                                        vertical = 8.dp
                                    ),
                                onSendClick = {
                                    onAction(ChatDetailAction.OnSendMessageClick)
                                }
                            )
                        }
                    }
                }
                if (currentDeviceConfiguration.isWideScreen) {
                    Spacer(modifier = Modifier.height(8.dp))
                    AnimatedVisibility(
                        visible = currentDeviceConfiguration.isWideScreen && state.chatUi != null
                    ) {
                        DynamicRoundedCornerColumn(
                            isCornerRounded = currentDeviceConfiguration.isWideScreen
                        ) {
                            InputMessageBox(
                                messageTextFieldState = state.messageTextFieldState,
                                connectionState = state.networkConnectionState,
                                isSendButtonEnabled = state.canSendMessage,
                                modifier = Modifier.fillMaxWidth().padding(8.dp),
                                onSendClick = {
                                    onAction(ChatDetailAction.OnSendMessageClick)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DynamicRoundedCornerColumn(
    isCornerRounded: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .shadow(
                elevation = if (isCornerRounded) 8.dp else 0.dp,
                shape = if (isCornerRounded) RoundedCornerShape(24.dp) else RectangleShape,
                spotColor = Color.Black.copy(alpha = 0.2f)
            )
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = if (isCornerRounded) RoundedCornerShape(24.dp) else RectangleShape,
            )
    ) {
        content()
    }
}

@Preview
@Composable
private fun ChatDetailScreenRootScreenPreview() {
    ChirpTheme(
        isDarkTheme = true
    ) {
        ChatDetailScreenRootScreen(
            state = ChatDetailState(
                messages = (1..20).map {
                    if (it % 2 == 0) {
                        MessageUi.LocalUserMessage(
                            id = "121",
                            content = "Hey",
                            formattedSentTime = UiText.DynamicString("now"),
                            deliveryStatus = ChatMessageDeliveryStatus.FAILED
                        )
                    } else {
                        MessageUi.OtherUserMessage(
                            id = "121",
                            content = "Hey",
                            formattedSentTime = UiText.DynamicString("now"),
                            sender = ChatParticipantUi(
                                userId = "343",
                                username = "Kiran"
                            )
                        )
                    }
                },
            ),
            onAction = {},
            isDetailScreenPresent = true,
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}