package com.twugteam.admin.chat.presentation.chat_detail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twugteam.admin.chat.domain.models.Chat
import com.twugteam.admin.chat.domain.models.ChatMessage
import com.twugteam.admin.chat.domain.models.ChatMessageDeliveryStatus
import com.twugteam.admin.chat.domain.models.ChatParticipant
import com.twugteam.admin.chat.presentation.components.ChatHeader
import com.twugteam.admin.chat.presentation.components.ChatItemHeaderRow
import com.twugteam.admin.chat.presentation.mappers.toUi
import com.twugteam.admin.chat.presentation.model.ChatUi
import com.twugteam.admin.core.designsystem.arrow_left_icon
import com.twugteam.admin.core.designsystem.components.buttons.ChripIconButton
import com.twugteam.admin.core.designsystem.components.dropdown.ChirpDropdownMenu
import com.twugteam.admin.core.designsystem.components.dropdown.ChirpDropdownMenuItem
import com.twugteam.admin.core.designsystem.dots_icon
import com.twugteam.admin.core.designsystem.log_out_icon
import com.twugteam.admin.core.designsystem.theme.ChirpTheme
import com.twugteam.admin.core.designsystem.theme.extended
import com.twugteam.admin.feature.chat.presentation.Res
import com.twugteam.admin.feature.chat.presentation.chat_members
import com.twugteam.admin.feature.chat.presentation.go_back
import com.twugteam.admin.feature.chat.presentation.leave_chat
import com.twugteam.admin.feature.chat.presentation.open_chat_options_menu
import com.twugteam.admin.feature.chat.presentation.users_icon
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import kotlin.time.Clock
import com.twugteam.admin.core.designsystem.Res as DesignSystemRes

@Composable
fun ChatDetailHeader(
    chatUi: ChatUi?,
    isDetailScreenPresent: Boolean,
    isChatOptionMenuOpen: Boolean,
    onChatOptionClick: () -> Unit,
    onManageClick: () -> Unit,
    onLeaveChatClick: () -> Unit,
    onDismissChatOption: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        if (!isDetailScreenPresent) {
            ChripIconButton(
                onClick = onBackClick
            ) {
                Icon(
                    imageVector = vectorResource(DesignSystemRes.drawable.arrow_left_icon),
                    contentDescription = stringResource(Res.string.go_back),
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.extended.textSecondary
                )
            }
        }
        if (chatUi != null){
            ChatItemHeaderRow(
                chat = chatUi,
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        onManageClick()
                    }
            )
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }

        Box {
            ChripIconButton(
                onClick = onChatOptionClick,
            ) {
                Icon(
                    imageVector = vectorResource(DesignSystemRes.drawable.dots_icon),
                    contentDescription = stringResource(Res.string.open_chat_options_menu),
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.extended.textSecondary
                )
            }
            ChirpDropdownMenu(
                isDropdownMenuOpen = isChatOptionMenuOpen,
                onDismissClick = onDismissChatOption,
                items = listOf(
                    ChirpDropdownMenuItem(
                        title = stringResource(Res.string.chat_members),
                        icon = vectorResource(Res.drawable.users_icon),
                        contentColor = MaterialTheme.colorScheme.extended.textSecondary,
                        onClick = onManageClick
                    ),
                    ChirpDropdownMenuItem(
                        title = stringResource(Res.string.leave_chat),
                        icon = vectorResource(DesignSystemRes.drawable.log_out_icon),
                        contentColor = MaterialTheme.colorScheme.extended.destructiveHover,
                        onClick = onLeaveChatClick
                    )
                )
            )
        }
    }
}

@Preview
@Composable
private fun ChatListHeaderPreview() {
    ChirpTheme(
        isDarkTheme = false
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ){
            ChatHeader {
                ChatDetailHeader(
                    modifier = Modifier.fillMaxWidth(),
                    isChatOptionMenuOpen = true,
                    isDetailScreenPresent = false,
                    onChatOptionClick = { },
                    onLeaveChatClick = { },
                    onManageClick = { },
                    onDismissChatOption = { },
                    onBackClick = { },
                    chatUi = Chat(
                        id = "123",
                        participants = listOf(
                            ChatParticipant(
                                userId = "123",
                                username = "Kiran",
                                profilePictureUrl = null,
                            ),
                            ChatParticipant(
                                userId = "789",
                                username = "Dinesh",
                                profilePictureUrl = null,
                            ),
                            ChatParticipant(
                                userId = "234",
                                username = "Ram",
                                profilePictureUrl = null,
                            ),
                            ChatParticipant(
                                userId = "456",
                                username = "Hari",
                                profilePictureUrl = null,
                            ),
                            ChatParticipant(
                                userId = "4565",
                                username = "John",
                                profilePictureUrl = null,
                            )
                        ),
                        lastActivityAt = Clock.System.now(),
                        lastMessage = ChatMessage(
                            id = "121",
                            chatId = "123",
                            senderId = "123",
                            content = "Hello!, Is there any one in this chat",
                            createdAt = Clock.System.now(),
                            deliveryStatus = ChatMessageDeliveryStatus.SENT
                        )
                    ).toUi(
                        localParticipantId = "234"
                    )
                )
            }
        }

    }

}