package com.sghore.chimtubeworld.feature.bookmark.impl.bookmark_screen.component

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import coil.compose.AsyncImage
import com.sghore.chimtubeworld.core.common.parseUploadTimeText
import com.sghore.chimtubeworld.core.common.parseVideoDurationText
import com.sghore.chimtubeworld.core.common.parseViewCountText
import com.sghore.chimtubeworld.core.designsystem.R as DesignSystemR
import com.sghore.chimtubeworld.core.designsystem.theme.ChimtubeWorldTheme
import com.sghore.chimtubeworld.core.model.Video
import com.sghore.chimtubeworld.feature.bookmark.impl.R
import com.sghore.chimtubeworld.feature.bookmark.impl.bookmark_screen.BookmarkUiState
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.Duration

@Composable
internal fun VideoInfo(
    video: Video?,
    typeImage: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Box {
            AsyncImage(
                model = video?.thumbnail,
                contentDescription = video?.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.aspectRatio(16 / 9f)
            )
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.BottomEnd)
            ) {
                Text(
                    text = parseVideoDurationText(video?.duration ?: 0),
                    color = Color.White,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .background(
                            color = colorResource(id = DesignSystemR.color.black_alpha_80),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(start = 6.dp, end = 6.dp, top = 2.dp, bottom = 2.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Column(
            modifier = Modifier.padding(start = 12.dp, end = 12.dp)
        ) {
            Row {
                Text(
                    text = video?.channelName ?: "",
                    color = colorResource(id = DesignSystemR.color.item_color),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Image(
                    painter = painterResource(id = typeImage),
                    contentDescription = stringResource(R.string.bookmark_video_source)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = video?.title ?: "",
                color = colorResource(id = DesignSystemR.color.item_color)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.bookmark_metadata, parseViewCountText(video?.viewCount ?: 0), parseUploadTimeText(video?.uploadTime ?: 0)),
                color = colorResource(id = DesignSystemR.color.default_text_color)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(
            color = colorResource(id = DesignSystemR.color.gray_bright_night),
            thickness = 2.dp
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun InputBookmarkInfo(
    uiState: BookmarkUiState,
    buttonText: String,
    onTitleChange: (String) -> Unit,
    onVideoPositionChange: (String) -> Unit,
    onChangeBookmarkColor: (Int) -> Unit,
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = modifier
    ) {
        TextField(
            value = uiState.bookmarkTitle,
            textStyle = MaterialTheme.typography.bodyLarge,
            onValueChange = onTitleChange,
            maxLines = 1,
            placeholder = {
                Text(
                    text = stringResource(R.string.bookmark_title_hint),
                    color = colorResource(id = DesignSystemR.color.default_text_color)
                )
            },
            colors = TextFieldDefaults.colors(
                focusedTextColor = colorResource(id = DesignSystemR.color.item_color),
                unfocusedTextColor = colorResource(id = DesignSystemR.color.item_color),
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                errorContainerColor = Color.Transparent,
                focusedIndicatorColor = colorResource(id = DesignSystemR.color.item_color),
                unfocusedIndicatorColor = colorResource(id = DesignSystemR.color.gray_night),
                cursorColor = colorResource(id = DesignSystemR.color.item_color)
            ),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusRequester.requestFocus() }
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = uiState.videoPosition,
            textStyle = MaterialTheme.typography.bodyLarge,
            onValueChange = onVideoPositionChange,
            maxLines = 1,
            placeholder = {
                Text(
                    text = stringResource(R.string.bookmark_position_hint),
                    color = colorResource(id = DesignSystemR.color.default_text_color)
                )
            },
            colors = TextFieldDefaults.colors(
                focusedTextColor = colorResource(id = DesignSystemR.color.item_color),
                unfocusedTextColor = colorResource(id = DesignSystemR.color.item_color),
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                errorContainerColor = Color.Transparent,
                focusedIndicatorColor = colorResource(id = DesignSystemR.color.item_color),
                unfocusedIndicatorColor = colorResource(id = DesignSystemR.color.gray_night),
                cursorColor = colorResource(id = DesignSystemR.color.item_color)
            ),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { keyboardController?.hide() }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
        )
    }

    Spacer(modifier = Modifier.height(28.dp))
    BookmarkColorList(
        selectedColor = uiState.selectedColor,
        onClick = onChangeBookmarkColor
    )
    Spacer(modifier = Modifier.height(56.dp))

    Row(
        modifier = Modifier
            .padding(start = 12.dp, end = 12.dp, bottom = 12.dp)
    ) {
        val buttonColors = ButtonDefaults.buttonColors(
            containerColor = colorResource(id = DesignSystemR.color.item_color),
            disabledContainerColor = colorResource(id = DesignSystemR.color.gray_night)
        )

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = buttonColors,
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 2.dp,
                pressedElevation = 8.dp,
                focusedElevation = 4.dp,
                hoveredElevation = 4.dp,
                disabledElevation = 0.dp
            ),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(8.dp),
            enabled = uiState.isEnable,
            onClick = { onButtonClick() }
        ) {
            Text(
                text = buttonText,
                color = colorResource(id = DesignSystemR.color.item_reverse_color)
            )
        }
    }
}

@Composable
internal fun BookmarkColorList(
    selectedColor: Int,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colorList = remember {
        listOf(
            Color(0xFFFF0000).toArgb(),
            Color(0xFFFF6200).toArgb(),
            Color(0xFFFFEB00).toArgb(),
            Color(0xFF76FF00).toArgb(),
            Color(0xFF0014FF).toArgb(),
            Color(0xFFC400FF).toArgb(),
            Color(0xFF767676).toArgb(),
            Color(0xFF000000).toArgb()
        )
    }
    val colorIndex = colorList.indexOf(selectedColor)

    LazyRow(
        modifier = modifier
    ) {
        item {
            Spacer(modifier = Modifier.width(12.dp))
        }
        items(colorList.size) { index ->
            BookmarkColorItem(
                color = colorList[index],
                isSelected = colorIndex == index,
                onClick = onClick
            )
            Spacer(modifier = Modifier.width(12.dp))
        }
    }
}

@Composable
internal fun BookmarkColorItem(
    color: Int,
    isSelected: Boolean,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = if (isSelected) {
        colorResource(id = DesignSystemR.color.black_alpha_30)
    } else {
        colorResource(id = DesignSystemR.color.gray_bright_night)
    }

    Box(
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(4.dp)
            )
            .padding(
                start = 16.dp,
                end = 16.dp,
                top = 4.dp,
                bottom = 4.dp
            )
            .clickable { onClick(color) },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(
                    color = Color(color),
                    shape = CircleShape
                )
        )
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun VideoInfoPreview() {
    ChimtubeWorldTheme(darkTheme = isSystemInDarkTheme()) {
        val dateFormat = SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            Locale.KOREA
        )
        // 업로드 시간
        val uploadTime =
            dateFormat.parse("2022-01-21T10:00:31Z")!!.time + 32400000 // (+9 Hours) UTC -> KOREA
        // 영상 길이
        val duration = Duration.parse("PT15M40S")
            .inWholeMilliseconds - 32400000 // (-9 Hours) KOREA -> UTC

        VideoInfo(
            video = Video(
                id = "",
                channelName = "침착맨",
                title = "고기동 설명회",
                thumbnail = "",
                uploadTime = uploadTime,
                viewCount = 295536,
                duration = duration,
                url = ""
            ),
            typeImage = DesignSystemR.drawable.youtube
        )
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun BookmarkColorListPreview() {
    ChimtubeWorldTheme(darkTheme = isSystemInDarkTheme()) {
        BookmarkColorList(
            selectedColor = "#FF0000".toColorInt(),
            {}
        )
    }
}

@Preview(name = "Input Light", showBackground = true)
@Preview(name = "Input Dark", showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun InputBookmarkInfoPreview() {
    ChimtubeWorldTheme(darkTheme = isSystemInDarkTheme()) {
        Surface(color = MaterialTheme.colorScheme.background, tonalElevation = 0.dp) {
            Column {
                InputBookmarkInfo(
                    uiState = BookmarkUiState(
                        bookmarkTitle = "북마크",
                        videoPosition = "00:00",
                        isEnable = true
                    ),
                    buttonText = stringResource(R.string.bookmark_create),
                    onTitleChange = {},
                    onVideoPositionChange = {},
                    onChangeBookmarkColor = {},
                    onButtonClick = {}
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BookmarkColorItemPreview() {
    ChimtubeWorldTheme { BookmarkColorItem(0xFFFF0000.toInt(), true, {}) }
}
