@file:OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalComposeUiApi::class)

package com.twugteam.admin.chat.presentation.chat_list_detail

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twugteam.admin.chat.presentation.chat_detail.ChatDetailScreenRoot
import com.twugteam.admin.chat.presentation.chat_list.ChatListScreenRoot
import com.twugteam.admin.chat.presentation.create_chat.CreateChatScreenRoot
import com.twugteam.admin.chat.presentation.manage_chat.ManageChatScreenRoot
import com.twugteam.admin.core.designsystem.theme.extended
import com.twugteam.admin.core.presentation.util.DialogSheetScopedViewModel
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ChatListDetailAdaptiveScreen(
    onConfirmLogoutClick: () -> Unit,
    viewModel: ChatListDetailViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scaffoldDirective = createNoSpacingPaneScaffoldDirective()
    val scaffoldNavigator = rememberListDetailPaneScaffoldNavigator(
        scaffoldDirective = scaffoldDirective
    )
    val scope = rememberCoroutineScope()

    val detailPane = scaffoldNavigator.scaffoldValue[ListDetailPaneScaffoldRole.Detail]
    LaunchedEffect(detailPane, state.selectedChatId) {
        if (detailPane == PaneAdaptedValue.Hidden && state.selectedChatId != null) {
            viewModel.onAction(ChatListDetailAction.OnSelectClick(null))
        }
    }

//    val backEventState = rememberNavigationEventState(
//        currentInfo = NavigationEventInfo.None
//    )
//
//    NavigationBackHandler(
//        state = backEventState,
//        onBackCompleted = {
//            scope.launch {
//                scaffoldNavigator.navigateBack()
//            }
//        },
//        onBackCancelled = {
//            // Optional: handle when a swipe gesture is cancelled
//        }
//    )
    // DEPRECATED
    BackHandler(enabled = scaffoldNavigator.canNavigateBack()) {
        scope.launch {
            scaffoldNavigator.navigateBack()
            viewModel.onAction(ChatListDetailAction.OnSelectClick(null))
        }
    }

    ListDetailPaneScaffold(
        directive = scaffoldDirective,
        value = scaffoldNavigator.scaffoldValue,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.extended.surfaceLower),
        listPane = {
            AnimatedPane {
                ChatListScreenRoot(
                    selectedChatId = state.selectedChatId,
                    onChatClick = { chatId ->
                        viewModel.onAction(ChatListDetailAction.OnSelectClick(chatId))

                        scope.launch {
                            scaffoldNavigator.navigateTo(ListDetailPaneScaffoldRole.Detail)
                        }
                    },
                    onCreateChatClick = {
                        viewModel.onAction(ChatListDetailAction.OnCreateChatClick)
                    },
                    onConfirmLogoutClick = onConfirmLogoutClick,
                    onProfileSettingClick = {
                        viewModel.onAction(ChatListDetailAction.OnProfileSettingClick)
                    }
                )
            }
        },
        detailPane = {
            AnimatedPane {
                val listPane = scaffoldNavigator.scaffoldValue[ListDetailPaneScaffoldRole.List]
                ChatDetailScreenRoot(
                    chatId = state.selectedChatId,
                    isDetailScreenPresent = detailPane == PaneAdaptedValue.Expanded && listPane == PaneAdaptedValue.Expanded,
                    onChatMemberClick = {
                        viewModel.onAction(ChatListDetailAction.OnManageChatClick)
                    },
                    onBack = {
                        scope.launch {
                            if (scaffoldNavigator.canNavigateBack()) {
                                scaffoldNavigator.navigateBack()
                            }
                        }
                    }
                )
            }
        }
    )

    DialogSheetScopedViewModel(
        visible = state.dialogState is DialogState.CreateChat
    ) {
        CreateChatScreenRoot(
            onChatCreated = { chat ->
                viewModel.onAction(ChatListDetailAction.OnDismissClick)
                viewModel.onAction(ChatListDetailAction.OnSelectClick(chat.id))
                scope.launch {
                    scaffoldNavigator.navigateTo(ListDetailPaneScaffoldRole.Detail)
                }
            },
            onDismiss = {
                viewModel.onAction(ChatListDetailAction.OnDismissClick)
            }
        )
    }

    DialogSheetScopedViewModel(
        visible = state.dialogState is DialogState.ManageChat
    ) {
        ManageChatScreenRoot(
            chatId = state.selectedChatId,
            onMemberAdded = {
                viewModel.onAction(ChatListDetailAction.OnDismissClick)
            },
            onDismiss = {
                viewModel.onAction(ChatListDetailAction.OnDismissClick)
            }
        )
    }
}