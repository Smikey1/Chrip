package com.twugteam.admin.chat.presentation.chat_detail.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twugteam.admin.chat.domain.models.ChatMessageDeliveryStatus
import com.twugteam.admin.core.designsystem.theme.ChirpTheme
import com.twugteam.admin.core.designsystem.theme.extended
import com.twugteam.admin.core.designsystem.theme.labelXSmall
import com.twugteam.admin.feature.chat.presentation.Res
import com.twugteam.admin.feature.chat.presentation.check_icon
import com.twugteam.admin.feature.chat.presentation.failed
import com.twugteam.admin.feature.chat.presentation.loading_icon
import com.twugteam.admin.feature.chat.presentation.sending
import com.twugteam.admin.feature.chat.presentation.sent
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun MessageStatus(
    status: ChatMessageDeliveryStatus,
    modifier: Modifier = Modifier
) {
    val (text, icon, color) = when (status) {
        ChatMessageDeliveryStatus.SENDING -> Triple(
            first = stringResource(Res.string.sending),
            second = vectorResource(Res.drawable.loading_icon),
            third = MaterialTheme.colorScheme.extended.textDisabled
        )

        ChatMessageDeliveryStatus.SENT -> Triple(
            first = stringResource(Res.string.sent),
            second = vectorResource(Res.drawable.check_icon),
            third = MaterialTheme.colorScheme.extended.textTertiary
        )

        ChatMessageDeliveryStatus.FAILED -> Triple(
            first = stringResource(Res.string.failed),
            second = Icons.Default.Close,
            third = MaterialTheme.colorScheme.error
        )
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = color,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            color = color,
            style = MaterialTheme.typography.labelXSmall
        )
    }
}

@Preview
@Composable
private fun MessageStatusPreview() {

    ChirpTheme(
        isDarkTheme = true
    ) {
        MessageStatus(
            modifier = Modifier.fillMaxWidth(),
            status = ChatMessageDeliveryStatus.SENT
        )

    }

}