package com.sghore.chimtubeworld.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sghore.chimtubeworld.R
import com.sghore.chimtubeworld.presentation.youtubeScreen.SubYoutubeChannelItem

@Composable
fun TitleTextWithExplain(
    title: String,
    explain: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Bottom
    ) {
        Text(
            text = title,
            color = colorResource(id = R.color.item_color),
            fontSize = 22.sp
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = explain,
            color = colorResource(id = R.color.default_text_color),
            fontSize = 16.sp
        )
    }
}

//@Composable
//fun <T> RowList(
//    rowIndex: Int,
//    list: List<T>,
//    spanCount: Int = 2,
//    paddingValue: Dp = 12.dp,
//    onClick: (T) -> Unit = {},
//    content: @Composable () -> Unit
//) {
//    Column(modifier = Modifier.fillMaxWidth()) {
//        Row {
//            for (index in 0 until spanCount) {
//                if (list.size >= rowIndex * spanCount + (index + 1)) {
//                    content()
//                    if (index != spanCount - 1) {
//                        Spacer(modifier = Modifier.width(paddingValue))
//                    }
//                } else {
//                    Spacer(modifier = Modifier.weight(1f))
//                }
//            }
//        }
//        Spacer(modifier = Modifier.height(paddingValue))
//    }
//}