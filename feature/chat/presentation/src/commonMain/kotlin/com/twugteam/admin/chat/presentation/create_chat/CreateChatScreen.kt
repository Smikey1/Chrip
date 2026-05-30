package com.twugteam.admin.chat.presentation.create_chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twugteam.admin.chat.domain.models.Chat
import com.twugteam.admin.chat.presentation.components.create_or_manage_chat.CreateOrManageChatAction
import com.twugteam.admin.chat.presentation.components.create_or_manage_chat.CreateOrManageChatScreen
import com.twugteam.admin.chat.presentation.components.create_or_manage_chat.CreateOrManageChatState
import com.twugteam.admin.core.designsystem.components.dialogs.ChirpAdaptiveDialogSheetLayout
import com.twugteam.admin.core.designsystem.theme.ChirpTheme
import com.twugteam.admin.core.presentation.util.ObserveAsEvents
import com.twugteam.admin.feature.chat.presentation.Res
import com.twugteam.admin.feature.chat.presentation.create_chat
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CreateChatScreenRoot(
    onDismiss: () -> Unit,
    onChatCreated: (Chat) -> Unit,
    viewModel: CreateChatViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is CreateChatEvent.OnChatCreated -> onChatCreated(event.chat)
        }
    }

    ChirpAdaptiveDialogSheetLayout(
        onDismiss = onDismiss
    ) {
        CreateOrManageChatScreen(
            headerText = stringResource(Res.string.create_chat),
            primaryButtonText = stringResource(Res.string.create_chat),
            state = state,
            onAction = { action ->
                when (action) {
                    CreateOrManageChatAction.OnDismissDialog -> onDismiss()
                    else -> Unit
                }
                viewModel.onAction(action)
            }
        )
    }
}

@Preview
@Composable
private fun CreateChatScreenPreview() {
    ChirpTheme {
        CreateOrManageChatScreen(
            state = CreateOrManageChatState(
                selectedChatParticipants = emptyList()
            ),
            onAction = {},
            headerText = "Create Chat",
            primaryButtonText = "Create Chat"
        )
    }
}