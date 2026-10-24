package com.sghore.chimtubeworld.feature.cafe.impl.cafe_screen.component

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sghore.chimtubeworld.core.designsystem.theme.ChimtubeWorldTheme
import com.sghore.chimtubeworld.core.model.CafeCategory
import com.sghore.chimtubeworld.feature.cafe.impl.R
import com.sghore.chimtubeworld.core.designsystem.R as DesignSystemR

@Composable
internal fun CafeCategoryList(
    categoryList: List<CafeCategory>,
    selectedCategoryId: Int,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
    ) {
        item {
            Spacer(modifier = Modifier.width(12.dp))
        }
        items(categoryList) { category ->
            val textColor = if (category.categoryId == selectedCategoryId) {
                colorResource(id = DesignSystemR.color.item_color)
            } else {
                colorResource(id = DesignSystemR.color.default_text_color)
            }
            val backgroundColor = if (category.categoryId == selectedCategoryId) {
                colorResource(id = DesignSystemR.color.gray_night)
            } else {
                colorResource(id = DesignSystemR.color.item_reverse_color)
            }

            Box(
                modifier = Modifier
                    .background(
                        color = backgroundColor,
                        shape = RoundedCornerShape(22.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = colorResource(id = DesignSystemR.color.gray_night),
                        shape = RoundedCornerShape(22.dp)
                    )
                    .clip(RoundedCornerShape(22.dp))
                    .clickable {
                        if (category.categoryId != selectedCategoryId) {
                            onClick(category.categoryId)
                        }
                    }
                    .padding(top = 8.dp, bottom = 8.dp, start = 10.dp, end = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = category.category,
                    color = textColor
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
        }
    }
}

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CafeCategoryListPreview() {
    ChimtubeWorldTheme(darkTheme = isSystemInDarkTheme()) {
        CafeCategoryList(
            categoryList = listOf(
                CafeCategory(stringResource(R.string.cafe_category_all), 0),
                CafeCategory(stringResource(R.string.cafe_category_notice), 5)
            ),
            selectedCategoryId = 0,
            onClick = {}
        )
    }
}
