package com.twugteam.admin.core.designsystem.components.avatar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.twugteam.admin.core.designsystem.theme.ChirpTheme
import com.twugteam.admin.core.designsystem.theme.extended

enum class AvatarSize(val dp: Dp) {
    SMALL(40.dp),
    MEDIUM(50.dp),
    LARGE(60.dp)
}

@Composable
fun ChripAvatarPhoto(
    displayInitialText: String,
    modifier: Modifier = Modifier,
    size: AvatarSize = AvatarSize.SMALL,
    imageUrl: String? = null,
    onProfileClick: (() -> Unit)? = null,
    textColor: Color = MaterialTheme.colorScheme.extended.textPlaceholder
) {
    Box(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .clickable(
                enabled = onProfileClick != null,
                onClick = {
                    onProfileClick?.invoke()
                }
            )
            .background(
                color = MaterialTheme.colorScheme.extended.secondaryFill
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = displayInitialText.uppercase(),
            style = MaterialTheme.typography.titleMedium,
            color = textColor
        )
        AsyncImage(
            model = imageUrl,
            contentScale = ContentScale.Crop,
            contentDescription = null,
            modifier = Modifier
                .clip(CircleShape)
                .fillMaxWidth()
        )
    }
}

@Preview
@Composable
private fun ChripAvatarPhotoPreview() {

    ChirpTheme {
        ChripAvatarPhoto(
            size = AvatarSize.LARGE,
            displayInitialText = "SD",
            imageUrl = null,
            onProfileClick = {},
            textColor = MaterialTheme.colorScheme.extended.textPrimary
        )

    }

}