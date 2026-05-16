package com.twugteam.admin.chat.presentation.chat_detail.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twugteam.admin.chat.domain.models.NetworkConnectionState
import com.twugteam.admin.chat.presentation.util.toUiText
import com.twugteam.admin.core.designsystem.components.buttons.ChirpButton
import com.twugteam.admin.core.designsystem.components.textfields.ChirpMultiLineTextField
import com.twugteam.admin.core.designsystem.theme.ChirpTheme
import com.twugteam.admin.core.designsystem.theme.extended
import com.twugteam.admin.feature.chat.presentation.Res
import com.twugteam.admin.feature.chat.presentation.cloud_off_icon
import com.twugteam.admin.feature.chat.presentation.offline
import com.twugteam.admin.feature.chat.presentation.send
import com.twugteam.admin.feature.chat.presentation.send_a_message
import com.twugteam.admin.feature.chat.presentation.sent
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun InputMessageBox(
    messageTextFieldState: TextFieldState,
    isTextInputFieldEnable: Boolean,
    connectionState: NetworkConnectionState,
    onSendClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isConnected = connectionState == NetworkConnectionState.CONNECTED
    ChirpMultiLineTextField(
        state = messageTextFieldState,
        modifier = modifier,
        placeholder = stringResource(Res.string.send_a_message),
        enabled = isTextInputFieldEnable,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Send
        ),
        onKeyboardAction = onSendClick
    ) {
        Spacer(modifier = Modifier.weight(1f))
        if(!isConnected){
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ){
                Icon(
                    imageVector = vectorResource(Res.drawable.cloud_off_icon),
                    contentDescription = connectionState.toUiText().asString(),
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.extended.textDisabled
                )
                Text(
                    text = connectionState.toUiText().asString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.extended.textDisabled
                )
            }
        }
        ChirpButton(
            text = stringResource(Res.string.send),
            onClick = onSendClick,
            enabled = isConnected
        )
    }
}

@Preview
@Composable
private fun InputMessageBoxPreview() {

    ChirpTheme(
        isDarkTheme = true
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            contentAlignment = Alignment.BottomCenter
        ){
            InputMessageBox(
                messageTextFieldState = TextFieldState(),
                connectionState = NetworkConnectionState.ERROR_NETWORK,
                isTextInputFieldEnable = true,
                onSendClick = {},
                modifier = Modifier.fillMaxSize()
            )
        }

    }

}