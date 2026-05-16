package com.twugteam.admin.chat.presentation.chat_detail.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twugteam.admin.chat.presentation.model.MessageUi
import com.twugteam.admin.core.designsystem.components.avatar.ChatParticipantUi
import com.twugteam.admin.core.designsystem.components.avatar.ChripAvatarPhoto
import com.twugteam.admin.core.designsystem.components.chat.ChirpChatBubble
import com.twugteam.admin.core.designsystem.components.chat.TrianglePosition
import com.twugteam.admin.core.designsystem.theme.ChirpTheme
import com.twugteam.admin.core.presentation.util.UiText

@Composable
fun OtherUserMessage(
    message: MessageUi.OtherUserMessage,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ChripAvatarPhoto(
            displayInitialText = message.sender.initial,
            imageUrl = message.sender.imageUrl
        )
        ChirpChatBubble(
            messageContent = message.content,
            sender = message.sender.username,
            formattedDateTime = message.formattedSentTime.asString(),
            trianglePosition = TrianglePosition.LEFT,
            color = color
        )
    }
}

@Preview
@Composable
private fun OtherUserMessagePreview() {
    ChirpTheme(
        isDarkTheme = true
    ) {
        OtherUserMessage(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background,
            message = MessageUi.OtherUserMessage(
                id = "121",
                content = "Hey",
                formattedSentTime = UiText.DynamicString("now"),
                sender = ChatParticipantUi(
                    userId = "343",
                    username = "Kiran"
                )
            )
        )
    }
}