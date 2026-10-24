package com.sghore.chimtubeworld.core.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.paging.compose.LazyPagingItems
import com.sghore.chimtubeworld.core.designsystem.component.RowItemCollocate

@Composable
fun <T : Any> PagingRowList(
    list: LazyPagingItems<T>,
    spanCount: Int,
    contentPaddingValue: Dp,
    itemPaddingValue: Dp,
    headerItem: @Composable () -> Unit,
    listItem: @Composable (Int, Modifier) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        contentPadding = PaddingValues(contentPaddingValue)
    ) {
        val itemCount = if (list.itemCount % spanCount == 0) {
            list.itemCount / spanCount
        } else {
            list.itemCount / spanCount + 1
        }

        item {
            headerItem()
        }

        items(itemCount) { index ->
            RowItemCollocate(
                rowIndex = index,
                list = list.itemSnapshotList.items,
                spanCount = spanCount,
                paddingValue = itemPaddingValue,
                content = listItem,
                modifier = modifier
            )
        }
    }
}

