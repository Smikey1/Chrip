package com.twugteam.admin.chat.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.twugteam.admin.core.designsystem.components.buttons.ChirpButton
import com.twugteam.admin.core.designsystem.components.buttons.ChripButtonStyle
import com.twugteam.admin.core.designsystem.components.textfields.ChirpTextField
import com.twugteam.admin.core.presentation.util.UiText
import com.twugteam.admin.feature.chat.presentation.Res
import com.twugteam.admin.feature.chat.presentation.add
import com.twugteam.admin.feature.chat.presentation.email_or_username
import org.jetbrains.compose.resources.stringResource

@Composable
fun ChatParticipantSearchTextSection(
    searchQueryState: TextFieldState,
    onAddParticipantClick: () -> Unit,
    isSearchEnabled: Boolean,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    error: UiText? = null,
    onFocusChanged: (Boolean) -> Unit
) {
    Row(
        modifier = modifier
            .padding(
                horizontal = 20.dp,
                vertical = 16.dp
            ),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ChirpTextField(
            state = searchQueryState,
            modifier = Modifier.weight(1f),
            placeholder = stringResource(Res.string.email_or_username),
            title = null,
            supportingText = error?.asString(),
            isError = error != null,
            singleLine = true,
            keyboardType = KeyboardType.Email,
            onFocusChange = onFocusChanged
        )
        ChirpButton(
            text = stringResource(Res.string.add),
            onClick = onAddParticipantClick,
            style = ChripButtonStyle.SECONDARY,
            enabled = isSearchEnabled,
            isLoading = isLoading
        )
    }
}