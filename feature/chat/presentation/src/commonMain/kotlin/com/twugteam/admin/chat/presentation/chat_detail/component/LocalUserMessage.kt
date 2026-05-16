package com.twugteam.admin.chat.presentation.chat_detail.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twugteam.admin.chat.domain.models.ChatMessageDeliveryStatus
import com.twugteam.admin.chat.presentation.model.MessageUi
import com.twugteam.admin.core.designsystem.components.chat.ChirpChatBubble
import com.twugteam.admin.core.designsystem.components.chat.TrianglePosition
import com.twugteam.admin.core.designsystem.components.dropdown.ChirpDropdownMenu
import com.twugteam.admin.core.designsystem.components.dropdown.ChirpDropdownMenuItem
import com.twugteam.admin.core.designsystem.theme.ChirpTheme
import com.twugteam.admin.core.designsystem.theme.extended
import com.twugteam.admin.core.presentation.util.UiText
import com.twugteam.admin.feature.chat.presentation.Res
import com.twugteam.admin.feature.chat.presentation.delete_for_everyone
import com.twugteam.admin.feature.chat.presentation.reload_icon
import com.twugteam.admin.feature.chat.presentation.retry
import com.twugteam.admin.feature.chat.presentation.you
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun LocalUserMessage(
    message: MessageUi.LocalUserMessage,
    onMessageLongClick: (MessageUi.LocalUserMessage) -> Unit,
    onDismissMessageMenu: () -> Unit,
    onDeleteClick: () -> Unit,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
    ) {
        Box(
            modifier = Modifier.weight(1f)
        ) {
            ChirpChatBubble(
                messageContent = message.content,
                sender = stringResource(Res.string.you),
                formattedDateTime = message.formattedSentTime.asString(),
                trianglePosition = TrianglePosition.RIGHT,
                messageStatus = {
                    MessageStatus(
                        status = message.deliveryStatus
                    )
                },
                onLongClick = {
                    onMessageLongClick(message)
                }
            )

            ChirpDropdownMenu(
                isDropdownMenuOpen = message.isMenuOpen,
                onDismissClick = onDismissMessageMenu,
                items = listOf(
                    ChirpDropdownMenuItem(
                        title = stringResource(Res.string.delete_for_everyone),
                        icon = Icons.Default.Delete,
                        contentColor = MaterialTheme.colorScheme.extended.destructiveHover,
                        onClick = onDeleteClick
                    )
                )
            )
        }
        if (message.deliveryStatus == ChatMessageDeliveryStatus.FAILED) {
            IconButton(
                onClick = onRetryClick,
            ) {
                Icon(
                    imageVector = vectorResource(Res.drawable.reload_icon),
                    contentDescription = stringResource(Res.string.retry),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Preview
@Composable
private fun LocalUserMessagePreview() {
    ChirpTheme(
        isDarkTheme = false
    ) {
        LocalUserMessage(
            modifier = Modifier.fillMaxWidth(),
            onMessageLongClick = {},
            onRetryClick = {},
            onDeleteClick = {},
            onDismissMessageMenu = {},
            message = MessageUi.LocalUserMessage(
                id = "121",
                content = "Hey",
                isMenuOpen = false,
                formattedSentTime = UiText.DynamicString("now"),
                deliveryStatus = ChatMessageDeliveryStatus.FAILED
            )
        )
    }
}