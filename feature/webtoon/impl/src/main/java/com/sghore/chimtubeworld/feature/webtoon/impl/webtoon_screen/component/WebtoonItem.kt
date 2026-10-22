package com.sghore.chimtubeworld.feature.webtoon.impl.webtoon_screen.component

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.sghore.chimtubeworld.core.designsystem.theme.ChimtubeWorldTheme
import com.sghore.chimtubeworld.core.model.Channel
import com.sghore.chimtubeworld.feature.webtoon.impl.webtoon_screen.WebtoonUiState
import com.sghore.chimtubeworld.core.designsystem.R as DesignSystemR

@Composable
internal fun WebtoonItem(
    webtoon: Channel,
    contentDescription: String,
    onClick: (Channel) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.clickable { onClick(webtoon) },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = webtoon.image.toInt()),
            contentDescription = contentDescription,
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
            color = colorResource(id = DesignSystemR.color.default_text_color)
        )
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun WebtoonItemPreview() {
    val webtoon = WebtoonUiState().webtoonInfos.first()

    ChimtubeWorldTheme(darkTheme = isSystemInDarkTheme()) {
        WebtoonItem(
            webtoon = webtoon,
            contentDescription = webtoon.id,
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}
