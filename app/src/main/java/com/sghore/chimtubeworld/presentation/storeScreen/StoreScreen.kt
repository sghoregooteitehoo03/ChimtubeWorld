package com.sghore.chimtubeworld.presentation.storeScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.sghore.chimtubeworld.R
import com.sghore.chimtubeworld.data.model.Channel
import com.sghore.chimtubeworld.data.model.Goods
import com.sghore.chimtubeworld.presentation.RowList
import com.sghore.chimtubeworld.presentation.TitleTextWithExplain

@Composable
fun StoreScreen(
    uiState: StoreScreenState,
    onCategoryClick: (String) -> Unit,
    onGoodsClick: (List<Goods>, Int) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = colorResource(id = R.color.item_color)
            )
        } else {
            val goodsList = uiState.goodsList
            RowList(
                list = goodsList,
                spanCount = 3,
                contentPaddingValue = 0.dp,
                itemPaddingValue = 4.dp,
                headerItem = {
                    TitleTextWithExplain(
                        title = "Goods",
                        explain = "",
                        modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    StoreInfoCategoryList(
                        storeInfoList = uiState.storeInfoList,
                        selectedStoreUrl = uiState.selectedStoreUrl,
                        onClick = onCategoryClick
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                },
                listItem = { index, modifier ->
                    GoodsItem(
                        goods = goodsList[index],
                        modifier = modifier,
                        onClick = {
                            onGoodsClick(
                                goodsList,
                                goodsList.indexOf(it)
                            )
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp)
            )
        }
    }
}

@Composable
fun StoreInfoCategoryList(
    storeInfoList: List<Channel>,
    selectedStoreUrl: String,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
    ) {
        item {
            Spacer(modifier = Modifier.width(12.dp))
        }
        items(storeInfoList) { storeInfo ->
            StoreInfoCategoryItem(
                storeInfo = storeInfo,
                selectedStoreUrl = selectedStoreUrl,
                onClick = onClick
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

@Composable
fun StoreInfoCategoryItem(
    storeInfo: Channel,
    selectedStoreUrl: String,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val imageColor = if (selectedStoreUrl == storeInfo.url) {
        colorResource(id = android.R.color.transparent)
    } else {
        colorResource(id = R.color.black_alpha_50)
    }
    val textColor = if (selectedStoreUrl == storeInfo.url) {
        colorResource(id = R.color.item_color)
    } else {
        colorResource(id = R.color.default_text_color)
    }

    Column(
        modifier = modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) {
            if (storeInfo.url != selectedStoreUrl) { // ?????? ?????? ???????????? ?????? x
                onClick(storeInfo.url)
            }
        }
    ) {
        Image(
            painter = rememberImagePainter(data = storeInfo.image),
            contentDescription = storeInfo.name,
            contentScale = ContentScale.Crop,
            colorFilter = ColorFilter.tint(
                color = imageColor,
                blendMode = BlendMode.SrcOver
            ),
            modifier = Modifier
                .size(84.dp)
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
            text = storeInfo.name,
            color = textColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(84.dp)
        )
    }
}

@Composable
fun GoodsItem(
    goods: Goods,
    onClick: (Goods) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple()
            ) {
                onClick(goods)
            }
    ) {
        Image(
            painter = rememberImagePainter(data = goods.thumbnailImage),
            contentDescription = goods.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .aspectRatio(1f)
        )
    }
}

@Preview
@Composable
fun StoreInfoCategoryItemPreview() {
    MaterialTheme {
        StoreInfoCategoryItem(
            storeInfo = Channel(
                id = "",
                name = "M????????????",
                explains = arrayOf(),
                url = "",
                image = "",
                type = 0
            ),
            "",
            onClick = {}
        )
    }
}