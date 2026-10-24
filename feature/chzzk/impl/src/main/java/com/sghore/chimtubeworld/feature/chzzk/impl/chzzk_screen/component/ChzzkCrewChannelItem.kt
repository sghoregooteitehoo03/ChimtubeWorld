package com.sghore.chimtubeworld.feature.chzzk.impl.chzzk_screen.component

import android.content.res.Configuration
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.sghore.chimtubeworld.core.designsystem.theme.ChimtubeWorldTheme
import com.sghore.chimtubeworld.core.model.Channel
import com.sghore.chimtubeworld.core.designsystem.R as DesignSystemR

@Composable
internal fun ChzzkCrewChannelItem(
    channel: Channel?,
    onClick: (Channel) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable {
                onClick(channel!!)
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(80.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            AsyncImage(
                model = channel?.image,
                contentDescription = channel?.id,
                modifier = Modifier
                    .fillMaxSize()
                    .aspectRatio(1f)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            if (channel?.isOnline == true) {
                Canvas(modifier = Modifier.size(20.dp), onDraw = {
                    drawCircle(
                        color = Color.Green,
                        center = center,
                        radius = size.minDimension / 2f
                    )
                })
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = channel?.name ?: "",
            color = colorResource(id = DesignSystemR.color.item_color),
            fontSize = 18.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = channel?.explains?.get(0) ?: "",
            color = colorResource(id = DesignSystemR.color.default_text_color),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ChzzkCrewChannelItemPreview() {
    ChimtubeWorldTheme(darkTheme = isSystemInDarkTheme()) {
        ChzzkCrewChannelItem(
            channel = Channel(
                id = "preview", name = "침착맨", explains = arrayOf("준회원", "590000"),
                url = "", image = "", type = 0, isOnline = true
            ),
            onClick = {}
        )
    }
}
