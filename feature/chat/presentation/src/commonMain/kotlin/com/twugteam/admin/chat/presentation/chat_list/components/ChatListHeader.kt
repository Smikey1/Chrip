package com.twugteam.admin.chat.presentation.chat_list.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twugteam.admin.chat.domain.models.ChatParticipant
import com.twugteam.admin.chat.presentation.components.ChatHeader
import com.twugteam.admin.chat.presentation.mappers.toUi
import com.twugteam.admin.core.designsystem.components.avatar.ChatParticipantUi
import com.twugteam.admin.core.designsystem.components.avatar.ChripAvatarPhoto
import com.twugteam.admin.core.designsystem.components.dropdown.ChirpDropdownMenu
import com.twugteam.admin.core.designsystem.components.dropdown.ChirpDropdownMenuItem
import com.twugteam.admin.core.designsystem.log_out_icon
import com.twugteam.admin.core.designsystem.logo_chirp
import com.twugteam.admin.core.designsystem.settings_icon
import com.twugteam.admin.core.designsystem.theme.ChirpTheme
import com.twugteam.admin.core.designsystem.theme.extended
import com.twugteam.admin.feature.chat.presentation.Res
import com.twugteam.admin.feature.chat.presentation.logout
import com.twugteam.admin.feature.chat.presentation.profile_settings
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import com.twugteam.admin.core.designsystem.Res as DesignSystemRes

@Composable
fun ChatListHeader(
    localParticipant: ChatParticipantUi?,
    isUserMenuOpen: Boolean,
    onUserAvatarClick: () -> Unit,
    onProfileSettingClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onDismissClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ChatHeader(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = vectorResource(DesignSystemRes.drawable.logo_chirp),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary
            )
            Text(
                text = "Chirp",
                color = MaterialTheme.colorScheme.extended.textPrimary,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.weight(1f))

            ProfileAvatarSection(
                localParticipant = localParticipant,
                isUserMenuOpen = isUserMenuOpen,
                onDismissClick = onDismissClick,
                onProfileSettingClick = onProfileSettingClick,
                onLogoutClick = onLogoutClick,
                onUserAvatarClick = onUserAvatarClick
            )
        }
    }
}

@Composable
fun ProfileAvatarSection(
    localParticipant: ChatParticipantUi?,
    isUserMenuOpen: Boolean,
    onUserAvatarClick: () -> Unit,
    onProfileSettingClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onDismissClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
    ) {
        if (localParticipant != null) {
            ChripAvatarPhoto(
                displayInitialText = localParticipant.initial,
                imageUrl = localParticipant.imageUrl,
                onProfileClick = onUserAvatarClick
            )
            ChirpDropdownMenu(
                isDropdownMenuOpen = isUserMenuOpen,
                onDismissClick = onDismissClick,
                items = listOf(
                    ChirpDropdownMenuItem(
                        title = stringResource(Res.string.profile_settings),
                        icon = vectorResource(DesignSystemRes.drawable.settings_icon),
                        contentColor = MaterialTheme.colorScheme.extended.textSecondary,
                        onClick = onProfileSettingClick
                    ),

                    ChirpDropdownMenuItem(
                        title = stringResource(Res.string.logout),
                        icon = vectorResource(DesignSystemRes.drawable.log_out_icon),
                        contentColor = MaterialTheme.colorScheme.extended.destructiveHover,
                        onClick = onLogoutClick
                    )
                )
            )
        }
    }
}


@Preview
@Composable
private fun ChatListHeaderPreview() {
    ChirpTheme(
        isDarkTheme = true
    ) {
        ChatListHeader(
            modifier = Modifier.fillMaxSize(),
            localParticipant = ChatParticipant(
                userId = "123",
                username = "Kiran",
                profilePictureUrl = null,
            ).toUi(),
            isUserMenuOpen = true,
            onUserAvatarClick = { },
            onProfileSettingClick = { },
            onLogoutClick = { },
            onDismissClick = { },
        )

    }

}