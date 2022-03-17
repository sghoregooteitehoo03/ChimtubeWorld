package com.sghore.chimtubeworld.presentation.bookmarkScreen

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.sghore.chimtubeworld.R
import com.sghore.chimtubeworld.data.model.Video
import com.sghore.chimtubeworld.util.parseUploadTimeText
import com.sghore.chimtubeworld.util.parseVideoDurationText
import com.sghore.chimtubeworld.util.parseViewCountText
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.Duration

@Composable
fun BookmarkScreen(
    uiState: BookmarkScreenState,
    buttonText: String,
    onTitleChange: (String) -> Unit,
    onVideoPositionChange: (String) -> Unit,
    onChangeBookmarkColor: (Int) -> Unit,
    onButtonClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(
                state = rememberScrollState()
            )
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = colorResource(id = R.color.item_color)
            )
        } else {
            Column {
                VideoInfo(
                    video = uiState.videoData,
                    typeImage = uiState.videoTypeImage
                )
                Spacer(modifier = Modifier.height(8.dp))
                InputBookmarkInfo(
                    uiState = uiState,
                    buttonText = buttonText,
                    onTitleChange = onTitleChange,
                    onVideoPositionChange = onVideoPositionChange,
                    onChangeBookmarkColor = onChangeBookmarkColor,
                    onButtonClick = onButtonClick
                )
            }
        }
    }
}

@Composable
fun VideoInfo(
    video: Video?,
    typeImage: Int
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box {
            Image(
                painter = rememberImagePainter(data = video?.thumbnail),
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
                            color = colorResource(id = R.color.black_alpha_80),
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
                    color = colorResource(id = R.color.item_color),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Image(
                    painter = painterResource(id = typeImage),
                    contentDescription = "typeImage"
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = video?.title ?: "",
                color = colorResource(id = R.color.item_color)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${parseViewCountText(video?.viewCount ?: 0)} | ${parseUploadTimeText(video?.uploadTime ?: 0)}",
                color = colorResource(id = R.color.default_text_color)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        Divider(
            color = colorResource(id = R.color.gray_bright_night),
            thickness = 2.dp
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun InputBookmarkInfo(
    uiState: BookmarkScreenState,
    buttonText: String,
    onTitleChange: (String) -> Unit,
    onVideoPositionChange: (String) -> Unit,
    onChangeBookmarkColor: (Int) -> Unit,
    onButtonClick: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier.padding(start = 12.dp, end = 12.dp)
    ) {
        TextField(
            value = uiState.bookmarkTitle,
            onValueChange = onTitleChange,
            maxLines = 1,
            placeholder = {
                Text(
                    text = "북마크 제목",
                    color = colorResource(id = R.color.default_text_color)
                )
            },
            colors = TextFieldDefaults.textFieldColors(
                textColor = colorResource(id = R.color.item_color),
                backgroundColor = Color.Transparent,
                focusedIndicatorColor = colorResource(id = R.color.item_color),
                unfocusedIndicatorColor = colorResource(id = R.color.gray_night),
                cursorColor = colorResource(id = R.color.item_color)
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
            onValueChange = onVideoPositionChange,
            maxLines = 1,
            placeholder = {
                Text(
                    text = "영상 위치 (00:00)",
                    color = colorResource(id = R.color.default_text_color)
                )
            },
            colors = TextFieldDefaults.textFieldColors(
                textColor = colorResource(id = R.color.item_color),
                backgroundColor = Color.Transparent,
                focusedIndicatorColor = colorResource(id = R.color.item_color),
                unfocusedIndicatorColor = colorResource(id = R.color.gray_night),
                cursorColor = colorResource(id = R.color.item_color)
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
            backgroundColor = colorResource(id = R.color.item_color),
            disabledBackgroundColor = colorResource(id = R.color.gray_night)
        )

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = buttonColors,
            shape = RoundedCornerShape(8.dp),
            enabled = uiState.isEnable,
            onClick = { onButtonClick() }
        ) {
            Text(
                text = buttonText,
                color = colorResource(id = R.color.item_reverse_color)
            )
        }
    }
}

@Composable
fun BookmarkColorList(
    selectedColor: Int,
    onClick: (Int) -> Unit = {}
) {
    val colorList = listOf(
        android.graphics.Color.parseColor("#FF0000"),
        android.graphics.Color.parseColor("#FF6200"),
        android.graphics.Color.parseColor("#FFEB00"),
        android.graphics.Color.parseColor("#76FF00"),
        android.graphics.Color.parseColor("#0014FF"),
        android.graphics.Color.parseColor("#C400FF"),
        android.graphics.Color.parseColor("#767676"),
        android.graphics.Color.parseColor("#000000")
    )
    val colorIndex = colorList.indexOf(selectedColor)

    LazyRow {
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
fun BookmarkColorItem(
    color: Int,
    isSelected: Boolean,
    onClick: (Int) -> Unit = {}
) {
    val backgroundColor = if (isSelected) {
        colorResource(id = R.color.black_alpha_30)
    } else {
        colorResource(id = R.color.gray_bright_night)
    }

    Box(
        modifier = Modifier
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
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = null
            ) {
                onClick(color)
            },
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

@Preview
@Composable
fun VideoInfoPreview() {
    MaterialTheme {
        val dateFormat = SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            Locale.KOREA
        )
        // 업로드 시간
        val uploadTime =
            dateFormat.parse("2022-01-21T10:00:31Z").time + 32400000 // (+9 Hours) UTC -> KOREA
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
            typeImage = R.drawable.youtube
        )
    }
}

@Preview
@Composable
fun BookmarkColorListPreview() {
    MaterialTheme {
        BookmarkColorList(selectedColor = android.graphics.Color.parseColor("#FF0000"))
    }
}

