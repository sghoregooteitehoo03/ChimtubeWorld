package com.sghore.chimtubeworld.feature.cafe.impl.cafe_screen.component

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sghore.chimtubeworld.core.designsystem.theme.ChimtubeWorldTheme
import com.sghore.chimtubeworld.core.model.Post
import com.sghore.chimtubeworld.feature.cafe.impl.R
import com.sghore.chimtubeworld.core.designsystem.R as DesignSystemR

@Composable
internal fun CafePostItem(
    post: Post?,
    isRead: Boolean,
    modifier: Modifier = Modifier,
    onClick: (Post?) -> Unit = {}
) {
    val contentAlpha = if (isRead) {
        0.4f
    } else {
        1f
    }

    Box(
        modifier = Modifier
            .clickable { onClick(post) }
    ) {
        Row(
            modifier = modifier
                .height(80.dp)
                .padding(top = 6.dp, bottom = 6.dp)
                .alpha(contentAlpha)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.Top),
            ) {
                Text(
                    text = post?.title ?: "",
                    color = colorResource(id = DesignSystemR.color.item_color),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stringResource(R.string.cafe_post_metadata, "${post?.userName}", "${post?.postDate}"),
                    color = colorResource(id = DesignSystemR.color.default_text_color),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            if ((post?.postImage ?: "").isNotEmpty()) {
                AsyncImage(
                    model = post?.postImage,
                    contentDescription = post?.postImage,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(68.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }
        }
        HorizontalDivider(
            modifier = modifier
                .align(Alignment.BottomStart),
            color = colorResource(id = DesignSystemR.color.gray_bright_night)
        )
    }
}

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CafePostItemPreview() {
    ChimtubeWorldTheme(darkTheme = isSystemInDarkTheme()) {
        CafePostItem(
            post = Post(
                id = 0, title = "[잡담] 최고민수야 고맙다", userName = "sghore",
                postDate = "2021.12.07", postImage = "", url = ""
            ),
            isRead = false,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp)
        )
    }
}
