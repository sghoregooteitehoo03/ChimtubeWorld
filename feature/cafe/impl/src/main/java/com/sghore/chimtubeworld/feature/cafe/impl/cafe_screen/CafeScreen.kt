package com.sghore.chimtubeworld.feature.cafe.impl.cafe_screen

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.sghore.chimtubeworld.core.designsystem.component.ScreenScaffold
import com.sghore.chimtubeworld.core.designsystem.theme.ChimtubeWorldTheme
import com.sghore.chimtubeworld.core.model.CafeCategory
import com.sghore.chimtubeworld.core.model.Channel
import com.sghore.chimtubeworld.core.model.Post
import com.sghore.chimtubeworld.feature.cafe.impl.R
import com.sghore.chimtubeworld.feature.cafe.impl.cafe_screen.component.CafePostItem
import com.sghore.chimtubeworld.feature.cafe.impl.cafe_screen.component.CafeTopItem
import kotlinx.coroutines.flow.flowOf
import me.onebone.toolbar.CollapsingToolbarScaffold
import me.onebone.toolbar.ScrollStrategy
import me.onebone.toolbar.rememberCollapsingToolbarScaffoldState
import com.sghore.chimtubeworld.core.designsystem.R as DesignSystemR

@Composable
internal fun CafeScreen(
    uiState: CafeUiState,
    onEvent: (CafeUiEvent) -> Unit
) {
    ScreenScaffold(title = stringResource(R.string.cafe_screen_title)) {
        val collapsingState = rememberCollapsingToolbarScaffoldState() // collapsing 상태
        val scrollState = rememberScrollState()
        val isEnabled by remember {
            derivedStateOf {
                collapsingState.toolbarState.progress != 0f
            }
        }

        Surface(
            color = colorResource(id = DesignSystemR.color.default_background_color),
            tonalElevation = 0.dp
        ) {
            CollapsingToolbarScaffold(
                modifier = Modifier
                    .fillMaxSize(),
                state = collapsingState,
                toolbarModifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(
                        state = scrollState,
                        enabled = isEnabled
                    )
                    .background(
                        color = if (isEnabled) {
                            colorResource(id = DesignSystemR.color.default_background_color)
                        } else {
                            colorResource(id = DesignSystemR.color.gray_bright_night)
                        }
                    ),
                scrollStrategy = ScrollStrategy.ExitUntilCollapsed,
                toolbar = {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    )
                    CafeTopItem(
                        cafeInfo = uiState.cafeInfo,
                        categoryList = cafeCategories(),
                        selectedCategoryId = uiState.cafeCategoryId,
                        modifier = Modifier
                            .parallax(1f)
                            .fillMaxWidth(),
                        onCafeBannerClick = { onEvent(CafeUiEvent.BannerClicked(it)) },
                        onChimhahaButtonClick = { onEvent(CafeUiEvent.ChimhahaClicked) },
                        onCafeCategoryClick = { onEvent(CafeUiEvent.CategoryClicked(it)) }
                    )
                }
            ) {
                val postList: LazyPagingItems<Post>? = uiState.cafePosts
                    ?.collectAsLazyPagingItems() // 게시글 리스트

                LazyColumn {
                    if (uiState.hasLoadError) {
                        item {
                            Text(
                                text = stringResource(R.string.cafe_load_error),
                                color = colorResource(DesignSystemR.color.item_color),
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    postList?.let {
                        items(count = it.itemCount) { index ->
                            val post = it[index]
                            val isRead =
                                (post?.isRead == true) || (uiState.readHistory[post?.id] == true)

                            CafePostItem(
                                post = post,
                                isRead = isRead,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 12.dp, end = 12.dp),
                                onClick = { onEvent(CafeUiEvent.PostClicked(it!!)) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun cafeCategories(): List<CafeCategory> = listOf(
    CafeCategory(stringResource(R.string.cafe_category_all), 0),
    CafeCategory(stringResource(R.string.cafe_category_notice), 5),
    CafeCategory(stringResource(R.string.cafe_category_exclusive), 42),
    CafeCategory(stringResource(R.string.cafe_category_gallery), 33),
    CafeCategory(stringResource(R.string.cafe_category_story), 1),
    CafeCategory(stringResource(R.string.cafe_category_fanart), 2),
    CafeCategory(stringResource(R.string.cafe_category_meme), 6),
    CafeCategory(stringResource(R.string.cafe_category_video), 55),
    CafeCategory(stringResource(R.string.cafe_category_request), 4),
    CafeCategory(stringResource(R.string.cafe_category_find), 56)
)

@Preview(name = "Screen Light", showBackground = true)
@Preview(name = "Screen Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CafeScreenPreview() {
    val posts = remember {
        flowOf(PagingData.from(listOf(
            Post(
                id = 0, title = "[잡담] 최고민수야 고맙다", userName = "sghore",
                postDate = "2021.12.07", postImage = "", url = ""
            )
        )))
    }
    val cafe = Channel(
        id = "cafe", name = "§ 침투부 카페 §", explains = arrayOf("75,563"),
        url = "", image = "", type = 0
    )

    ChimtubeWorldTheme(darkTheme = isSystemInDarkTheme()) {
        CafeScreen(uiState = CafeUiState(cafeInfo = cafe, cafePosts = posts), onEvent = {})
    }
}

@Preview(name = "Error Light", showBackground = true)
@Preview(name = "Error Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CafeScreenErrorPreview() {
    ChimtubeWorldTheme(darkTheme = isSystemInDarkTheme()) {
        CafeScreen(uiState = CafeUiState(hasLoadError = true), onEvent = {})
    }
}
