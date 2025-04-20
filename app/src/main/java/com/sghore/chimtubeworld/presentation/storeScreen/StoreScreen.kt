package com.sghore.chimtubeworld.presentation.storeScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sghore.chimtubeworld.data.model.GoodsChannelInfo
import com.sghore.chimtubeworld.presentation.TitleTextWithExplain

@Composable
fun StoreScreen(
    uiState: StoreScreenState,
    onClickGoods: (GoodsChannelInfo) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TitleTextWithExplain(
            title = "Goods",
            explain = "",
            modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 12.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        ) {
            StoreInfoCategoryItem(
                modifier = Modifier
                    .weight(0.5f),
                storeInfo = uiState.storeInfoList[0],
                onClick = onClickGoods
            )
            Spacer(modifier = Modifier.width(12.dp))
            StoreInfoCategoryItem(
                modifier = Modifier
                    .weight(0.5f),
                storeInfo = uiState.storeInfoList[1],
                onClick = onClickGoods
            )
        }
    }
}

@Composable
private fun StoreInfoCategoryItem(
    storeInfo: GoodsChannelInfo,
    onClick: (GoodsChannelInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.clickable { onClick(storeInfo) },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = storeInfo.channelImage),
            contentDescription = storeInfo.channelName,
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