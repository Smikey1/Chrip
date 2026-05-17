package com.twugteam.admin.chat.presentation.components.create_or_manage_chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import com.twugteam.admin.chat.presentation.components.ChatParticipantSearchTextSection
import com.twugteam.admin.chat.presentation.components.ChatParticipantSelectionSection
import com.twugteam.admin.chat.presentation.components.ManageChatButtonSection
import com.twugteam.admin.chat.presentation.components.ManageChatHeaderRow
import com.twugteam.admin.core.designsystem.components.buttons.ChirpButton
import com.twugteam.admin.core.designsystem.components.buttons.ChripButtonStyle
import com.twugteam.admin.core.designsystem.components.divider.ChirpHorizontalDivider
import com.twugteam.admin.core.designsystem.theme.ChirpTheme
import com.twugteam.admin.core.presentation.util.DeviceConfiguration
import com.twugteam.admin.core.presentation.util.clearFocusOnTapOutside
import com.twugteam.admin.core.presentation.util.getCurrentDeviceConfiguration
import com.twugteam.admin.feature.chat.presentation.Res
import com.twugteam.admin.feature.chat.presentation.cancel
import org.jetbrains.compose.resources.stringResource

@Composable
fun CreateOrManageChatScreen(
    headerText: String,
    primaryButtonText: String,
    state: CreateOrManageChatState,
    onAction: (CreateOrManageChatAction) -> Unit
) {
    var isTextFieldFocus: Boolean by mutableStateOf(false)
    val imeHeight = WindowInsets.ime.getBottom(LocalDensity.current)
    val isKeyboardVisible = imeHeight > 0
    val currentDeviceConfiguration = getCurrentDeviceConfiguration()

    val shouldHideHeader = currentDeviceConfiguration == DeviceConfiguration.MOBILE_LANDSCAPE
            || (isKeyboardVisible && currentDeviceConfiguration != DeviceConfiguration.DESKTOP) || isTextFieldFocus

    Column(
        modifier = Modifier
            .clearFocusOnTapOutside()
            .fillMaxWidth()
            .wrapContentHeight()
            .imePadding()
            .background(MaterialTheme.colorScheme.surface)
            .navigationBarsPadding()

    ) {
        AnimatedVisibility(
            visible = !shouldHideHeader
        ) {
            Column {
                ManageChatHeaderRow(
                    title = headerText,
                    modifier = Modifier.fillMaxWidth(),
                    onCloseClick = {
                        onAction(CreateOrManageChatAction.OnDismissDialog)
                    }
                )
                ChirpHorizontalDivider()
            }
        }
        ChatParticipantSearchTextSection(
            searchQueryState = state.searchQueryTextState,
            isSearchEnabled = state.canAddParticipant,
            isLoading = state.isSearching,
            modifier = Modifier.fillMaxWidth(),
            error = state.searchError,
            onFocusChanged = {
                isTextFieldFocus = it
            },
            onAddParticipantClick = {
                onAction(CreateOrManageChatAction.OnAddParticipantClick)
            }
        )
        ChirpHorizontalDivider()
        ChatParticipantSelectionSection(
            existingParticipants = state.existingChatParticipants,
            selectedParticipants = state.selectedChatParticipants,
            modifier = Modifier.fillMaxWidth(),
            searchResult = state.currentSearchResult
        )
        ChirpHorizontalDivider()
        ManageChatButtonSection(
            modifier = Modifier.fillMaxWidth(),
            error = state.createChatError?.asString(),
            primaryButton = {
                ChirpButton(
                    text = primaryButtonText,
                    enabled = state.selectedChatParticipants.isNotEmpty(),
                    isLoading = state.isCreatingChat,
                    onClick = {
                        onAction(CreateOrManageChatAction.OnCreateOrManageChatClick)
                    }
                )
            },
            secondaryButton = {
                ChirpButton(
                    text = stringResource(Res.string.cancel),
                    style = ChripButtonStyle.SECONDARY,
                    onClick = {
                        onAction(CreateOrManageChatAction.OnDismissDialog)
                    }
                )
            }
        )
    }
}

@Preview
@Composable
private fun CreateOrManageChatScreenPreview() {
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