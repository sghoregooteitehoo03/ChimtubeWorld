package com.sghore.chimtubeworld.presentation.cafeScreen

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.sghore.chimtubeworld.core.designsystem.component.TitleTextWithExplain
import com.sghore.chimtubeworld.core.designsystem.theme.ChimtubeWorldTheme
import com.sghore.chimtubeworld.core.model.CafeCategory
import com.sghore.chimtubeworld.core.model.Channel
import com.sghore.chimtubeworld.core.model.Post
import me.onebone.toolbar.CollapsingToolbarScaffold
import me.onebone.toolbar.ScrollStrategy
import me.onebone.toolbar.rememberCollapsingToolbarScaffoldState
import com.sghore.chimtubeworld.core.designsystem.R as DesignSystemR

@Composable
fun CafeScreen(
    uiState: CafeScreenState,
    onCafeBannerClick: (String) -> Unit,
    onChimhahaButtonClick: () -> Unit,
    onCafeCategoryClick: (Int) -> Unit,
    onCafePostClick: (Post?) -> Unit
) {
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
                    selectedCategoryId = uiState.cafeCategoryId,
                    modifier = Modifier
                        .parallax(1f)
                        .fillMaxWidth(),
                    onCafeBannerClick = onCafeBannerClick,
                    onChimhahaButtonClick = onChimhahaButtonClick,
                    onCafeCategoryClick = onCafeCategoryClick
                )
            }
        ) {
            val postList: LazyPagingItems<Post>? = uiState.cafePosts
                ?.collectAsLazyPagingItems() // 게시글 리스트

            LazyColumn {
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
                            onClick = onCafePostClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CafeTopItem(
    cafeInfo: Channel?,
    selectedCategoryId: Int,
    modifier: Modifier = Modifier,
    onCafeBannerClick: (String) -> Unit,
    onChimhahaButtonClick: () -> Unit,
    onCafeCategoryClick: (Int) -> Unit
) {
    // 카테고리 리스트
    val categoryList = remember {
        listOf(
            CafeCategory("전체", 0),
            CafeCategory("방송일정 및 공지", 5),
            CafeCategory("침착맨 전용", 42),
            CafeCategory("침착맨 갤러리", 33),
            CafeCategory("침착맨 이야기", 1),
            CafeCategory("팬아트", 2),
            CafeCategory("침착맨 짤", 6),
            CafeCategory("추천영상", 55),
            CafeCategory("해줘요", 4),
            CafeCategory("찾아주세요", 56)
        )
    }

    Column(
        modifier = modifier
    ) {
        TitleTextWithExplain(
            title = "Community",
            explain = "팬카페&침하하",
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
                text = "👍침하하 바로가기",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        TitleTextWithExplain(
            title = "비상대피소",
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

@Composable
fun CafeInfoBanner(
    cafeInfo: Channel?,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(218.dp)
            .background(
                color = colorResource(id = DesignSystemR.color.cafe_banner),
                shape = RoundedCornerShape(12.dp)
            )
            .clip(
                RoundedCornerShape(12.dp)
            )
            .clickable {
                onClick(cafeInfo?.url ?: "")
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .size(168.dp)
                .background(
                    color = Color.White,
                    shape = CircleShape
                )
                .clip(CircleShape),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = cafeInfo?.image,
                contentDescription = cafeInfo?.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = cafeInfo?.name ?: "",
                color = Color.Black,
                fontSize = 18.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = cafeInfo?.explains?.get(0) ?: "",
                color = colorResource(id = DesignSystemR.color.gray_darker),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun CafeCategoryList(
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

@Composable
fun CafePostItem(
    post: Post?,
    isRead: Boolean,
    modifier: Modifier = Modifier,
    onClick: (Post?) -> Unit = {}
) {
    val contentAlpha = if (isRead) {
        0.4f
    } else {
        1f
    }

    Box(
        modifier = Modifier
            .clickable { onClick(post) }
    ) {
        Row(
            modifier = modifier
                .height(80.dp)
                .padding(top = 6.dp, bottom = 6.dp)
                .alpha(contentAlpha)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.Top),
            ) {
                Text(
                    text = post?.title ?: "",
                    color = colorResource(id = DesignSystemR.color.item_color),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${post?.userName} | ${post?.postDate}",
                    color = colorResource(id = DesignSystemR.color.default_text_color),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            if ((post?.postImage ?: "").isNotEmpty()) {
                AsyncImage(
                    model = post?.postImage,
                    contentDescription = post?.postImage,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(68.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }
        }
        HorizontalDivider(
            modifier = modifier
                .align(Alignment.BottomStart),
            color = colorResource(id = DesignSystemR.color.gray_bright_night)
        )
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun CafeInfoBannerPreview() {
    ChimtubeWorldTheme(darkTheme = isSystemInDarkTheme()) {
        CafeInfoBanner(
            cafeInfo = Channel(
                id = "",
                name = "§ 침투부 카페 §",
                explains = arrayOf("75,563"),
                url = "",
                image = "",
                type = 0
            )
        )
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun CafeCategoryListPreview() {
    ChimtubeWorldTheme(darkTheme = isSystemInDarkTheme()) {
        val list = listOf(
            CafeCategory("전체", 0),
            CafeCategory("방송일정 및 공지", 5),
            CafeCategory("침착맨 전용", 42),
            CafeCategory("침착맨 갤러리", 33),
            CafeCategory("침착맨 이야기", 1),
            CafeCategory("팬아트", 2),
            CafeCategory("침착맨 짤", 6),
            CafeCategory("추천영상", 55),
            CafeCategory("해줘요", 4),
            CafeCategory("찾아주세요", 56)
        )

        CafeCategoryList(
            categoryList = list,
            selectedCategoryId = -1,
            onClick = {}
        )
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun CafePostItemPreview() {
    ChimtubeWorldTheme(darkTheme = isSystemInDarkTheme()) {
        CafePostItem(
            post = Post(
                id = 0,
                title = "[잡담] 최고민수야 고맙다",
                userName = "sghore",
                postDate = "2021.12.07",
                postImage = "",
                url = ""
            ),
            isRead = false,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp)
        )
    }
}

