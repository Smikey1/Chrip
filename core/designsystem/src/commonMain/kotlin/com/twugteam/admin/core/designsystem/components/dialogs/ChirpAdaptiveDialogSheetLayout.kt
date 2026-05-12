package com.twugteam.admin.core.designsystem.components.dialogs

import androidx.compose.runtime.Composable
import com.twugteam.admin.core.presentation.util.getCurrentDeviceConfiguration

@Composable
fun ChirpAdaptiveDialogSheetLayout(
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    val deviceConfiguration = getCurrentDeviceConfiguration()
    if (deviceConfiguration.isMobile) {
        ChirpBottomSheet(
            onDismiss = onDismiss,
            content = content
        )
    } else {
        ChirpDialog(
            onDismiss = onDismiss,
            content = content
        )
    }
}