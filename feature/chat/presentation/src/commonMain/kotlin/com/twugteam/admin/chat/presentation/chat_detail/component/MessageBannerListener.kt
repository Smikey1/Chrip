package com.twugteam.admin.chat.presentation.chat_detail.component

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import com.twugteam.admin.chat.presentation.model.MessageUi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlin.time.Duration.Companion.seconds

@Composable
fun MessageBannerListener(
    lazyListState: LazyListState,
    messages: List<MessageUi>,
    isBannerVisible: Boolean,
    onShowBanner: (topVisibleItemIndex: Int) -> Unit,
    onHide: () -> Unit
) {
    val isBannerVisibleUpdated by rememberUpdatedState(isBannerVisible)

    LaunchedEffect(lazyListState, messages) {
        snapshotFlow {
            val layoutInfo = lazyListState.layoutInfo
            val totalItemCount = layoutInfo.totalItemsCount
            val visibleItems = layoutInfo.visibleItemsInfo

            val oldestVisibleMessageIndex: Int = visibleItems.maxOfOrNull { it.index } ?: -1

            val isAtTopOldestMessages = oldestVisibleMessageIndex >= totalItemCount - 1
            val isAtBottomNewestMessage = visibleItems.any { it.index == 0 }

            MessageBannerScrollState(
                isScrollInProgress = lazyListState.isScrollInProgress,
                oldestVisibleMessageIndex = oldestVisibleMessageIndex,
                isVeryCloseToBottomOrTopOfList = isAtTopOldestMessages || isAtBottomNewestMessage
            )
        }.distinctUntilChanged() // it means the new emission is different from previous emission
            .collect { bannerState ->
                val shouldShowBanner = bannerState.isScrollInProgress
                        && bannerState.isVeryCloseToBottomOrTopOfList
                        && bannerState.oldestVisibleMessageIndex >= 0

                when {
                    shouldShowBanner -> onShowBanner(bannerState.oldestVisibleMessageIndex)
                    !shouldShowBanner && isBannerVisibleUpdated -> {
                        delay(1.seconds)
                        onHide()
                    }
                }
            }
    }
}

data class MessageBannerScrollState(
    val oldestVisibleMessageIndex: Int,
    val isScrollInProgress: Boolean,
    val isVeryCloseToBottomOrTopOfList: Boolean
)