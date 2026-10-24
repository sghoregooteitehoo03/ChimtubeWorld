package com.sghore.chimtubeworld.feature.chzzk.impl.chzzk_screen.component

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.sghore.chimtubeworld.core.common.parseFollowText
import com.sghore.chimtubeworld.core.designsystem.theme.ChimtubeWorldTheme
import com.sghore.chimtubeworld.core.model.Channel
import com.sghore.chimtubeworld.feature.chzzk.impl.R
import com.sghore.chimtubeworld.core.designsystem.R as DesignSystemR

@Composable
internal fun MainChannelInfo(
    mainChannel: Channel?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorFilter = if (mainChannel?.isOnline == true) {
        null
    } else {
        ColorFilter.tint(
            colorResource(id = DesignSystemR.color.black_alpha_50),
            blendMode = BlendMode.SrcOver
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
    ) {
        AsyncImage(
            model = mainChannel?.thumbnailImage,
            contentDescription = mainChannel?.id,
            modifier = Modifier
                .fillMaxWidth()
                .height(208.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop,
            colorFilter = colorFilter
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row {
            Box {
                AsyncImage(
                    model = mainChannel?.image,
                    contentDescription = stringResource(R.string.chzzk_main_profile_description),
                    modifier = Modifier
                        .size(64.dp)
                        .border(
                            width = 2.dp,
                            color = if (mainChannel?.isOnline == true)
                                colorResource(id = DesignSystemR.color.green_online_color)
                            else
                                colorResource(id = android.R.color.darker_gray),
                            shape = CircleShape
                        )
                        .padding(4.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                ) {
                    Spacer(modifier = Modifier.height(50.dp))
                    if (mainChannel?.isOnline == true) {
                        Text(
                            text = stringResource(R.string.chzzk_live),
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .border(
                                    width = 2.dp,
                                    color = colorResource(id = DesignSystemR.color.item_reverse_color),
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .background(
                                    color = colorResource(id = DesignSystemR.color.red_online_color),
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(start = 4.dp, end = 4.dp, top = 2.dp, bottom = 2.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.height(60.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = mainChannel?.name ?: "",
                    color = colorResource(id = DesignSystemR.color.item_color),
                    fontSize = 18.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = parseFollowText(mainChannel?.explains?.get(1)),
                    color = colorResource(id = DesignSystemR.color.default_text_color),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun MainChannelInfoPreview() {
    ChimtubeWorldTheme(darkTheme = isSystemInDarkTheme()) {
        MainChannelInfo(
            mainChannel = Channel(
                id = "preview", name = "침착맨", explains = arrayOf("준회원", "590000"),
                url = "", image = "", type = 0, isOnline = true
            ),
            onClick = {}
        )
    }
}
