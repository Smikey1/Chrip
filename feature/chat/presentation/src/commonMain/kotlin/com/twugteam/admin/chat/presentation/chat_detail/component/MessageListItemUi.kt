package com.twugteam.admin.chat.presentation.chat_detail.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twugteam.admin.chat.domain.models.ChatMessageDeliveryStatus
import com.twugteam.admin.chat.presentation.model.MessageUi
import com.twugteam.admin.chat.presentation.util.getChatBubbleColorForUser
import com.twugteam.admin.core.designsystem.theme.ChirpTheme
import com.twugteam.admin.core.designsystem.theme.extended
import com.twugteam.admin.core.presentation.util.UiText

@Composable
fun MessageListItemUi(
    messageUi: MessageUi,
    onMessageLongClick: (MessageUi.LocalUserMessage) -> Unit,
    onDismissMessageMenu: () -> Unit,
    onDeleteClick: (MessageUi.LocalUserMessage) -> Unit,
    onRetryClick: (MessageUi.LocalUserMessage) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
    ) {
        when (messageUi) {
            is MessageUi.DateSeparator -> {
                DateSeparator(
                    date = messageUi.date.asString(),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            is MessageUi.LocalUserMessage -> {
                LocalUserMessage(
                    message = messageUi,
                    onMessageLongClick = onMessageLongClick,
                    onDismissMessageMenu = onDismissMessageMenu,
                    onDeleteClick = { onDeleteClick(messageUi) },
                    onRetryClick = { onRetryClick(messageUi) }
                )
            }

            is MessageUi.OtherUserMessage -> {
                OtherUserMessage(
                    message = messageUi,
                    color = getChatBubbleColorForUser(messageUi.sender.userId)
                )
            }
        }
    }
}


@Composable
private fun DateSeparator(
    date: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f))
        Text(
            text = date,
            modifier = Modifier.padding(horizontal = 40.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.extended.textPlaceholder
        )
        HorizontalDivider(modifier = Modifier.weight(1f))
    }
}


@Preview
@Composable
private fun MessageListItemUiPreview() {
    ChirpTheme(
        isDarkTheme = true
    ) {
        MessageListItemUi(
            modifier = Modifier.fillMaxSize(),
            onMessageLongClick = {},
            onRetryClick = {},
            onDeleteClick = {},
            onDismissMessageMenu = {},
            messageUi = MessageUi.LocalUserMessage(
                id = "121",
                content = "Hey",
                isMenuOpen = false,
                formattedSentTime = UiText.DynamicString("now"),
                deliveryStatus = ChatMessageDeliveryStatus.FAILED
            )
        )

    }

}