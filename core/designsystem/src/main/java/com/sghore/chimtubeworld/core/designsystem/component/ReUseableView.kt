package com.sghore.chimtubeworld.core.designsystem.component

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sghore.chimtubeworld.core.designsystem.R
import com.sghore.chimtubeworld.core.designsystem.theme.ChimtubeWorldTheme

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
                content = listItem,
                modifier = modifier
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
    content: @Composable (Int, Modifier) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
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


@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TitleTextWithExplainPreview() {
    ChimtubeWorldTheme(darkTheme = isSystemInDarkTheme()) {
        TitleTextWithExplain(title = "제목", explain = "설명")
    }
}

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun RowListPreview() {
    ChimtubeWorldTheme(darkTheme = isSystemInDarkTheme()) {
        RowList(
            list = listOf("항목 1", "항목 2", "항목 3"),
            spanCount = 2,
            contentPaddingValue = 16.dp,
            itemPaddingValue = 8.dp,
            headerItem = { TitleTextWithExplain(title = "목록", explain = "설명") },
            listItem = { index, modifier -> Text(text = "항목 $index", modifier = modifier) }
        )
    }
}

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun RowItemCollocatePreview() {
    ChimtubeWorldTheme(darkTheme = isSystemInDarkTheme()) {
        RowItemCollocate(
            rowIndex = 0,
            list = listOf("항목 1", "항목 2"),
            spanCount = 2,
            paddingValue = 8.dp,
            content = { index, modifier -> Text(text = "항목 $index", modifier = modifier) }
        )
    }
}
