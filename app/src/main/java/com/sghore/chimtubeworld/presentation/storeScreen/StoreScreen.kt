package com.sghore.chimtubeworld.presentation.storeScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.sghore.chimtubeworld.R
import com.sghore.chimtubeworld.data.model.Goods
import com.sghore.chimtubeworld.data.model.GoodsChannelInfo
import com.sghore.chimtubeworld.presentation.PagingRowList
import com.sghore.chimtubeworld.presentation.TitleTextWithExplain

@Composable
fun StoreScreen(
    uiState: StoreScreenState,
    onCategoryClick: (ProductType) -> Unit,
    onGoodsClick: (List<Goods?>, Int) -> Unit
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
            val goodsList = when (uiState.selectedProductType) {
                ProductType.MarpleProduct -> {
                    uiState.marpleGoodsList?.collectAsLazyPagingItems()
                }

                ProductType.NaverProduct -> {
                    uiState.naverGoodsList?.collectAsLazyPagingItems()
                }
            }

            goodsList?.let {
                PagingRowList(
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
                            selectedProductType = uiState.selectedProductType,
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
                                    goodsList.itemSnapshotList,
                                    goodsList.itemSnapshotList.indexOf(it)
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
}

@Composable
fun StoreInfoCategoryList(
    storeInfoList: List<GoodsChannelInfo>,
    selectedProductType: ProductType,
    onClick: (ProductType) -> Unit,
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
                selectedProductType = selectedProductType,
                onClick = onClick
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

@Composable
fun StoreInfoCategoryItem(
    storeInfo: GoodsChannelInfo,
    selectedProductType: ProductType,
    onClick: (ProductType) -> Unit,
    modifier: Modifier = Modifier
) {
    val imageColor = if (selectedProductType == storeInfo.productType) {
        colorResource(id = android.R.color.transparent)
    } else {
        colorResource(id = R.color.black_alpha_50)
    }
    val textColor = if (selectedProductType == storeInfo.productType) {
        colorResource(id = R.color.item_color)
    } else {
        colorResource(id = R.color.default_text_color)
    }

    Column(
        modifier = modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) {
            if (storeInfo.productType != selectedProductType) { // 같은 것을 누를때는 동작 x
                onClick(storeInfo.productType)
            }
        }
    ) {
        Image(
            painter = painterResource(id = storeInfo.channelImage),
            contentDescription = storeInfo.channelName,
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
            text = storeInfo.channelName,
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
    goods: Goods?,
    onClick: (Goods?) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onClick(goods)
            }
    ) {
        AsyncImage(
            model = goods?.thumbnailImage,
            contentDescription = goods?.title ?: "",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .aspectRatio(1f)
        )
    }
}