package com.twugteam.admin.chat.presentation.chat_list.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twugteam.admin.core.designsystem.theme.ChirpTheme
import com.twugteam.admin.core.designsystem.theme.extended
import com.twugteam.admin.core.presentation.util.DeviceConfiguration
import com.twugteam.admin.core.presentation.util.getCurrentDeviceConfiguration
import com.twugteam.admin.feature.chat.presentation.Res
import com.twugteam.admin.feature.chat.presentation.empty_chat
import com.twugteam.admin.feature.chat.presentation.no_messages
import com.twugteam.admin.feature.chat.presentation.no_messages_subtitle
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun EmptyChatSection(
    modifier: Modifier = Modifier
) {
    val currentDeviceConfiguration = getCurrentDeviceConfiguration()
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(
            space = 4.dp,
            alignment = Alignment.CenterVertically
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(Res.drawable.empty_chat),
            contentDescription = stringResource(Res.string.no_messages),
            modifier = Modifier.size(
                if(currentDeviceConfiguration == DeviceConfiguration.MOBILE_LANDSCAPE) 125.dp else 200.dp
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(Res.string.no_messages),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.extended.textPrimary
        )
        Text(
            text = stringResource(Res.string.no_messages_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.extended.textSecondary
        )
    }
}

@Preview
@Composable
private fun EmptyChatSectionPreview() {
    ChirpTheme(
        isDarkTheme = true
    ) {
        EmptyChatSection()
    }
}