package com.sghore.chimtubeworld.feature.chzzk.impl.chzzk_screen.component

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.sghore.chimtubeworld.core.designsystem.theme.ChimtubeWorldTheme
import com.sghore.chimtubeworld.feature.chzzk.impl.R
import com.sghore.chimtubeworld.core.designsystem.R as DesignSystemR

@Composable
internal fun ChannelSelectionDialog(
    onDismiss: () -> Unit,
    onYoutubeClick: () -> Unit,
    onChzzkClick: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(colorResource(DesignSystemR.color.default_background_color))
                .padding(16.dp)
        ) {
            SelectionItem(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colorResource(DesignSystemR.color.gray_bright_night)),
                image = painterResource(DesignSystemR.drawable.youtube),
                explain = stringResource(R.string.chzzk_watch_youtube),
                onClick = onYoutubeClick
            )

            Spacer(modifier = Modifier.width(20.dp))

            SelectionItem(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colorResource(DesignSystemR.color.gray_bright_night)),
                image = painterResource(DesignSystemR.drawable.chzzk),
                explain = stringResource(R.string.chzzk_watch_chzzk),
                onClick = onChzzkClick
            )
        }
    }
}

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ChannelSelectionDialogPreview() {
    ChimtubeWorldTheme(darkTheme = isSystemInDarkTheme()) {
        ChannelSelectionDialog(onDismiss = {}, onYoutubeClick = {}, onChzzkClick = {})
    }
}
