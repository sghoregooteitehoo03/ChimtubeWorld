package com.sghore.chimtubeworld.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sghore.chimtubeworld.R

@Composable
fun TitleTextWithExplain(
    title: String,
    explain: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Bottom
    ) {
        Text(
            text = title,
            color = colorResource(id = R.color.item_color),
            fontSize = 22.sp
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = explain,
            color = colorResource(id = R.color.default_text_color),
            fontSize = 16.sp
        )
    }
}

@Composable
fun <T> RowList(
    list: List<T>,
    spanCount: Int = 2,
    contentPaddingValue: Dp = 12.dp,
    itemPaddingValue: Dp = 12.dp,
    headerItem: @Composable () -> Unit,
    listItem: @Composable (Int, Modifier) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(contentPaddingValue)
    ) {
        val itemCount = if (list.size % spanCount == 0) {
            list.size / spanCount
        } else {
            list.size / spanCount + 1
        }

        item {
            headerItem()
        }

        items(itemCount) { index ->
            RowItemCollocate(
                rowIndex = index,
                list = list,
                spanCount = spanCount,
                paddingValue = itemPaddingValue,
                content = listItem
            )
        }
    }
}

@Composable
fun <T> RowItemCollocate(
    rowIndex: Int,
    list: List<T>,
    spanCount: Int,
    paddingValue: Dp,
    content: @Composable (Int, Modifier) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row {
            for (index in 0 until spanCount) {
                if (list.size >= rowIndex * spanCount + (index + 1)) {
                    content(
                        rowIndex * spanCount + index,
                        Modifier.weight(1f)
                    )

                    if (index != spanCount - 1) {
                        Spacer(modifier = Modifier.width(paddingValue))
                    }
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
        Spacer(modifier = Modifier.height(paddingValue))
    }
}