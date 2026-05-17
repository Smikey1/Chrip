package com.twugteam.admin.chat.presentation.chat_list_detail

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.PaneScaffoldDirective
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.twugteam.admin.core.presentation.util.DeviceConfiguration
import com.twugteam.admin.core.presentation.util.getCurrentDeviceConfiguration

@Composable
fun createNoSpacingPaneScaffoldDirective(): PaneScaffoldDirective {
    val deviceConfiguration = getCurrentDeviceConfiguration()
    val currentWindowAdaptiveInfo = currentWindowAdaptiveInfo()
    val maxHorizontalPartitions = when (deviceConfiguration) {
        DeviceConfiguration.MOBILE_PORTRAIT,
        DeviceConfiguration.MOBILE_LANDSCAPE,
        DeviceConfiguration.TABLET_PORTRAIT -> 1

        DeviceConfiguration.TABLET_LANDSCAPE,
        DeviceConfiguration.DESKTOP -> 2
    }

    // For foldable devices
    val verticalPartitionSpacerSize: Dp
    val maxVerticalPartitions: Int

    // TableTop is folding device form vertically
    if (currentWindowAdaptiveInfo.windowPosture.isTabletop) {
        maxVerticalPartitions = 2
        verticalPartitionSpacerSize = 24.dp
    } else {
        maxVerticalPartitions = 1
        verticalPartitionSpacerSize = 0.dp
    }
    return PaneScaffoldDirective(
        maxHorizontalPartitions = maxHorizontalPartitions,
        // there should not be any extra spacing for horizontal partition between
        // Chat List and Chat Detail Screen
        horizontalPartitionSpacerSize = 0.dp,
        maxVerticalPartitions = maxVerticalPartitions,
        verticalPartitionSpacerSize = verticalPartitionSpacerSize,
        defaultPanePreferredWidth = 360.dp,
        excludedBounds = emptyList()

    )
}