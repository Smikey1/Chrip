package com.twugteam.admin.chat.presentation.chat_list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twugteam.admin.chat.presentation.chat_list.components.ChatListHeader
import com.twugteam.admin.chat.presentation.chat_list.components.ChatListItemUi
import com.twugteam.admin.chat.presentation.components.EmptySection
import com.twugteam.admin.core.designsystem.components.buttons.ChripFloatingActionButton
import com.twugteam.admin.core.designsystem.components.dialogs.DestructiveConfirmationDialog
import com.twugteam.admin.core.designsystem.components.divider.ChirpHorizontalDivider
import com.twugteam.admin.core.designsystem.theme.ChirpTheme
import com.twugteam.admin.core.designsystem.theme.extended
import com.twugteam.admin.feature.chat.presentation.Res
import com.twugteam.admin.feature.chat.presentation.cancel
import com.twugteam.admin.feature.chat.presentation.create_chat
import com.twugteam.admin.feature.chat.presentation.do_you_want_to_logout
import com.twugteam.admin.feature.chat.presentation.do_you_want_to_logout_desc
import com.twugteam.admin.feature.chat.presentation.logout
import com.twugteam.admin.feature.chat.presentation.no_chats
import com.twugteam.admin.feature.chat.presentation.no_chats_subtitle
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ChatListScreenRoot(
    selectedChatId: String?,
    onChatClick: (String?) -> Unit,
    onConfirmLogoutClick: () -> Unit,
    onCreateChatClick: () -> Unit,
    onProfileSettingClick: () -> Unit,
    viewModel: ChatListViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(selectedChatId){
        viewModel.onAction(ChatListAction.OnSelectClick(selectedChatId))
    }

    ChatListScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is ChatListAction.OnSelectClick -> onChatClick(action.chatId)
                ChatListAction.OnConfirmLogoutClick -> onConfirmLogoutClick()
                ChatListAction.OnCreateChatClick -> onCreateChatClick()
                ChatListAction.OnProfileSettingCLick -> onProfileSettingClick()
                else -> Unit
            }
            viewModel.onAction(action)
        },
        snackbarHostState = snackbarHostState
    )
}

@Composable
private fun ChatListScreen(
    state: ChatListState,
    onAction: (ChatListAction) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.extended.surfaceLower,
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        floatingActionButton = {
            ChripFloatingActionButton(
                onClick = {
                    onAction(ChatListAction.OnCreateChatClick)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(Res.string.create_chat)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ChatListHeader(
                localParticipant = state.localParticipant,
                isUserMenuOpen = state.isUserMenuOpen,
                modifier = Modifier
                    .fillMaxWidth(),
                onUserAvatarClick = {
                    onAction(ChatListAction.OnUserAvatarClick)
                },
                onProfileSettingClick = {
                    onAction(ChatListAction.OnProfileSettingCLick)
                },
                onLogoutClick = {
                    onAction(ChatListAction.OnConfirmLogoutClick)
                },
                onDismissClick = {
                    onAction(ChatListAction.OnDismissUserMenu)
                },
            )
            when {
                state.isLoadingChat -> CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )

                state.chats.isEmpty() -> {
                    EmptySection(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        title = stringResource(Res.string.no_chats),
                        description = stringResource(Res.string.no_chats_subtitle)
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        items(
                            items = state.chats,
                            key = { it.chatId }
                        ) { chatUi ->
                            ChatListItemUi(
                                chat = chatUi,
                                isSelected = chatUi.chatId == state.selectedChatId,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onAction(ChatListAction.OnSelectClick(chatUi.chatId))
                                    }
                            )
                            ChirpHorizontalDivider()
                        }
                    }
                }
            }
        }
    }
    if (state.showLogoutConfirmationDialog) {
        DestructiveConfirmationDialog(
            title = stringResource(Res.string.do_you_want_to_logout),
            description = stringResource(Res.string.do_you_want_to_logout_desc),
            onDismiss = {
                onAction(ChatListAction.OnDismissLogoutDialog)
            },
            onConfirmClick = {
                onAction(ChatListAction.OnConfirmLogoutClick)
            },
            onCancelClick = {
                onAction(ChatListAction.OnDismissLogoutDialog)
            },
            confirmButtonText = stringResource(Res.string.logout),
            cancelButtonText = stringResource(Res.string.cancel)
        )
    }
}

@Preview
@Composable
private fun ChatListScreenPreview() {
    ChirpTheme {
        ChatListScreen(
            state = ChatListState(

            ),
            onAction = {

            },
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}