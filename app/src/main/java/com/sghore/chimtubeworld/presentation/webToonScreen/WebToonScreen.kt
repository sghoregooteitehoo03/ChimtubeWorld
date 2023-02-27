package com.sghore.chimtubeworld.presentation.webToonScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sghore.chimtubeworld.R
import com.sghore.chimtubeworld.data.model.Channel
import com.sghore.chimtubeworld.presentation.RowList
import com.sghore.chimtubeworld.presentation.TitleTextWithExplain

@Composable
fun WebToonScreen(
    webToonInfos: List<Channel>,
    onWebToonClick: (Channel) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        RowList(
            list = webToonInfos,
            spanCount = 2,
            contentPaddingValue = 12.dp,
            itemPaddingValue = 12.dp,
            headerItem = {
                TitleTextWithExplain(
                    title = "WebToon",
                    explain = "이말년 시절 작품활동"
                )
                Spacer(modifier = Modifier.height(16.dp))
            },
            listItem = { index, modifier ->
                WebToonItem(
                    webtoon = webToonInfos[index],
                    modifier = modifier,
                    onClick = onWebToonClick
                )
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun WebToonItem(
    webtoon: Channel,
    onClick: (Channel) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
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
            painter = painterResource(id = webtoon.image.toInt()),
            contentDescription = webtoon.id,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(12.dp))
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = webtoon.name,
            color = colorResource(id = R.color.item_color),
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = webtoon.explains[0],
            color = colorResource(id = R.color.default_text_color),
        )
    }
}

@Preview
@Composable
fun WebToonItemPreview() {
    MaterialTheme {
        WebToonItem(
            webtoon = Channel(
                id = "",
                name = "이말년씨리즈",
                explains = arrayOf("최종 업데이트 2018.12.03"),
                url = "",
                image = "",
                type = android.graphics.Color.parseColor("#C3B9A0")
            ),
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}