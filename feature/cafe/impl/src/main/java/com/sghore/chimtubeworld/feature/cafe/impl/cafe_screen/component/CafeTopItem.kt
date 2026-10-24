package com.sghore.chimtubeworld.feature.cafe.impl.cafe_screen.component

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sghore.chimtubeworld.core.designsystem.component.TitleTextWithExplain
import com.sghore.chimtubeworld.core.designsystem.theme.ChimtubeWorldTheme
import com.sghore.chimtubeworld.core.model.CafeCategory
import com.sghore.chimtubeworld.core.model.Channel
import com.sghore.chimtubeworld.feature.cafe.impl.R

@Composable
internal fun CafeTopItem(
    cafeInfo: Channel?,
    categoryList: List<CafeCategory>,
    selectedCategoryId: Int,
    modifier: Modifier = Modifier,
    onCafeBannerClick: (String) -> Unit,
    onChimhahaButtonClick: () -> Unit,
    onCafeCategoryClick: (Int) -> Unit
) {
    Column(
        modifier = modifier
    ) {
        TitleTextWithExplain(
            title = stringResource(R.string.cafe_title),
            explain = stringResource(R.string.cafe_explanation),
            modifier = Modifier.padding(top = 12.dp, start = 12.dp, end = 12.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        CafeInfoBanner(
            cafeInfo = cafeInfo,
            modifier = Modifier.padding(start = 12.dp, end = 12.dp),
            onClick = onCafeBannerClick
        )
        Spacer(modifier = Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp)
                .background(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF4FA5AD)
                )
                .clip(RoundedCornerShape(8.dp))
                .clickable {
                    onChimhahaButtonClick()
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                text = stringResource(R.string.cafe_chimhaha),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        TitleTextWithExplain(
            title = stringResource(R.string.cafe_categories_title),
            explain = "",
            modifier = Modifier.padding(start = 12.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        CafeCategoryList(
            categoryList = categoryList,
            selectedCategoryId = selectedCategoryId,
            onClick = onCafeCategoryClick
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CafeTopItemPreview() {
    ChimtubeWorldTheme(darkTheme = isSystemInDarkTheme()) {
        CafeTopItem(
            cafeInfo = null,
            categoryList = listOf(CafeCategory(stringResource(R.string.cafe_category_all), 0)),
            selectedCategoryId = 0,
            onCafeBannerClick = {},
            onChimhahaButtonClick = {},
            onCafeCategoryClick = {}
        )
    }
}
