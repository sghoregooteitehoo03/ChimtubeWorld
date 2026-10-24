package com.sghore.chimtubeworld.feature.chzzk.impl.chzzk_screen

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.hasScrollToIndexAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.test.espresso.Espresso.pressBack
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.sghore.chimtubeworld.core.designsystem.theme.ChimtubeWorldTheme
import com.sghore.chimtubeworld.core.model.Channel
import com.sghore.chimtubeworld.feature.chzzk.impl.R
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChzzkScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun setScreen(state: ChzzkUiState, onEvent: (ChzzkUiEvent) -> Unit = {}) {
        composeTestRule.setContent {
            ChimtubeWorldTheme {
                ChzzkScreen(uiState = state, onEvent = onEvent)
            }
        }
    }

    @Test
    fun 로딩_중에는_진행_표시를_노출하고_채널과_오류를_감춘다() {
        setScreen(ChzzkUiState(isLoading = true))

        composeTestRule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)).assertIsDisplayed()
        composeTestRule.onNodeWithText(string(R.string.chzzk_title)).assertDoesNotExist()
        composeTestRule.onNodeWithText(string(R.string.chzzk_load_error)).assertDoesNotExist()
    }

    @Test
    fun 채널_목록과_헤더를_표시하고_크루_클릭을_전달한다() {
        val channels = channels()
        val crew = channels[1]
        val events = mutableListOf<ChzzkUiEvent>()
        setScreen(ChzzkUiState(mainChannelInfo = channels[0], channels = channels)) { events += it }

        composeTestRule.onNodeWithText(string(R.string.chzzk_screen_title)).assertIsDisplayed()
        composeTestRule.onNodeWithText(string(R.string.chzzk_title)).assertIsDisplayed()
        composeTestRule.onNodeWithText(string(R.string.chzzk_explanation)).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(string(R.string.chzzk_main_profile_description))
            .assertIsDisplayed()
        composeTestRule.onNodeWithText(string(R.string.chzzk_live)).assertIsDisplayed()
        composeTestRule.onNodeWithText(string(R.string.chzzk_crew_title)).assertIsDisplayed()
        composeTestRule.onNodeWithText(string(R.string.chzzk_crew_explanation)).assertIsDisplayed()
        composeTestRule.onNode(hasScrollToIndexAction()).performScrollToNode(hasText(crew.name))
        composeTestRule.onNodeWithText(crew.name).assertIsDisplayed().performClick()

        composeTestRule.runOnIdle {
            assertEquals(listOf(ChzzkUiEvent.CrewChannelClicked(crew)), events)
        }
    }

    @Test
    fun 메인_채널_클릭은_event를_전달하며_닫힌_상태에서는_다이얼로그를_표시하지_않는다() {
        val channels = channels()
        val events = mutableListOf<ChzzkUiEvent>()
        setScreen(ChzzkUiState(mainChannelInfo = channels[0], channels = channels)) { events += it }

        composeTestRule.onNodeWithContentDescription(string(R.string.chzzk_main_profile_description)).performClick()

        composeTestRule.runOnIdle {
            assertEquals(listOf(ChzzkUiEvent.MainChannelClicked), events)
        }
        composeTestRule.onNode(isDialog()).assertDoesNotExist()
        composeTestRule.onNodeWithText(string(R.string.chzzk_watch_youtube)).assertDoesNotExist()
        composeTestRule.onNodeWithText(string(R.string.chzzk_watch_chzzk)).assertDoesNotExist()
    }

    @Test
    fun 열린_다이얼로그의_두_선택지는_각_event를_전달한다() {
        val channels = channels()
        val events = mutableListOf<ChzzkUiEvent>()
        setScreen(ChzzkUiState(mainChannelInfo = channels[0], channels = channels, isDialogOpen = true)) {
            events += it
        }

        composeTestRule.onNode(isDialog()).assertIsDisplayed()
        composeTestRule.onNodeWithText(string(R.string.chzzk_watch_youtube)).assertIsDisplayed().performClick()
        composeTestRule.onNodeWithText(string(R.string.chzzk_watch_chzzk)).assertIsDisplayed().performClick()

        // callback만 기록하므로 주입한 열린 상태는 두 클릭 사이에도 유지된다.
        composeTestRule.runOnIdle {
            assertEquals(listOf(ChzzkUiEvent.YoutubeClicked, ChzzkUiEvent.ChzzkClicked), events)
        }
    }

    @Test
    fun 다이얼로그에서_뒤로가기는_닫기_event를_전달한다() {
        val channels = channels()
        val events = mutableListOf<ChzzkUiEvent>()
        setScreen(ChzzkUiState(mainChannelInfo = channels[0], channels = channels, isDialogOpen = true)) {
            events += it
        }
        composeTestRule.onNode(isDialog()).assertIsDisplayed()

        pressBack()

        composeTestRule.runOnIdle {
            assertEquals(listOf(ChzzkUiEvent.DialogDismissed), events)
        }
    }

    @Test
    fun 조회_실패는_기존_오류_문구를_표시하고_채널과_로딩을_감춘다() {
        setScreen(ChzzkUiState(hasLoadError = true))

        composeTestRule.onNodeWithText(string(R.string.chzzk_load_error)).assertIsDisplayed()
        composeTestRule.onNodeWithText(string(R.string.chzzk_title)).assertDoesNotExist()
        composeTestRule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)).assertDoesNotExist()
    }

    private fun channels(): List<Channel> = listOf(
        Channel(
            id = "main", name = "메인 채널", explains = arrayOf("메인 설명", "590000"),
            url = "https://chzzk.naver.com/live/main", image = "", type = 0, isOnline = true
        ),
        Channel(
            id = "crew", name = "크루 채널", explains = arrayOf("크루 설명"),
            url = "https://chzzk.naver.com/live/crew", image = "", type = 1, isOnline = false
        )
    )

    private fun string(id: Int, vararg args: Any): String =
        InstrumentationRegistry.getInstrumentation().targetContext.getString(id, *args)
}
