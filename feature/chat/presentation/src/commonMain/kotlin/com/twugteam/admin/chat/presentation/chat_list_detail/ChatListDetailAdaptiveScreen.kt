@file:OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalComposeUiApi::class)

package com.twugteam.admin.chat.presentation.chat_list_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twugteam.admin.chat.presentation.create_chat.CreateChatScreenRoot
import com.twugteam.admin.core.designsystem.theme.extended
import com.twugteam.admin.core.presentation.util.DialogSheetScopedViewModel
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ChatListDetailAdaptiveScreen(
    viewModel: ChatListDetailViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scaffoldDirective = createNoSpacingPaneScaffoldDirective()
    val scaffoldNavigator = rememberListDetailPaneScaffoldNavigator(
        scaffoldDirective = scaffoldDirective
    )
    val scope = rememberCoroutineScope()

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
        }
    }

    ListDetailPaneScaffold(
        directive = scaffoldDirective,
        value = scaffoldNavigator.scaffoldValue,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.extended.surfaceLower),
        listPane = {
            AnimatedPane {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(100) { chatIndex ->
                        Text(
                            text = "Chat $chatIndex",
                            color = MaterialTheme.colorScheme.extended.textPlaceholder,
                            modifier = Modifier
                                .clickable {
                                    viewModel.onAction(ChatListDetailAction.OnCreateChatClick)
                                    viewModel.onAction(ChatListDetailAction.OnChatClick(chatIndex.toString()))
                                    scope.launch {
                                        scaffoldNavigator.navigateTo(ListDetailPaneScaffoldRole.Detail)
                                    }
                                }
                                .padding(16.dp)
                        )
                    }
                }
            }
        },
        detailPane = {
            AnimatedPane {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    state.selectedChatId?.let { chatId ->
                        Text(
                            text = "Opened Chat was --> $chatId"
                        )
                    }
                }
            }
        }
    )

    DialogSheetScopedViewModel(
        visible = state.dialogState is DialogState.CreateChat
    ) {
        CreateChatScreenRoot(

        )
    }
}