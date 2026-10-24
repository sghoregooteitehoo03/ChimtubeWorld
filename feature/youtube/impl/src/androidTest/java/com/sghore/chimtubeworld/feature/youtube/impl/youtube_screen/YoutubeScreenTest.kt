package com.sghore.chimtubeworld.feature.youtube.impl.youtube_screen

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.sghore.chimtubeworld.core.designsystem.theme.ChimtubeWorldTheme
import com.sghore.chimtubeworld.core.model.Channel
import com.sghore.chimtubeworld.feature.youtube.impl.R
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class YoutubeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun 메인과_서브_채널을_표시하고_선택한_채널을_전달한다() {
        val main = Channel(id = "main", name = "메인 채널", explains = arrayOf("메인 설명"), url = "", image = "", type = 0)
        val sub = Channel(id = "sub", name = "외부 채널", explains = arrayOf("외부 설명"), url = "", image = "", type = 1)
        val events = mutableListOf<YoutubeUiEvent>()
        composeTestRule.setContent {
            ChimtubeWorldTheme { YoutubeScreen(YoutubeUiState(channels = listOf(main, null, sub)), { events += it }) }
        }
        composeTestRule.onNodeWithText(string(R.string.youtube_title)).assertIsDisplayed()
        composeTestRule.onNodeWithText(main.name).performScrollTo().assertIsDisplayed().performClick()
        composeTestRule.onNodeWithText(sub.name).performScrollTo().assertIsDisplayed().performClick()
        composeTestRule.runOnIdle {
            assertEquals(listOf(YoutubeUiEvent.ChannelClicked(main), YoutubeUiEvent.ChannelClicked(sub)), events)
        }
    }

    @Test
    fun 오류_상태에서는_리소스_문구를_표시한다() {
        composeTestRule.setContent {
            ChimtubeWorldTheme { YoutubeScreen(YoutubeUiState(hasLoadError = true), {}) }
        }
        composeTestRule.onNodeWithText(string(R.string.youtube_load_error)).assertIsDisplayed()
    }

    @Test
    fun 로딩_상태에서는_진행_표시만_보인다() {
        composeTestRule.setContent {
            ChimtubeWorldTheme { YoutubeScreen(YoutubeUiState(isLoading = true), {}) }
        }
        composeTestRule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)).assertIsDisplayed()
        composeTestRule.onNodeWithText(string(R.string.youtube_title)).assertDoesNotExist()
    }

    private fun string(id: Int, vararg args: Any): String =
        InstrumentationRegistry.getInstrumentation().targetContext.getString(id, *args)
}
