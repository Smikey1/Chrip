package com.twugteam.admin.chat.presentation.manage_chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twugteam.admin.chat.presentation.components.create_or_manage_chat.CreateOrManageChatAction
import com.twugteam.admin.chat.presentation.components.create_or_manage_chat.CreateOrManageChatScreen
import com.twugteam.admin.chat.presentation.components.create_or_manage_chat.CreateOrManageChatState
import com.twugteam.admin.core.designsystem.components.avatar.ChatParticipantUi
import com.twugteam.admin.core.designsystem.components.dialogs.ChirpAdaptiveDialogSheetLayout
import com.twugteam.admin.core.designsystem.theme.ChirpTheme
import com.twugteam.admin.core.presentation.util.ObserveAsEvents
import com.twugteam.admin.feature.chat.presentation.Res
import com.twugteam.admin.feature.chat.presentation.chat_members
import com.twugteam.admin.feature.chat.presentation.save
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ManageChatScreenRoot(
    onDismiss: () -> Unit,
    onMemberAdded: () -> Unit,
    viewModel: ManageChatViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is ManageChatEvent.OnMemberAdded -> onMemberAdded()
        }
    }

    ChirpAdaptiveDialogSheetLayout(
        onDismiss = onDismiss
    ) {
        CreateOrManageChatScreen(
            headerText = stringResource(Res.string.chat_members),
            primaryButtonText = stringResource(Res.string.save),
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
private fun ManageChatScreenPreview() {
    ChirpTheme {
        CreateOrManageChatScreen(
            state = CreateOrManageChatState(
                selectedChatParticipants = listOf(
                    ChatParticipantUi(
                        userId = "123",
                        username = "Kiran",
                    )
                ),
            ),
            onAction = {},
            headerText = "Manage Chat",
            primaryButtonText = "Save Chat"
        )
    }
}