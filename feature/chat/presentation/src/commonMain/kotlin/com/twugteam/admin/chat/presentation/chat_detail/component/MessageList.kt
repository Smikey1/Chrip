package com.twugteam.admin.chat.presentation.chat_detail.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twugteam.admin.chat.domain.models.ChatMessageDeliveryStatus
import com.twugteam.admin.chat.presentation.components.EmptySection
import com.twugteam.admin.chat.presentation.model.MessageUi
import com.twugteam.admin.core.designsystem.components.avatar.ChatParticipantUi
import com.twugteam.admin.core.designsystem.theme.ChirpTheme
import com.twugteam.admin.core.presentation.util.UiText
import com.twugteam.admin.feature.chat.presentation.Res
import com.twugteam.admin.feature.chat.presentation.no_chats
import com.twugteam.admin.feature.chat.presentation.no_chats_subtitle
import org.jetbrains.compose.resources.stringResource

@Composable
fun MessageList(
    messages: List<MessageUi>,
    listState: LazyListState,
    messageWithMenuOpen: MessageUi.LocalUserMessage?,
    onMessageLongClick: (MessageUi.LocalUserMessage) -> Unit,
    onDeleteMessageClick: (MessageUi.LocalUserMessage) -> Unit,
    onMessageRetryClick: (MessageUi.LocalUserMessage) -> Unit,
    onDismissMessageMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (messages.isEmpty()) {
        Box(
            modifier = modifier.padding(vertical = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            EmptySection(
                title = stringResource(Res.string.no_chats),
                description = stringResource(Res.string.no_chats_subtitle)
            )
        }
    } else {
        LazyColumn(
            modifier = modifier,
            state = listState,
            contentPadding = PaddingValues(16.dp),
            reverseLayout = true,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(
                items = messages,
                key = { it.id }
            ) { message ->
                MessageListItemUi(
                    messageUi = message,
                    onDismissMessageMenu = onDismissMessageMenu,
                    messageWithMenuOpen = messageWithMenuOpen,
                    onDeleteClick = {
                        onDeleteMessageClick(it)
                    },
                    onRetryClick = {
                        onMessageRetryClick(it)
                    },
                    onMessageLongClick = {
                        onMessageLongClick(it)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItem()
                )
            }
        }
    }
}

@Preview
@Composable
private fun MessageListPreview() {
    ChirpTheme(
        isDarkTheme = true
    ) {
        MessageList(
            onMessageLongClick = {},
            onMessageRetryClick = {},
            onDismissMessageMenu = {},
            onDeleteMessageClick = {},
            modifier = Modifier.fillMaxSize(),
            messages = listOf(
                MessageUi.LocalUserMessage(
                    id = "121",
                    content = "Hey",
                    formattedSentTime = UiText.DynamicString("now"),
                    deliveryStatus = ChatMessageDeliveryStatus.FAILED
                ),
                MessageUi.OtherUserMessage(
                    id = "121",
                    content = "Hey",
                    formattedSentTime = UiText.DynamicString("now"),
                    sender = ChatParticipantUi(
                        userId = "343",
                        username = "Kiran"
                    )
                ),
                MessageUi.OtherUserMessage(
                    id = "122",
                    content = "Hi",
                    formattedSentTime = UiText.DynamicString("now"),
                    sender = ChatParticipantUi(
                        userId = "343",
                        username = "Kiran"
                    )
                )
            ),
            messageWithMenuOpen = null,
            listState = rememberLazyListState(),
        )

    }

}