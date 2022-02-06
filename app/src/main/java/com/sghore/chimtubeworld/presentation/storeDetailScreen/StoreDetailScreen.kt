package com.sghore.chimtubeworld.presentation.storeDetailScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.sghore.chimtubeworld.R
import com.sghore.chimtubeworld.data.model.Goods

@Composable
fun StoreDetailScreen(
    uiState: StoreDetailScreenState,
    goods: Goods?,
    onPreviewImageClick: (String) -> Unit,
    onActionClick: (String) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box {
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        color = colorResource(id = R.color.default_background_color)
                    )
                    .padding(12.dp)
            ) {
                Image(
                    painter = rememberImagePainter(data = uiState.selectedImage),
                    contentDescription = "Goods",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(240.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))

                PreviewImageList(
                    imageList = uiState.previewImages,
                    selectedImage = uiState.selectedImage,
                    modifier = Modifier.width(240.dp),
                    onClick = onPreviewImageClick
                )
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (uiState.isLoading) {
                        ""
                    } else {
                        goods?.title ?: ""
                    },
                    color = colorResource(id = R.color.default_text_color),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.width(240.dp)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (uiState.isLoading) {
                        ""
                    } else {
                        goods?.price ?: ""
                    },
                    color = colorResource(id = R.color.item_color),
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .width(240.dp)
                )
            }

            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = colorResource(id = R.color.item_color)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "바로가기",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple()
                ) {
                    onActionClick(goods?.url ?: "")
                }
        )
    }
}

@Composable
fun PreviewImageList(
    imageList: List<String>,
    selectedImage: String,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit = {}
) {
    LazyRow(
        modifier = modifier
    ) {
        items(imageList) { image ->
            val selectedColor = if (image == selectedImage) {
                colorResource(id = android.R.color.transparent)
            } else {
                colorResource(id = R.color.black_alpha_50)
            }

            Image(
                painter = rememberImagePainter(data = image),
                contentDescription = "GoodsPreview",
                contentScale = ContentScale.Crop,
                colorFilter = ColorFilter.tint(
                    color = selectedColor,
                    blendMode = BlendMode.SrcOver
                ),
                modifier = Modifier
                    .size(60.dp)
                    .clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = null
                    ) {
                        onClick(image)
                    }
            )
            Spacer(modifier = Modifier.width(6.dp))
        }
    }
}