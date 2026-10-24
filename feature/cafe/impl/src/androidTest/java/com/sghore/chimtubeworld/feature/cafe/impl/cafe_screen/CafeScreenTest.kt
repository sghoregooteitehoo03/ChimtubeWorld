package com.sghore.chimtubeworld.feature.cafe.impl.cafe_screen

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.paging.PagingData
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.sghore.chimtubeworld.core.designsystem.theme.ChimtubeWorldTheme
import com.sghore.chimtubeworld.core.model.Channel
import com.sghore.chimtubeworld.core.model.Post
import com.sghore.chimtubeworld.feature.cafe.impl.R
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CafeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun setScreen(state: CafeUiState, onEvent: (CafeUiEvent) -> Unit = {}) {
        composeTestRule.setContent {
            ChimtubeWorldTheme {
                CafeScreen(uiState = state, onEvent = onEvent)
            }
        }
    }

    @Test
    fun 헤더와_카페_정보_침하하_버튼을_표시한다() {
        val cafe = cafe()
        setScreen(CafeUiState(cafeInfo = cafe))

        composeTestRule.onNodeWithText(string(R.string.cafe_screen_title)).assertIsDisplayed()
        composeTestRule.onNodeWithText(string(R.string.cafe_title)).assertIsDisplayed()
        composeTestRule.onNodeWithText(string(R.string.cafe_explanation)).assertIsDisplayed()
        composeTestRule.onNodeWithText(cafe.name).assertIsDisplayed()
        composeTestRule.onNodeWithText(cafe.explains[0]).assertIsDisplayed()
        composeTestRule.onNodeWithText(string(R.string.cafe_chimhaha)).assertIsDisplayed()
        composeTestRule.onNodeWithText(string(R.string.cafe_categories_title)).assertIsDisplayed()
        composeTestRule.onNodeWithText(string(R.string.cafe_load_error)).assertDoesNotExist()
    }

    @Test
    fun 모든_카테고리를_표시하고_선택된_전체를_제외한_클릭_ID를_전달한다() {
        val events = mutableListOf<CafeUiEvent>()
        val categories = listOf(
            R.string.cafe_category_all to 0,
            R.string.cafe_category_notice to 5,
            R.string.cafe_category_exclusive to 42,
            R.string.cafe_category_gallery to 33,
            R.string.cafe_category_story to 1,
            R.string.cafe_category_fanart to 2,
            R.string.cafe_category_meme to 6,
            R.string.cafe_category_video to 55,
            R.string.cafe_category_request to 4,
            R.string.cafe_category_find to 56
        )
        setScreen(CafeUiState(cafeCategoryId = 0)) { events += it }

        categories.forEachIndexed { index, (label, _) ->
            composeTestRule.onNode(
                SemanticsMatcher.keyIsDefined(SemanticsProperties.HorizontalScrollAxisRange)
            ).performScrollToNode(hasText(string(label)))
            composeTestRule.onNodeWithText(string(label)).assertIsDisplayed().performClick()

            // Screen에 주입한 선택 상태는 callback만으로 변경되지 않는다.
            composeTestRule.runOnIdle {
                assertEquals(
                    categories.take(index + 1).drop(1).map { CafeUiEvent.CategoryClicked(it.second) },
                    events
                )
            }
        }
    }

    @Test
    fun 선택된_공지_카테고리는_다시_요청하지_않고_전체_클릭을_전달한다() {
        val events = mutableListOf<CafeUiEvent>()
        setScreen(CafeUiState(cafeCategoryId = 5)) { events += it }

        composeTestRule.onNodeWithText(string(R.string.cafe_category_notice)).performClick()
        composeTestRule.onNodeWithText(string(R.string.cafe_category_all)).performClick()

        composeTestRule.runOnIdle {
            assertEquals(listOf(CafeUiEvent.CategoryClicked(0)), events)
        }
    }

    @Test
    fun 배너와_침하하_클릭은_각_event를_전달한다() {
        val cafe = cafe()
        val events = mutableListOf<CafeUiEvent>()
        setScreen(CafeUiState(cafeInfo = cafe)) { events += it }

        composeTestRule.onNodeWithText(cafe.name).performClick()
        composeTestRule.onNodeWithText(string(R.string.cafe_chimhaha)).performClick()

        composeTestRule.runOnIdle {
            assertEquals(listOf(CafeUiEvent.BannerClicked(cafe.url), CafeUiEvent.ChimhahaClicked), events)
        }
    }

    @Test
    fun Paging_게시글을_표시하고_클릭한_게시글을_전달한다() {
        val post = Post(
            id = 731, title = "표시할 게시글", userName = "작성자", postDate = "2026.10.23",
            postImage = "", url = "https://cafe.naver.com/zilioner/731"
        )
        val events = mutableListOf<CafeUiEvent>()
        setScreen(CafeUiState(cafePosts = flowOf(PagingData.from(listOf(post))))) { events += it }

        composeTestRule.onNodeWithText(post.title).performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText(string(R.string.cafe_post_metadata, post.userName, post.postDate))
            .assertIsDisplayed()
        composeTestRule.onNodeWithText(post.title).performClick()

        composeTestRule.runOnIdle {
            assertEquals(listOf(CafeUiEvent.PostClicked(post)), events)
        }
    }

    @Test
    fun 조회_실패는_기존_오류_문구와_헤더를_표시한다() {
        setScreen(CafeUiState(hasLoadError = true))

        composeTestRule.onNodeWithText(string(R.string.cafe_load_error)).assertIsDisplayed()
        composeTestRule.onNodeWithText(string(R.string.cafe_title)).assertIsDisplayed()
    }

    private fun cafe(): Channel = Channel(
        id = "cafe", name = "표시할 카페", explains = arrayOf("75,563"),
        url = "https://cafe.naver.com/zilioner", image = "", type = 0
    )

    private fun string(id: Int, vararg args: Any): String =
        InstrumentationRegistry.getInstrumentation().targetContext.getString(id, *args)
}
