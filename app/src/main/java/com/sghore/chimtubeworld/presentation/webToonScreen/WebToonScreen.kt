package com.sghore.chimtubeworld.presentation.webToonScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.sghore.chimtubeworld.R
import com.sghore.chimtubeworld.data.model.Channel
import com.sghore.chimtubeworld.presentation.TitleTextWithExplain

@Composable
fun WebToonScreen(
    viewModel: WebToonViewModel,
    onWebToonClick: (Channel) -> Unit
) {
    val webToonList = viewModel.state.webtoos ?: emptyList()

    val spanCount = 2
    val itemCount = if (webToonList.size % spanCount == 0) {
        webToonList.size / spanCount
    } else {
        webToonList.size / spanCount + 1
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        LazyColumn(contentPadding = PaddingValues(12.dp)) {
            if (!viewModel.state.isLoading) {
                item {
                    TitleTextWithExplain(
                        title = "WebToon",
                        explain = "이말년 시절 작품활동"
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                items(itemCount) { index ->
                    WebToonRow(
                        rowIndex = index,
                        webtoonsList = webToonList,
                        spanCount = spanCount,
                        onClick = onWebToonClick
                    )
                }
            }
        }

        if (viewModel.state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center),
                color = colorResource(id = R.color.item_color)
            )
        }
    }
}

@Composable
fun WebToonRow(
    rowIndex: Int,
    webtoonsList: List<Channel>,
    spanCount: Int,
    onClick: (Channel) -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row {
            for (index in 0 until spanCount) {
                if (webtoonsList.size >= rowIndex * spanCount + (index + 1)) {
                    WebToonItem(
                        webtoon = webtoonsList[rowIndex * spanCount + index],
                        modifier = Modifier
                            .weight(1f),
                        onClick = onClick
                    )

                    if (index != spanCount - 1) {
                        Spacer(modifier = Modifier.width(12.dp))
                    }
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
fun WebToonItem(
    webtoon: Channel,
    modifier: Modifier = Modifier,
    onClick: (Channel) -> Unit = {}
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .height(220.dp)
            .background(color = Color(webtoon.type))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = true)
            ) {
                onClick(webtoon)
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = rememberImagePainter(
                data = webtoon.image
            ),
            contentDescription = webtoon.id,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = webtoon.name,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = webtoon.explains[0],
            color = Color.White,
        )
    }
}

@Preview
@Composable
fun WebToonItemPreview() {
    MaterialTheme {
        WebToonItem(
            webtoon = Channel(
                "",
                "이말년씨리즈",
                arrayOf("최종 업데이트 2018.12.03"),
                "",
                "",
                type = android.graphics.Color.parseColor("#C3B9A0")
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}