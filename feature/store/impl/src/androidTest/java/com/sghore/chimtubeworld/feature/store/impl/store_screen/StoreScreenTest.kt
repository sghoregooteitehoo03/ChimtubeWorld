package com.sghore.chimtubeworld.feature.store.impl.store_screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.sghore.chimtubeworld.core.designsystem.theme.ChimtubeWorldTheme
import com.sghore.chimtubeworld.core.model.GoodsChannelInfo
import com.sghore.chimtubeworld.feature.store.impl.R
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StoreScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun setScreen(state: StoreUiState, onEvent: (StoreUiEvent) -> Unit = {}) {
        composeTestRule.setContent {
            ChimtubeWorldTheme {
                StoreScreen(uiState = state, onEvent = onEvent)
            }
        }
    }

    @Test
    fun 상태의_두_상점과_제목을_표시한다() {
        val stores = stores()
        setScreen(StoreUiState(storeInfoList = stores))

        composeTestRule.onNodeWithText(string(R.string.store_screen_title)).assertIsDisplayed()
        composeTestRule.onNodeWithText(string(R.string.store_title)).assertIsDisplayed()

        stores.forEach { storeInfo ->
            composeTestRule.onNodeWithText(storeInfo.channelName).assertIsDisplayed()
            composeTestRule.onNodeWithContentDescription(
                string(R.string.store_image_content_description, storeInfo.channelName)
            ).assertIsDisplayed()
        }

        composeTestRule.onNodeWithText("침착맨").assertDoesNotExist()
        composeTestRule.onNodeWithText("얼렁뚱땅 상점").assertDoesNotExist()
    }

    @Test
    fun 각_상점_클릭은_선택한_값의_event만_전달한다() {
        val stores = stores()
        val events = mutableListOf<StoreUiEvent>()
        setScreen(StoreUiState(storeInfoList = stores)) { events += it }

        stores.forEachIndexed { index, storeInfo ->
            composeTestRule.onNodeWithText(storeInfo.channelName).performClick()

            composeTestRule.runOnIdle {
                assertEquals(
                    stores.take(index + 1).map { StoreUiEvent.StoreClicked(it) },
                    events
                )
            }
        }
    }

    private fun stores(): List<GoodsChannelInfo> = listOf(
        GoodsChannelInfo(
            channelImage = R.drawable.marple,
            channelName = "표시할 첫 상점",
            url = "https://example.com/store/first"
        ),
        GoodsChannelInfo(
            channelImage = R.drawable.naver,
            channelName = "표시할 둘째 상점",
            url = "https://example.com/store/second"
        )
    )

    private fun string(id: Int, vararg args: Any): String =
        InstrumentationRegistry.getInstrumentation().targetContext.getString(id, *args)
}
