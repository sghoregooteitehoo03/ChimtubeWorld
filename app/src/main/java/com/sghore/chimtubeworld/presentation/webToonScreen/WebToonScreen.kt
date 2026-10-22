package com.sghore.chimtubeworld.presentation.webToonScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sghore.chimtubeworld.core.designsystem.component.RowList
import com.sghore.chimtubeworld.core.designsystem.component.TitleTextWithExplain
import com.sghore.chimtubeworld.core.designsystem.theme.ChimtubeWorldTheme
import com.sghore.chimtubeworld.core.model.Channel
import com.sghore.chimtubeworld.presentation.ScreenScaffold
import com.sghore.chimtubeworld.core.designsystem.R as DesignSystemR

@Composable
fun WebToonScreen(
    webToonInfos: List<Channel>,
    onWebToonClick: (Channel) -> Unit
) {
    ScreenScaffold {
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
}

@Composable
fun WebToonItem(
    webtoon: Channel,
    onClick: (Channel) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable {
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
            color = colorResource(id = DesignSystemR.color.item_color),
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = webtoon.explains[0],
            color = colorResource(id = DesignSystemR.color.default_text_color),
        )
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun WebToonItemPreview() {
    ChimtubeWorldTheme(darkTheme = isSystemInDarkTheme()) {
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

@Preview(name = "Screen Light", showBackground = true)
@Preview(name = "Screen Dark", showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun WebToonScreenPreview() {
    ChimtubeWorldTheme(darkTheme = androidx.compose.foundation.isSystemInDarkTheme()) {
        WebToonScreen(webToonInfos = emptyList(), onWebToonClick = {})
    }
}
