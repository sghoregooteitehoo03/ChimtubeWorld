package com.sghore.chimtubeworld.feature.youtube.impl.videos_screen.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sghore.chimtubeworld.core.common.parseDateTextFromPosition
import com.sghore.chimtubeworld.core.designsystem.R as DesignSystemR
import com.sghore.chimtubeworld.core.designsystem.theme.ChimtubeWorldTheme
import com.sghore.chimtubeworld.core.model.Bookmark
import com.sghore.chimtubeworld.feature.youtube.impl.R

@Composable
internal fun SelectBookmarkContent(
    bookmarkList: List<Bookmark>,
    onBookmarkClick: (Long) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(color = colorResource(id = DesignSystemR.color.default_background_color)),
        contentAlignment = Alignment.TopCenter
    ) {
        Column {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.videos_select_position),
                fontSize = 16.sp,
                color = colorResource(id = DesignSystemR.color.item_color),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                color = colorResource(id = DesignSystemR.color.gray_bright_night)
            )

            LazyColumn(
                modifier = Modifier
                    .heightIn(max = 280.dp),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    SelectionBookmarkItem(
                        bookmark = null,
                        onClick = onBookmarkClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(color = colorResource(id = DesignSystemR.color.gray_bright_night))
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                items(bookmarkList) { bookmark ->
                    SelectionBookmarkItem(
                        bookmark = bookmark,
                        onClick = onBookmarkClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(color = colorResource(id = DesignSystemR.color.gray_bright_night))
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun SelectionBookmarkItem(
    bookmark: Bookmark?,
    onClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clickable {
                onClick(bookmark?.videoPosition ?: -32400000) // 0[s]
            }
            .padding(
                start = 12.dp,
                end = 12.dp,
                top = 16.dp,
                bottom = 16.dp
            )
    ) {
        val bookmarkColor = if (bookmark != null) {
            Color(bookmark.color)
        } else {
            Color.Transparent
        }
        val bookmarkText = if (bookmark != null) {
            stringResource(R.string.videos_bookmark_position, bookmark.title, parseDateTextFromPosition(bookmark.videoPosition))
        } else {
            stringResource(R.string.videos_from_start)
        }

        Box(
            modifier = Modifier
                .size(20.dp)
                .background(
                    color = bookmarkColor,
                    shape = CircleShape
                )
        )

        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = bookmarkText,
            color = colorResource(id = DesignSystemR.color.item_color),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SelectionContentPreview() {
    ChimtubeWorldTheme { SelectBookmarkContent(emptyList(), {}) }
}

@Preview(showBackground = true)
@Composable
private fun SelectionBookmarkPreview() {
    ChimtubeWorldTheme { SelectionBookmarkItem(null, {}) }
}
