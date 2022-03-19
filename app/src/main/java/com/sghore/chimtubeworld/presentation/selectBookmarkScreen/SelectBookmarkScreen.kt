package com.sghore.chimtubeworld.presentation.selectBookmarkScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sghore.chimtubeworld.R
import com.sghore.chimtubeworld.data.model.Bookmark
import com.sghore.chimtubeworld.util.parseDateTextFromPosition

@Composable
fun SelectBookmarkScreen(
    bookmarkList: List<Bookmark>,
    onBookmarkClick: (Long) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(color = colorResource(id = R.color.default_background_color)),
        contentAlignment = Alignment.TopCenter
    ) {
        Column {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "시작 위치 선택",
                fontSize = 16.sp,
                color = colorResource(id = R.color.item_color),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(14.dp))
            Divider(
                modifier = Modifier.fillMaxWidth(),
                color = colorResource(id = R.color.gray_bright_night)
            )

            LazyColumn(
                modifier = Modifier
                    .heightIn(max = 280.dp),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    BookmarkItem(
                        bookmark = null,
                        onClick = onBookmarkClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(color = colorResource(id = R.color.gray_bright_night))
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                items(bookmarkList) { bookmark ->
                    BookmarkItem(
                        bookmark = bookmark,
                        onClick = onBookmarkClick
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun BookmarkItem(
    bookmark: Bookmark?,
    onClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    color = colorResource(id = R.color.item_color)
                )
            ) {
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
            "${bookmark.title} (${parseDateTextFromPosition(bookmark.videoPosition)})"
        } else {
            "처음부터"
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
            color = colorResource(id = R.color.item_color),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
    }
}