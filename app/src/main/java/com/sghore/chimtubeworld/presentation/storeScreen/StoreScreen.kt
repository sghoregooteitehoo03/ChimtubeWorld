package com.sghore.chimtubeworld.presentation.storeScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.sghore.chimtubeworld.presentation.TitleTextWithExplain
import com.sghore.chimtubeworld.presentation.twitchScreen.TwitchViewModel
import kotlinx.coroutines.flow.collect

@Composable
fun StoreScreen(
    viewModel: StoreViewModel,
    onCategoryClick: (String) -> Unit,
    onGoodsClick: (List<Goods>, Int) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        StoreList(
            viewModel = viewModel,
            onCategoryClick = onCategoryClick,
            onGoodsClick = onGoodsClick
        )
        LoadingView(
            viewModel = viewModel,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun LoadingView(
    viewModel: StoreViewModel,
    modifier: Modifier = Modifier
) {
    if (viewModel.state.isLoading) {
        CircularProgressIndicator(
            modifier = modifier,
            color = colorResource(id = R.color.item_color)
        )
    }
}

@Composable
fun StoreList(
    viewModel: StoreViewModel,
    onCategoryClick: (String) -> Unit,
    onGoodsClick: (List<Goods>, Int) -> Unit
) {
    LaunchedEffect(key1 = viewModel.state.goodsListFlow) {
        viewModel.state.goodsListFlow?.collect {
            viewModel.setGoodsList(it)
        }
    }

    LazyColumn {
        if (!viewModel.state.isLoading) {
            val spanCount = 3
            val itemCount = if (viewModel.state.goodsList.size % spanCount == 0) {
                viewModel.state.goodsList.size / spanCount
            } else {
                viewModel.state.goodsList.size / spanCount + 1
            }

            item {
                TitleTextWithExplain(
                    title = "Goods",
                    explain = "",
                    modifier = Modifier
                        .padding(top = 12.dp, start = 12.dp, end = 12.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                StoreInfoCategoryList(
                    storeInfoList = viewModel.state.storeInfoList,
                    selectedStoreUrl = viewModel.state.selectedStoreUrl,
                    onClick = onCategoryClick
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            items(itemCount) { index ->
                GoodsRow(
                    rowIndex = index,
                    goodsList = viewModel.state.goodsList,
                    spanCount = spanCount,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp, end = 12.dp),
                    onClick = onGoodsClick
                )
            }
        }
    }
}

@Composable
fun StoreInfoCategoryList(
    storeInfoList: List<Channel>,
    selectedStoreUrl: String,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit = {}
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
    onClick: (String) -> Unit = {}
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
        modifier = Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) {
            onClick(storeInfo.url)
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
fun GoodsRow(
    rowIndex: Int,
    goodsList: List<Goods>,
    spanCount: Int,
    modifier: Modifier = Modifier,
    onClick: (List<Goods>, Int) -> Unit = { _goodsList, _goodsIndex -> }
) {
    Column(
        modifier = modifier
    ) {
        Row {
            for (index in 0 until spanCount) {
                if (goodsList.size >= rowIndex * spanCount + (index + 1)) {
                    GoodsItem(
                        goods = goodsList[rowIndex * spanCount + index],
                        modifier = Modifier.weight(1f),
                        onClick = {
                            onClick(
                                goodsList,
                                goodsList.indexOf(it)
                            )
                        }
                    )

                    if (index != spanCount - 1) {
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
    }
}

@Composable
fun GoodsItem(
    goods: Goods,
    modifier: Modifier = Modifier,
    onClick: (Goods) -> Unit = {}
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
                name = "M드로메다",
                explains = arrayOf(),
                url = "",
                image = "",
                type = 0
            ),
            ""
        )
    }
}