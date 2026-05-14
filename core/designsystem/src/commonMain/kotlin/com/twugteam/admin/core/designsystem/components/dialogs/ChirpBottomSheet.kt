@file:OptIn(ExperimentalMaterial3Api::class)

package com.twugteam.admin.core.designsystem.components.dialogs

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.twugteam.admin.core.designsystem.theme.ChirpTheme

@Composable
fun ChirpBottomSheet(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    LaunchedEffect(sheetState.isVisible) {
        if (sheetState.isVisible) {
            sheetState.expand()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null,
        contentWindowInsets = {
            WindowInsets()
        },
        modifier = modifier.statusBarsPadding()
    ) {
        content()
    }
}

@Preview
@Composable
private fun ChirpBottomSheetPreview() {
    ChirpTheme {
        ChirpBottomSheet(
            onDismiss = {},
            content = {},
            modifier = Modifier.fillMaxSize()
        )

    }

}