package com.twugteam.admin.chat.presentation.chat_detail.component

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged

private const val MIN_NUMBER_CLOSE_TO_END_OF_ITEMS = 5

@Composable
fun PaginationScrollListener(
    lazyListState: LazyListState,
    itemCount: Int,
    isPaginationLoading: Boolean,
    isEndReached: Boolean,
    onNearTop: () -> Unit
) {
    val itemCount by rememberUpdatedState(itemCount)
    val isPaginationLoading by rememberUpdatedState(isPaginationLoading)
    val isEndReached by rememberUpdatedState(isEndReached)

    var lastTriggerItemCount by remember { mutableIntStateOf(0) }

    LaunchedEffect(lazyListState) {
        snapshotFlow {
            val layoutInfo = lazyListState.layoutInfo
            // 20 times
            val totalItemCount = layoutInfo.totalItemsCount
            // lastOrNull here because we reverse the layList layout
            // for ex: 6 items is visible in viewport screen
            val topVisibleIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index
            val remainingItems = if (topVisibleIndex != null) {
                // -1 is here for array start with index 0
                totalItemCount - topVisibleIndex - 1 // 20-6-1 = 14 items is remaining to scroll
            } else null

            PaginationScrollState(
                currentItemCount = itemCount,
                isEligibleToTriggerPagination = remainingItems != null &&
                        // this means if you have remaining item in a list out if 20 is <= 5 then
                        // pre-actively start loading next items
                        remainingItems <= MIN_NUMBER_CLOSE_TO_END_OF_ITEMS &&
                        !isPaginationLoading && !isEndReached
            )
        }.distinctUntilChanged() // it means the new emission is different from previous emission
            .collect { paginationScrollState ->
                val shouldTriggerPagination = paginationScrollState.isEligibleToTriggerPagination
                        && paginationScrollState.currentItemCount > lastTriggerItemCount

                if (shouldTriggerPagination) {
                    // Before we load the next page 2 of items, it prevents the pagination anymore
                    // because itemCount=20 and lastTriggerItemCount is updated to 20 already.
                    // So, we only want to paginate if itemCount is greater than 20.
                    lastTriggerItemCount = itemCount
                    onNearTop()
                }
            }


    }
}

data class PaginationScrollState(
    val currentItemCount: Int,
    val isEligibleToTriggerPagination: Boolean
)