package com.twugteam.admin.core.designsystem.components.avatar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.twugteam.admin.core.designsystem.theme.ChirpTheme

@Composable
fun ChripStackedAvatar(
    avatars: List<ChatParticipantUi>,
    modifier: Modifier = Modifier,
    size: AvatarSize = AvatarSize.SMALL,
    maxVisibleAvatarCount: Int = 2,
    stackAvatarOverlapPercentage: Float = 0.4f
) {
    val overlapOffset = -(size.dp * stackAvatarOverlapPercentage)
    val visibleAvatar = avatars.take(maxVisibleAvatarCount)
    val remainingCount = (avatars.size - maxVisibleAvatarCount).coerceAtLeast(0)

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(overlapOffset),
        verticalAlignment = Alignment.CenterVertically
    ) {
        visibleAvatar.forEach { avatarUi ->
            ChripAvatarPhoto(
                displayInitialText = avatarUi.initial,
                size = size,
                imageUrl = avatarUi.imageUrl
            )
        }
        if (remainingCount > 0) {
            ChripAvatarPhoto(
                displayInitialText = "$remainingCount+",
                size = size,
                textColor = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview
@Composable
private fun ChripStackedAvatarPreview() {
    ChirpTheme {
        ChripStackedAvatar(
            avatars = (1..5).map {
                ChatParticipantUi(
                    userId = it.toString(),
                    username = "U$it",
                )
            },
            modifier = Modifier.fillMaxSize()
        )

    }

}