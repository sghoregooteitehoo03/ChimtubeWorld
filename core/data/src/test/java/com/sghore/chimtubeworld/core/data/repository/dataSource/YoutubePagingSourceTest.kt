package com.sghore.chimtubeworld.core.data.repository.dataSource

import androidx.paging.PagingSource
import com.sghore.chimtubeworld.core.data.datasource.FakeYoutubeRemoteDataSource
import com.sghore.chimtubeworld.core.data.datasource.SpyDao
import com.sghore.chimtubeworld.core.data.model.remoteVideo
import com.sghore.chimtubeworld.core.database.entity.BookmarkEntity
import com.sghore.chimtubeworld.core.model.Video
import com.sghore.chimtubeworld.core.network.dto.youtubeAPI.PlaylistItemDTO
import com.sghore.chimtubeworld.core.network.dto.youtubeAPI.PlaylistItemsDTO
import com.sghore.chimtubeworld.core.network.dto.youtubeAPI.PlaylistItemsDetailsDTO
import com.sghore.chimtubeworld.core.network.dto.youtubeAPI.VideosDTO
import java.time.LocalDateTime
import java.time.ZoneId
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class YoutubePagingSourceTest {

    @Test
    fun `요청 영상 ID와 다른 응답을 변환하고 응답 ID로 북마크를 조회한다`() = runTest {
        val remote = FakeYoutubeRemoteDataSource().apply {
            playlistItemsResponse = PlaylistItemsDTO(
                nextPageToken = "next-token",
                items = listOf(PlaylistItemDTO(PlaylistItemsDetailsDTO("requested-video")))
            )
            videosResponse = VideosDTO(listOf(remoteVideo(id = "response-video")))
        }
        val entity = BookmarkEntity(42, "response-video", "저장된 장면", 987L, 123)
        val dao = SpyDao().apply { bookmarks = listOf(entity) }
        val source = YoutubePagingSource("uploads-7", remote, dao)

        val page = source.load(PagingSource.LoadParams.Refresh(null, 10, false))
            as PagingSource.LoadResult.Page<String, Video>
        val actual = page.data.single()

        assertEquals(listOf(FakeYoutubeRemoteDataSource.PlaylistItemsRequest("uploads-7", null)), remote.playlistItemsRequests)
        assertEquals(listOf(listOf("requested-video")), remote.videoRequests)
        assertEquals(listOf("response-video"), dao.bookmarkRequests)
        assertEquals("response-video", actual.id)
        assertEquals("원격 채널", actual.channelName)
        assertEquals("원격 영상 제목", actual.title)
        assertEquals("https://example.com/high.jpg", actual.thumbnail)
        // 기존 구현은 Z를 literal로 해석한 뒤 9시간을 더한다.
        val expectedUploadTime = LocalDateTime.of(2026, 1, 2, 3, 4, 5)
            .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() + 32_400_000L
        assertEquals(expectedUploadTime, actual.uploadTime)
        assertEquals(987_654L, actual.viewCount)
        assertEquals(3_723_000L, actual.duration)
        assertEquals("https://www.youtube.com/watch?v=response-video", actual.url)
        val bookmark = actual.bookmarks.single()
        assertEquals(entity.id, bookmark.id)
        assertEquals(entity.videoId, bookmark.videoId)
        assertEquals(entity.title, bookmark.title)
        assertEquals(entity.videoPosition, bookmark.videoPosition)
        assertEquals(entity.color, bookmark.color)
        assertNull(page.prevKey)
        assertEquals("next-token", page.nextKey)
    }

    @Test
    fun `다음 토큰과 빈 응답을 전달하며 빈 문자열 다음 키를 보존한다`() = runTest {
        val remote = FakeYoutubeRemoteDataSource()
        val dao = SpyDao()
        val page = YoutubePagingSource("uploads-7", remote, dao)
            .load(PagingSource.LoadParams.Append("current-token", 10, false))
            as PagingSource.LoadResult.Page<String, Video>

        assertEquals(listOf(FakeYoutubeRemoteDataSource.PlaylistItemsRequest("uploads-7", "current-token")), remote.playlistItemsRequests)
        assertEquals(listOf(emptyList<String>()), remote.videoRequests)
        assertTrue(page.data.isEmpty())
        assertTrue(dao.bookmarkRequests.isEmpty())
        assertNull(page.prevKey)
        assertEquals("", page.nextKey)
    }

    @Test
    fun `목록 조회 실패는 영상과 북마크를 조회하지 않고 Error로 전달한다`() = runTest {
        val failure = IllegalStateException("목록 조회 실패")
        val remote = FakeYoutubeRemoteDataSource().apply { playlistItemsFailure = failure }
        val dao = SpyDao()
        val result = YoutubePagingSource("uploads-7", remote, dao)
            .load(PagingSource.LoadParams.Refresh(null, 10, false))

        assertSame(failure, (result as PagingSource.LoadResult.Error<*, *>).throwable)
        assertTrue(remote.videoRequests.isEmpty())
        assertTrue(dao.bookmarkRequests.isEmpty())
    }

    @Test
    fun `영상 조회 실패는 북마크를 조회하지 않고 Error로 전달한다`() = runTest {
        val failure = IllegalStateException("영상 조회 실패")
        val remote = FakeYoutubeRemoteDataSource().apply { videosFailure = failure }
        val dao = SpyDao()
        val result = YoutubePagingSource("uploads-7", remote, dao)
            .load(PagingSource.LoadParams.Refresh(null, 10, false))

        assertSame(failure, (result as PagingSource.LoadResult.Error<*, *>).throwable)
        assertEquals(listOf(emptyList<String>()), remote.videoRequests)
        assertTrue(dao.bookmarkRequests.isEmpty())
    }

    @Test
    fun `북마크 조회 실패는 같은 예외의 Error를 반환한다`() = runTest {
        val failure = IllegalStateException("북마크 조회 실패")
        val remote = FakeYoutubeRemoteDataSource().apply { videosResponse = VideosDTO(listOf(remoteVideo())) }
        val dao = SpyDao().apply { getBookmarksFailure = failure }
        val result = YoutubePagingSource("uploads-7", remote, dao)
            .load(PagingSource.LoadParams.Refresh(null, 10, false))

        assertSame(failure, (result as PagingSource.LoadResult.Error<*, *>).throwable)
        assertEquals(listOf("response-video"), dao.bookmarkRequests)
    }
}
