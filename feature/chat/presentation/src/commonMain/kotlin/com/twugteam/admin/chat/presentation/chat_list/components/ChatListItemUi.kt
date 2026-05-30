package com.twugteam.admin.chat.presentation.chat_list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twugteam.admin.chat.domain.models.Chat
import com.twugteam.admin.chat.domain.models.ChatMessage
import com.twugteam.admin.chat.domain.models.ChatMessageDeliveryStatus
import com.twugteam.admin.chat.domain.models.ChatParticipant
import com.twugteam.admin.chat.presentation.components.ChatItemHeaderRow
import com.twugteam.admin.chat.presentation.mappers.toUi
import com.twugteam.admin.chat.presentation.model.ChatUi
import com.twugteam.admin.core.designsystem.theme.ChirpTheme
import com.twugteam.admin.core.designsystem.theme.extended
import com.twugteam.admin.feature.chat.presentation.Res
import com.twugteam.admin.feature.chat.presentation.you
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Clock

@Composable
fun ChatListItemUi(
    chat: ChatUi,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    val isLastMessageSenderIsYou = chat.localParticipant.userId == chat.lastMessage?.senderId
    Row(
        modifier = modifier
            .height(IntrinsicSize.Max)
            .background(
                color = if (isSelected) {
                    MaterialTheme.colorScheme.surface
                } else {
                    MaterialTheme.colorScheme.extended.surfaceLower
                }
            )
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ChatItemHeaderRow(
                chat = chat,
                modifier = Modifier.fillMaxWidth()
            )
            if (chat.lastMessage != null) {
                val formattedLastSenderUsername = if (isLastMessageSenderIsYou) {
                    stringResource(Res.string.you)
                } else chat.lastMessageSenderUsername

                val messagePreviewWithBoldSenderName = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.extended.textSecondary,
                        )
                    ) {
                        append("$formattedLastSenderUsername: ")
                    }
                    append(chat.lastMessage.content)
                }
                Text(
                    text = messagePreviewWithBoldSenderName,
                    color = MaterialTheme.colorScheme.extended.textSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Box(
            modifier = Modifier
                .alpha(if (isSelected) 1f else 0f)
                .background(MaterialTheme.colorScheme.primary)
                .width(4.dp)
                .fillMaxHeight()
        ) {

        }
    }
}

@Preview
@Composable
private fun ChatListItemUiPreview() {
    ChirpTheme(
        isDarkTheme = true
    ) {
        ChatListItemUi(
            chat = Chat(
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
            ),
            isSelected = true,
        )
    }
}
