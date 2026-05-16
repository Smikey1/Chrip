package com.twugteam.admin.core.designsystem.components.dropdown

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.twugteam.admin.core.designsystem.components.divider.ChirpHorizontalDivider
import com.twugteam.admin.core.designsystem.theme.extended

@Composable
fun ChirpDropdownMenu(
    isDropdownMenuOpen: Boolean,
    onDismissClick: () -> Unit,
    items: List<ChirpDropdownMenuItem>,
    modifier: Modifier = Modifier
) {
    DropdownMenu(
        expanded = isDropdownMenuOpen,
        onDismissRequest = onDismissClick,
        shape = RoundedCornerShape(16.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.extended.surfaceOutline
        )
    ) {
        items.forEachIndexed { index, item ->
            DropdownMenuItem(
                onClick = item.onClick,
                text = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            tint = item.contentColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = item.title,
                            color = item.contentColor,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            )
            if (index != items.lastIndex) {
                ChirpHorizontalDivider()
            }
        }
    }
}