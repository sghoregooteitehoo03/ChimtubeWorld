package com.sghore.chimtubeworld.feature.youtube.impl.youtube_screen

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sghore.chimtubeworld.core.designsystem.R as DesignSystemR
import com.sghore.chimtubeworld.core.designsystem.component.ScreenScaffold
import com.sghore.chimtubeworld.core.designsystem.component.TitleTextWithExplain
import com.sghore.chimtubeworld.core.designsystem.theme.ChimtubeWorldTheme
import com.sghore.chimtubeworld.feature.youtube.impl.R
import com.sghore.chimtubeworld.feature.youtube.impl.youtube_screen.component.MainYoutubeChannelList
import com.sghore.chimtubeworld.feature.youtube.impl.youtube_screen.component.SubYoutubeChannelList

@Composable
internal fun YoutubeScreen(
    uiState: YoutubeUiState,
    onEvent: (YoutubeUiEvent) -> Unit
) {
    ScreenScaffold {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    trackColor = Color.Transparent,
                    strokeCap = StrokeCap.Square,
                    gapSize = 0.dp,
                    modifier = Modifier.align(Alignment.Center),
                    color = colorResource(id = DesignSystemR.color.item_color)
                )
            } else {
                Column(modifier = Modifier.padding(12.dp)) {
                    if (uiState.hasLoadError) {
                        Text(stringResource(R.string.youtube_load_error), color = colorResource(DesignSystemR.color.item_color))
                    }
                    TitleTextWithExplain(
                        title = stringResource(R.string.youtube_title),
                        explain = stringResource(R.string.youtube_explanation)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    MainYoutubeChannelList(
                        mainChannelList = uiState.channels.filter { it?.type == 0 },
                        onClick = { onEvent(YoutubeUiEvent.ChannelClicked(it)) }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    TitleTextWithExplain(
                        title = stringResource(R.string.youtube_sub_title),
                        explain = stringResource(R.string.youtube_sub_explanation)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    SubYoutubeChannelList(
                        subChannelList = uiState.channels.filter { it?.type == 1 },
                        onClick = { onEvent(YoutubeUiEvent.ChannelClicked(it)) }
                    )
                }
            }
        }
    }
}

@Preview(name = "Screen Light", showBackground = true)
@Preview(name = "Screen Dark", showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun YoutubeScreenPreview() {
    ChimtubeWorldTheme(darkTheme = isSystemInDarkTheme()) {
        YoutubeScreen(YoutubeUiState(), {})
    }
}
