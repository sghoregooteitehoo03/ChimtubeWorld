package com.sghore.chimtubeworld.presentation.bookmarkScreen

import android.widget.Toast
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.sghore.chimtubeworld.R
import com.sghore.chimtubeworld.data.model.Bookmark
import com.sghore.chimtubeworld.data.model.Video
import com.sghore.chimtubeworld.presentation.ui.GlobalViewModel
import com.sghore.chimtubeworld.util.parseUploadTimeText
import com.sghore.chimtubeworld.util.parseVideoDurationText
import com.sghore.chimtubeworld.util.parseViewCountText
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.Duration

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BookmarkScreen(
    viewModel: BookmarkViewModel,
    gViewModel: GlobalViewModel,
    navController: NavController,
    colorList: List<Int>,
    buttonText: String,
    onButtonClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(
                state = rememberScrollState()
            )
    ) {
        val context = LocalContext.current
        val focusRequester = remember { FocusRequester() }
        val keyboardController = LocalSoftwareKeyboardController.current

        LaunchedEffect(key1 = viewModel.state.errorMsg) {
            val msg = viewModel.state.errorMsg
            if (msg.isNotEmpty()) {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT)
                    .show()
                viewModel.clearMsg()
            }
            viewModel.state.completeBookmark?.let {
                gViewModel.bookmarkData.value = it
                navController.navigateUp()
            }
        }

        viewModel.state.videoData?.let {
            Column {
                VideoInfo(
                    video = viewModel.state.videoData,
                    typeImage = viewModel.state.videoTypeImage
                )

                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    Modifier
                        .padding(start = 12.dp, end = 12.dp)
                ) {
                    TextField(
                        value = viewModel.state.bookmarkTitle,
                        onValueChange = viewModel::setTitle,
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
                        value = viewModel.state.videoPosition,
                        onValueChange = viewModel::setVideoPosition,
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
                    colorList = colorList,
                    selectedColor = viewModel.state.selectedColor,
                    onClick = viewModel::changeBookmarkColor
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
                        enabled = viewModel.state.isEnable,
                        onClick = { onButtonClick() }
                    ) {
                        Text(
                            text = buttonText,
                            color = colorResource(id = R.color.item_reverse_color)
                        )
                    }
                }
            }
        }

        if (viewModel.state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = colorResource(id = R.color.item_color)
            )
        }
    }
}

@Composable
fun AddBookmarkScreen(
    viewModel: BookmarkViewModel,
    gViewModel: GlobalViewModel,
    navController: NavController
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

    BookmarkScreen(
        viewModel = viewModel,
        gViewModel = gViewModel,
        navController = navController,
        colorList = colorList,
        buttonText = "만들기",
        onButtonClick = {
            viewModel.addOrEditBookmark()
        }
    )
}

@Composable
fun EditBookmarkScreen(
    viewModel: BookmarkViewModel,
    gViewModel: GlobalViewModel,
    navController: NavController,
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

    BookmarkScreen(
        viewModel = viewModel,
        gViewModel = gViewModel,
        navController = navController,
        colorList = colorList,
        buttonText = "수정하기",
        onButtonClick = {
            viewModel.addOrEditBookmark(viewModel.selectedBookmark?.id!!)
        }
    )
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

@Composable
fun BookmarkColorList(
    colorList: List<Int>,
    selectedColor: Int,
    onClick: (Int) -> Unit = {}
) {
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

        BookmarkColorList(colorList = colorList, selectedColor = colorList[0])
    }
}

