package com.sghore.chimtubeworld.feature.chzzk.impl.chzzk_screen.component

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sghore.chimtubeworld.core.designsystem.theme.ChimtubeWorldTheme
import com.sghore.chimtubeworld.feature.chzzk.impl.R
import com.sghore.chimtubeworld.core.designsystem.R as DesignSystemR

@Composable
internal fun SelectionItem(
    image: Painter,
    explain: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.clickable { onClick() }.padding(12.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = image, contentDescription = explain, modifier = Modifier.size(74.dp))

        Spacer(modifier = Modifier.height(6.dp))

        Text(text = explain, color = colorResource(DesignSystemR.color.item_color))
    }
}

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SelectionItemPreview() {
    ChimtubeWorldTheme(darkTheme = isSystemInDarkTheme()) {
        SelectionItem(
            image = painterResource(DesignSystemR.drawable.youtube),
            explain = stringResource(R.string.chzzk_watch_youtube),
            onClick = {},
            modifier = Modifier.size(120.dp)
        )
    }
}
