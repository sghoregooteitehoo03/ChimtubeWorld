package com.sghore.chimtubeworld.presentation.cafeScreen

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.compose.rememberImagePainter
import com.sghore.chimtubeworld.R
import com.sghore.chimtubeworld.data.model.CafeCategory
import com.sghore.chimtubeworld.data.model.Channel
import com.sghore.chimtubeworld.data.model.Post
import com.sghore.chimtubeworld.presentation.TitleTextWithExplain
import me.onebone.toolbar.CollapsingToolbarScaffold
import me.onebone.toolbar.ScrollStrategy
import me.onebone.toolbar.rememberCollapsingToolbarScaffoldState

// Todo:
//  . Collapsing 시 렉 걸리는 버그 고치기 ㅁ
//  . 카페 글을 본 후 네비게이션 한 다음 다시 돌아오면 히스토리가 적용 안되는 버그 수정 X

@Composable
fun CafeScreen(
    viewModel: CafeViewModel,
    onCafeBannerClick: (Channel?) -> Unit,
    onCafeCategoryClick: (CafeCategory) -> Unit,
    onCafePostClick: (Post?) -> Unit
) {
    val collapsingState = rememberCollapsingToolbarScaffoldState() // collapsing 상태
    val scrollState = rememberScrollState()

    Surface {
        CollapsingToolbarScaffold(
            modifier = Modifier
                .fillMaxSize(),
            state = collapsingState,
            toolbarModifier = Modifier
                .fillMaxWidth()
                .verticalScroll(
                    state = scrollState,
                    enabled = collapsingState.toolbarState.progress != 0f
                )
                .background(
                    color = if (collapsingState.toolbarState.progress != 0f) {
                        colorResource(id = R.color.default_background_color)
                    } else {
                        colorResource(id = R.color.gray_bright_night)
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
                    viewModel = viewModel,
                    modifier = Modifier
                        .parallax(1f)
                        .fillMaxWidth(),
                    onCafeBannerClick = onCafeBannerClick,
                    onCafeCategoryClick = onCafeCategoryClick
                )
            }
        ) {
            CafePostList(
                viewModel = viewModel,
                onCafePostClick = onCafePostClick
            )
        }
    }
}

@Composable
fun CafeTopItem(
    viewModel: CafeViewModel,
    modifier: Modifier = Modifier,
    onCafeBannerClick: (Channel?) -> Unit,
    onCafeCategoryClick: (CafeCategory) -> Unit
) {
    // 카테고리 리스트
    val categoryList = listOf(
        CafeCategory("전체", -1),
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

    Column(
        modifier = modifier
    ) {
        TitleTextWithExplain(
            title = "Community",
            explain = "침착맨 팬카페",
            modifier = Modifier.padding(top = 12.dp, start = 12.dp, end = 12.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        CafeInfoBanner(
            cafeInfo = viewModel.state.cafeInfo,
            modifier = Modifier.padding(start = 12.dp, end = 12.dp),
            onClick = onCafeBannerClick
        )

        Spacer(modifier = Modifier.height(24.dp))
        TitleTextWithExplain(
            title = "게시글",
            explain = "",
            modifier = Modifier.padding(start = 12.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        CafeCategoryList(
            categoryList = categoryList,
            selectedCategoryId = viewModel.state.cafeCategoryId,
            onClick = {
                onCafeCategoryClick(it)
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun CafeInfoBanner(
    cafeInfo: Channel?,
    modifier: Modifier = Modifier,
    onClick: (Channel?) -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(218.dp)
            .background(
                color = colorResource(id = R.color.cafe_banner),
                shape = RoundedCornerShape(12.dp)
            )
            .clip(
                RoundedCornerShape(12.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple()
            ) {
                onClick(cafeInfo)
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
            Image(
                painter = rememberImagePainter(data = cafeInfo?.image),
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
                color = colorResource(id = R.color.gray_darker),
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
    modifier: Modifier = Modifier,
    onClick: (CafeCategory) -> Unit = {}
) {
    LazyRow(
        modifier = modifier
    ) {
        item {
            Spacer(modifier = Modifier.width(12.dp))
        }
        items(categoryList) { category ->
            val textColor = if (category.categoryId == selectedCategoryId) {
                colorResource(id = R.color.item_color)
            } else {
                colorResource(id = R.color.default_text_color)
            }
            val backgroundColor = if (category.categoryId == selectedCategoryId) {
                colorResource(id = R.color.gray_night)
            } else {
                colorResource(id = R.color.item_reverse_color)
            }

            Box(
                modifier = Modifier
                    .background(
                        color = backgroundColor,
                        shape = RoundedCornerShape(22.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = colorResource(id = R.color.gray_night),
                        shape = RoundedCornerShape(22.dp)
                    )
                    .clip(RoundedCornerShape(22.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(
                            bounded = true,
                            color = colorResource(id = R.color.item_color)
                        )
                    ) {
                        onClick(category)
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
fun CafePostList(
    viewModel: CafeViewModel,
    onCafePostClick: (Post?) -> Unit
) {
    // 게시글 리스트
    val postList: LazyPagingItems<Post>? =
        viewModel.state.cafePosts?.collectAsLazyPagingItems()
    val readState = remember { mutableStateMapOf<Int, Boolean>() } // 게시글 아이템 읽음 여부

    LazyColumn {
        postList?.let {
            items(items = it) { post ->
                val isRead = (post?.isRead ?: false) || (readState[post?.id] ?: false)
                CafePostItem(
                    post = post,
                    isRead = isRead,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp, end = 12.dp),
                    onClick = { clickPost ->
                        if (!isRead) {
                            readState[clickPost!!.id] = true
                        }
                        onCafePostClick(clickPost)
                    }
                )
            }
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
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    bounded = true,
                    color = colorResource(id = R.color.item_color)
                )
            ) {
                onClick(post)
            }
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
                    color = colorResource(id = R.color.item_color),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${post?.userName} | ${post?.postDate}",
                    color = colorResource(id = R.color.default_text_color),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            if ((post?.postImage ?: "").isNotEmpty()) {
                Image(
                    painter = rememberImagePainter(data = post?.postImage),
                    contentDescription = post?.postImage,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(68.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }
        }
        Divider(
            modifier = modifier
                .align(Alignment.BottomStart),
            color = colorResource(id = R.color.gray_bright_night)
        )
    }
}

@Preview
@Composable
fun CafeInfoBannerPreview() {
    MaterialTheme {
        CafeInfoBanner(
            cafeInfo = Channel(
                "",
                "§ 침투부 카페 §",
                arrayOf("75,563"),
                "",
                "",
                type = 0
            )
        )
    }
}

@Preview
@Composable
fun CafeCategoryListPreview() {
    MaterialTheme {
        val list = listOf(
            CafeCategory("전체", -1),
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
            selectedCategoryId = -1
        )
    }
}

@Preview
@Composable
fun CafePostItemPreview() {
    MaterialTheme {
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

