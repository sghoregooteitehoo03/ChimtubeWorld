package com.sghore.chimtubeworld.feature.webtoon.impl.webtoon_screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasScrollToIndexAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.sghore.chimtubeworld.core.designsystem.theme.ChimtubeWorldTheme
import com.sghore.chimtubeworld.core.model.Channel
import com.sghore.chimtubeworld.feature.webtoon.impl.R
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WebtoonScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun setScreen(state: WebtoonUiState, onEvent: (WebtoonUiEvent) -> Unit = {}) {
        composeTestRule.setContent {
            ChimtubeWorldTheme {
                WebtoonScreen(uiState = state, onEvent = onEvent)
            }
        }
    }

    @Test
    fun 상태의_작품_목록과_제목_설명을_표시한다() {
        val webtoons = webtoons()
        setScreen(WebtoonUiState(webtoonInfos = webtoons))

        composeTestRule.onNodeWithText(string(R.string.webtoon_screen_title)).assertIsDisplayed()
        composeTestRule.onNodeWithText(string(R.string.webtoon_title)).assertIsDisplayed()
        composeTestRule.onNodeWithText(string(R.string.webtoon_explanation)).assertIsDisplayed()

        webtoons.forEach { webtoon ->
            composeTestRule.onNode(hasScrollToIndexAction()).performScrollToNode(hasText(webtoon.name))
            composeTestRule.onNodeWithText(webtoon.name).assertIsDisplayed()
            composeTestRule.onNodeWithText(webtoon.explains[0]).assertIsDisplayed()
            composeTestRule.onNodeWithContentDescription(
                string(R.string.webtoon_cover_content_description, webtoon.id)
            ).assertIsDisplayed()
        }
    }

    @Test
    fun 각_작품_클릭은_선택한_값의_event만_전달한다() {
        val webtoons = webtoons()
        val events = mutableListOf<WebtoonUiEvent>()
        setScreen(WebtoonUiState(webtoonInfos = webtoons)) { events += it }

        webtoons.forEachIndexed { index, webtoon ->
            composeTestRule.onNode(hasScrollToIndexAction()).performScrollToNode(hasText(webtoon.name))
            composeTestRule.onNodeWithText(webtoon.name).performClick()

            composeTestRule.runOnIdle {
                assertEquals(
                    webtoons.take(index + 1).map { WebtoonUiEvent.WebtoonClicked(it) },
                    events
                )
            }
        }
    }

    @Test
    fun 빈_목록에서도_제목과_설명을_표시한다() {
        setScreen(WebtoonUiState(webtoonInfos = emptyList()))

        composeTestRule.onNodeWithText(string(R.string.webtoon_screen_title)).assertIsDisplayed()
        composeTestRule.onNodeWithText(string(R.string.webtoon_title)).assertIsDisplayed()
        composeTestRule.onNodeWithText(string(R.string.webtoon_explanation)).assertIsDisplayed()
        composeTestRule.onNodeWithText("이말년씨리즈").assertDoesNotExist()
    }

    private fun webtoons(): List<Channel> = List(5) { index ->
        Channel(
            id = "fixture-$index",
            name = "표시할 작품 $index",
            explains = arrayOf("작품 연도 $index"),
            url = "https://example.com/webtoon/$index",
            image = R.drawable.webtoon_cover_1.toString(),
            type = 0
        )
    }

    private fun string(id: Int, vararg args: Any): String =
        InstrumentationRegistry.getInstrumentation().targetContext.getString(id, *args)
}
