package com.sghore.chimtubeworld.navigation

import androidx.navigation3.runtime.NavKey
import com.sghore.chimtubeworld.core.model.Bookmark
import com.sghore.chimtubeworld.core.model.Video
import com.sghore.chimtubeworld.core.navigation.NavigationResultKey
import com.sghore.chimtubeworld.core.navigation.Navigator
import java.util.UUID
import kotlinx.serialization.Serializable

@Serializable
data class AddBookmarkKey(
    val url: String,
    // Each delivery of the same share URL still owns a distinct entry and ViewModel.
    val requestId: String
) : NavKey

@Serializable
data class EditBookmarkKey(val video: Video, val pos: Int, val source: VideoSource) : NavKey

object BookmarkResultKey : NavigationResultKey<Bookmark>()

fun Navigator.navigateToAddBookmark(url: String) {
    navigate(AddBookmarkKey(url, UUID.randomUUID().toString()))
}

fun Navigator.navigateToEditBookmark(video: Video, pos: Int, source: VideoSource) {
    navigate(EditBookmarkKey(video, pos, source))
}
