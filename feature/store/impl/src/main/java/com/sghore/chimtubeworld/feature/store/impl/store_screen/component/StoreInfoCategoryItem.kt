package com.sghore.chimtubeworld.feature.store.impl.store_screen.component

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sghore.chimtubeworld.core.designsystem.theme.ChimtubeWorldTheme
import com.sghore.chimtubeworld.core.model.GoodsChannelInfo
import com.sghore.chimtubeworld.feature.store.impl.store_screen.StoreUiState

@Composable
internal fun StoreInfoCategoryItem(
    storeInfo: GoodsChannelInfo,
    contentDescription: String,
    onClick: (GoodsChannelInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.clickable { onClick(storeInfo) },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = storeInfo.channelImage),
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(
                    RoundedCornerShape(
                        topEnd = 28.dp,
                        bottomStart = 28.dp,
                        bottomEnd = 28.dp
                    )
                )
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = storeInfo.channelName,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            fontSize = 20.sp
        )
    }
}

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun StoreInfoCategoryItemPreview() {
    val storeInfo = StoreUiState().storeInfoList.first()

    ChimtubeWorldTheme(darkTheme = isSystemInDarkTheme()) {
        StoreInfoCategoryItem(
            storeInfo = storeInfo,
            contentDescription = storeInfo.channelName,
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}
