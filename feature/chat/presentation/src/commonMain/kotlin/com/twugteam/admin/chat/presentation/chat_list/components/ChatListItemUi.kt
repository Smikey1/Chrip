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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
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
import com.twugteam.admin.chat.domain.models.ChatParticipant
import com.twugteam.admin.chat.presentation.mappers.toUi
import com.twugteam.admin.chat.presentation.model.ChatUi
import com.twugteam.admin.core.designsystem.components.avatar.ChripStackedAvatar
import com.twugteam.admin.core.designsystem.theme.ChirpTheme
import com.twugteam.admin.core.designsystem.theme.extended
import com.twugteam.admin.core.designsystem.theme.titleXSmall
import com.twugteam.admin.feature.chat.presentation.Res
import com.twugteam.admin.feature.chat.presentation.group_chat
import com.twugteam.admin.feature.chat.presentation.you
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Clock

@Composable
fun ChatListItemUi(
    chat: ChatUi,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    val isGroupChat = chat.otherParticipant.size > 1
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
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ChripStackedAvatar(
                    avatars = chat.otherParticipant
                )
                Column(
                    modifier = Modifier
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = if (isGroupChat) stringResource(Res.string.group_chat)
                        else {
                            chat.otherParticipant.first().username
                        },
                        style = MaterialTheme.typography.titleXSmall,
                        color = MaterialTheme.colorScheme.extended.textPrimary,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (isGroupChat) {
                        val you = stringResource(Res.string.you)
                        val formattedUsername = remember(chat.otherParticipant) {
                            "$you, " + chat.otherParticipant.joinToString {
                                it.username
                            }
                        }
                        Text(
                            text = formattedUsername,
                            color = MaterialTheme.colorScheme.extended.textPlaceholder,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
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
                    createdAt = Clock.System.now()
                )
            ).toUi(
                localParticipantId = "234"
            ),
            isSelected = true,
        )
    }
}
