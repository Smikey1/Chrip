package com.twugteam.admin.core.designsystem.components.buttons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twugteam.admin.core.designsystem.theme.ChirpTheme
import com.twugteam.admin.core.designsystem.theme.extended

enum class ChripButtonStyle {
    PRIMARY,
    DESTRUCTIVE_PRIMARY,
    SECONDARY,
    DESTRUCTIVE_SECONDARY,
    TEXT
}

@Composable
fun ChripButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: ChripButtonStyle = ChripButtonStyle.PRIMARY,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    val colors = when (style) {
        ChripButtonStyle.PRIMARY -> {
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = MaterialTheme.colorScheme.extended.disabledFill,
                disabledContentColor = MaterialTheme.colorScheme.extended.textDisabled
            )
        }

        ChripButtonStyle.DESTRUCTIVE_PRIMARY -> {
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError,
                disabledContainerColor = MaterialTheme.colorScheme.extended.disabledFill,
                disabledContentColor = MaterialTheme.colorScheme.extended.textDisabled
            )
        }

        ChripButtonStyle.SECONDARY -> {
            ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.extended.textSecondary,
                disabledContainerColor = Color.Transparent,
                disabledContentColor = MaterialTheme.colorScheme.extended.textDisabled
            )
        }

        ChripButtonStyle.DESTRUCTIVE_SECONDARY -> {
            ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.error,
                disabledContainerColor = Color.Transparent,
                disabledContentColor = MaterialTheme.colorScheme.extended.textDisabled
            )
        }

        ChripButtonStyle.TEXT -> {
            ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.tertiary,
                disabledContainerColor = Color.Transparent,
                disabledContentColor = MaterialTheme.colorScheme.extended.textDisabled
            )
        }
    }

    val defaultBorderStroke = BorderStroke(
        width = 1.dp,
        color = MaterialTheme.colorScheme.extended.disabledOutline
    )

    val borderStroke = when (style) {
        ChripButtonStyle.PRIMARY if !enabled -> defaultBorderStroke
        ChripButtonStyle.DESTRUCTIVE_PRIMARY if !enabled -> defaultBorderStroke
        ChripButtonStyle.SECONDARY -> defaultBorderStroke
        ChripButtonStyle.DESTRUCTIVE_SECONDARY -> {
            val borderColor = if(enabled){
                MaterialTheme.colorScheme.extended.destructiveSecondaryOutline
            } else {
                MaterialTheme.colorScheme.extended.disabledOutline
            }
            BorderStroke(
                width = 1.dp,
                color = borderColor
            )
        }
        else -> null
    }
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = RoundedCornerShape(8.dp),
        colors = colors,
        border = borderStroke
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(15.dp)
                    .alpha(
                        alpha = if (isLoading) 1f else 0f
                    ),
                strokeWidth = 1.5.dp,
            )
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.alpha(
                    alpha = if (isLoading) 0f else 1f
                )
            ) {
                leadingIcon?.invoke()
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleSmall,
                )
            }
        }
    }
}

@Preview
@Composable
fun ChirpButtonPreview() {
    ChirpTheme(
        isDarkTheme = false
    ) {
        Column(
            modifier = Modifier.wrapContentSize(),
            verticalArrangement = Arrangement.spacedBy(
                space = 4.dp,
                alignment = Alignment.CenterVertically
            ),
        ) {
            ChripButton(
                text = "Hello",
                onClick = {},
                modifier = Modifier,
                style = ChripButtonStyle.PRIMARY
            )
            ChripButton(
                text = "Hello",
                onClick = {},
                modifier = Modifier,
                style = ChripButtonStyle.DESTRUCTIVE_PRIMARY
            )
            ChripButton(
                text = "Hello",
                onClick = {},
                modifier = Modifier,
                style = ChripButtonStyle.SECONDARY
            )
            ChripButton(
                text = "Hello",
                onClick = {},
                modifier = Modifier,
                style = ChripButtonStyle.DESTRUCTIVE_SECONDARY
            )
            ChripButton(
                text = "Hello",
                onClick = {},
                modifier = Modifier,
                style = ChripButtonStyle.TEXT
            )
        }
    }
}