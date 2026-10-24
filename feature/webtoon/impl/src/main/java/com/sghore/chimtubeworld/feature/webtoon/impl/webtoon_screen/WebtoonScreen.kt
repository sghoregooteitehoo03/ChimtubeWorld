package com.sghore.chimtubeworld.feature.webtoon.impl.webtoon_screen

import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sghore.chimtubeworld.core.designsystem.component.RowList
import com.sghore.chimtubeworld.core.designsystem.component.ScreenScaffold
import com.sghore.chimtubeworld.core.designsystem.component.TitleTextWithExplain
import com.sghore.chimtubeworld.core.designsystem.theme.ChimtubeWorldTheme
import com.sghore.chimtubeworld.feature.webtoon.impl.R
import com.sghore.chimtubeworld.feature.webtoon.impl.webtoon_screen.component.WebtoonItem

@Composable
internal fun WebtoonScreen(
    uiState: WebtoonUiState,
    onEvent: (WebtoonUiEvent) -> Unit
) {
    ScreenScaffold(title = stringResource(R.string.webtoon_screen_title)) {
        Box(modifier = Modifier.fillMaxSize()) {
            RowList(
                list = uiState.webtoonInfos,
                spanCount = 2,
                contentPaddingValue = 12.dp,
                itemPaddingValue = 12.dp,
                headerItem = {
                    TitleTextWithExplain(
                        title = stringResource(R.string.webtoon_title),
                        explain = stringResource(R.string.webtoon_explanation)
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                },
                listItem = { index, modifier ->
                    val webtoon = uiState.webtoonInfos[index]

                    WebtoonItem(
                        webtoon = webtoon,
                        contentDescription = stringResource(
                            R.string.webtoon_cover_content_description,
                            webtoon.id
                        ),
                        modifier = modifier,
                        onClick = { onEvent(WebtoonUiEvent.WebtoonClicked(it)) }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(name = "Screen Light", showBackground = true)
@Preview(name = "Screen Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun WebtoonScreenPreview() {
    ChimtubeWorldTheme(darkTheme = isSystemInDarkTheme()) {
        WebtoonScreen(uiState = WebtoonUiState(), onEvent = {})
    }
}
