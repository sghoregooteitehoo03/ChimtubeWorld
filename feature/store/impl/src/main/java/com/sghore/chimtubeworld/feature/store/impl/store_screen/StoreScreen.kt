package com.sghore.chimtubeworld.feature.store.impl.store_screen

import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sghore.chimtubeworld.core.designsystem.component.ScreenScaffold
import com.sghore.chimtubeworld.core.designsystem.component.TitleTextWithExplain
import com.sghore.chimtubeworld.core.designsystem.theme.ChimtubeWorldTheme
import com.sghore.chimtubeworld.feature.store.impl.R
import com.sghore.chimtubeworld.feature.store.impl.store_screen.component.StoreInfoCategoryItem

@Composable
internal fun StoreScreen(
    uiState: StoreUiState,
    onEvent: (StoreUiEvent) -> Unit
) {
    ScreenScaffold(title = stringResource(R.string.store_screen_title)) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TitleTextWithExplain(
                title = stringResource(R.string.store_title),
                explain = "",
                modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            ) {
                StoreInfoCategoryItem(
                    modifier = Modifier.weight(0.5f),
                    storeInfo = uiState.storeInfoList[0],
                    contentDescription = stringResource(
                        R.string.store_image_content_description,
                        uiState.storeInfoList[0].channelName
                    ),
                    onClick = { onEvent(StoreUiEvent.StoreClicked(it)) }
                )

                Spacer(modifier = Modifier.width(12.dp))

                StoreInfoCategoryItem(
                    modifier = Modifier.weight(0.5f),
                    storeInfo = uiState.storeInfoList[1],
                    contentDescription = stringResource(
                        R.string.store_image_content_description,
                        uiState.storeInfoList[1].channelName
                    ),
                    onClick = { onEvent(StoreUiEvent.StoreClicked(it)) }
                )
            }
        }
    }
}

@Preview(name = "Screen Light", showBackground = true)
@Preview(name = "Screen Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun StoreScreenPreview() {
    ChimtubeWorldTheme(darkTheme = isSystemInDarkTheme()) {
        StoreScreen(uiState = StoreUiState(), onEvent = {})
    }
}
