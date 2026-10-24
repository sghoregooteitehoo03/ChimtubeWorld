package com.sghore.chimtubeworld.feature.chzzk.impl.chzzk_screen

import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.sghore.chimtubeworld.core.designsystem.component.RowList
import com.sghore.chimtubeworld.core.designsystem.component.ScreenScaffold
import com.sghore.chimtubeworld.core.designsystem.component.TitleTextWithExplain
import com.sghore.chimtubeworld.core.designsystem.theme.ChimtubeWorldTheme
import com.sghore.chimtubeworld.core.model.Channel
import com.sghore.chimtubeworld.feature.chzzk.impl.R
import com.sghore.chimtubeworld.feature.chzzk.impl.chzzk_screen.component.ChannelSelectionDialog
import com.sghore.chimtubeworld.feature.chzzk.impl.chzzk_screen.component.ChzzkCrewChannelItem
import com.sghore.chimtubeworld.feature.chzzk.impl.chzzk_screen.component.MainChannelInfo
import com.sghore.chimtubeworld.core.designsystem.R as DesignSystemR

@Composable
internal fun ChzzkScreen(
    uiState: ChzzkUiState,
    onEvent: (ChzzkUiEvent) -> Unit
) {
    ScreenScaffold(title = stringResource(R.string.chzzk_screen_title)) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    trackColor = Color.Transparent,
                    strokeCap = StrokeCap.Square,
                    gapSize = 0.dp,
                    modifier = Modifier.align(Alignment.Center),
                    color = colorResource(id = DesignSystemR.color.item_color)
                )
            } else if (uiState.hasLoadError) {
                Text(
                    text = stringResource(R.string.chzzk_load_error),
                    color = colorResource(DesignSystemR.color.item_color),
                    modifier = Modifier.align(Alignment.Center).padding(12.dp)
                )
            } else {
                RowList(
                    list = uiState.channels,
                    spanCount = 4,
                    contentPaddingValue = 12.dp,
                    itemPaddingValue = 12.dp,
                    headerItem = {
                        TitleTextWithExplain(
                            title = stringResource(R.string.chzzk_title),
                            explain = stringResource(R.string.chzzk_explanation)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        MainChannelInfo(
                            mainChannel = uiState.mainChannelInfo,
                            onClick = { onEvent(ChzzkUiEvent.MainChannelClicked) }
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        TitleTextWithExplain(
                            title = stringResource(R.string.chzzk_crew_title),
                            explain = stringResource(R.string.chzzk_crew_explanation)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    },
                    listItem = { index, modifier ->
                        ChzzkCrewChannelItem(
                            channel = uiState.channels[index],
                            onClick = { onEvent(ChzzkUiEvent.CrewChannelClicked(it)) },
                            modifier = modifier
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    if (uiState.isDialogOpen) {
        ChannelSelectionDialog(
            onDismiss = { onEvent(ChzzkUiEvent.DialogDismissed) },
            onYoutubeClick = { onEvent(ChzzkUiEvent.YoutubeClicked) },
            onChzzkClick = { onEvent(ChzzkUiEvent.ChzzkClicked) }
        )
    }
}

@Preview(name = "Screen Light", showBackground = true)
@Preview(name = "Screen Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ChzzkScreenPreview() {
    val mainChannel = Channel(
        id = "preview", name = "침착맨", explains = arrayOf("준회원", "590000"),
        url = "", image = "", type = 0, isOnline = true
    )

    ChimtubeWorldTheme(darkTheme = isSystemInDarkTheme()) {
        ChzzkScreen(
            uiState = ChzzkUiState(mainChannelInfo = mainChannel, channels = listOf(mainChannel)),
            onEvent = {}
        )
    }
}

@Preview(name = "Error Light", showBackground = true)
@Preview(name = "Error Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ChzzkScreenErrorPreview() {
    ChimtubeWorldTheme(darkTheme = isSystemInDarkTheme()) {
        ChzzkScreen(uiState = ChzzkUiState(hasLoadError = true), onEvent = {})
    }
}
