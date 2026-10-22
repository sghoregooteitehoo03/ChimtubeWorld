package com.sghore.chimtubeworld.navigation

import com.sghore.chimtubeworld.core.model.Bookmark
import com.sghore.chimtubeworld.core.model.Video
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class NavigationKeysTest {
    @Test
    fun everyKeyCanBeSerializedWithRouteReservedCharacters() {
        val name = "침착맨 / 재생목록? #1 & 100% + \"영상\""
        val url = "https://www.youtube.com/watch?v=abc&t=42s&list=PL%2Fabc#fragment"
        assertEquals(YoutubeKey, roundTrip(YoutubeKey))
        assertEquals(ChzzkKey, roundTrip(ChzzkKey))
        assertEquals(WebtoonKey, roundTrip(WebtoonKey))
        assertEquals(CafeKey, roundTrip(CafeKey))
        assertEquals(StoreKey, roundTrip(StoreKey))
        val playlists = PlaylistsKey(name, null, "PL1|PL2", 1)
        assertEquals(playlists, roundTrip(playlists))
        val videos = VideosKey("PL1", name, VideoSource.YOUTUBE)
        assertEquals(videos, roundTrip(videos))
        val share = AddBookmarkKey(url, "request-1")
        assertEquals(share, roundTrip(share))
        assertNotEquals(share, share.copy(requestId = "request-2"))
        val bookmark = Bookmark(7, "video", name, -32400000L, -65536)
        val edit = EditBookmarkKey(
            video = Video("video", name, name, url, 10L, 20L, 30L, url, listOf(bookmark)),
            pos = 0,
            source = VideoSource.TWITCH
        )
        val restored = roundTrip(edit)
        assertEquals(edit, restored)
        // Bookmark.equals only compares IDs; assert its remaining serialized fields too.
        val restoredBookmark = restored.video.bookmarks.single()
        assertEquals(bookmark.videoId, restoredBookmark.videoId)
        assertEquals(bookmark.title, restoredBookmark.title)
        assertEquals(bookmark.videoPosition, restoredBookmark.videoPosition)
        assertEquals(bookmark.color, restoredBookmark.color)
    }

    private inline fun <reified T> roundTrip(value: T): T = Json.decodeFromString(Json.encodeToString(value))
}
