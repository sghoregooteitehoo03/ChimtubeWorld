package com.sghore.chimtubeworld.core.data.repository.dataSource

import androidx.paging.PagingSource
import com.sghore.chimtubeworld.core.data.datasource.FakeYoutubeRemoteDataSource
import com.sghore.chimtubeworld.core.data.model.remotePlaylist
import com.sghore.chimtubeworld.core.model.Playlist
import com.sghore.chimtubeworld.core.network.dto.youtubeAPI.PlaylistsDTO
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Test

class MainPlaylistPagingSourceTest {

    @Test
    fun `최근 업로드 조회 뒤 빈 키로 채널 재생목록을 조회한다`() = runTest {
        val remote = FakeYoutubeRemoteDataSource().apply {
            playlistsResponse = PlaylistsDTO(nextPageToken = "next-token", items = listOf(remotePlaylist()))
        }
        val source = MainPlaylistPagingSource("channel-7", mutableListOf("requested-7"), remote::getYPlaylists)

        val first = source.load(PagingSource.LoadParams.Refresh(null, 10, false))
            as PagingSource.LoadResult.Page<String, Playlist>

        assertEquals(listOf(FakeYoutubeRemoteDataSource.PlaylistsRequest(null, listOf("requested-7"), null)), remote.playlistsRequests)
        assertEquals(listOf(Playlist("response-playlist", "최근 업로드된 동영상", "https://example.com/high.jpg")), first.data)
        assertNull(first.prevKey)
        assertEquals("", first.nextKey)

        val next = source.load(PagingSource.LoadParams.Append(first.nextKey!!, 10, false))
            as PagingSource.LoadResult.Page<String, Playlist>

        assertEquals(FakeYoutubeRemoteDataSource.PlaylistsRequest("channel-7", null, null), remote.playlistsRequests.last())
        assertEquals(listOf(Playlist("response-playlist", "원격 재생목록", "https://example.com/high.jpg")), next.data)
        assertNull(next.prevKey)
        assertEquals("next-token", next.nextKey)
    }

    @Test
    fun `일반 조회는 페이지 토큰을 전달하고 마지막 응답의 null 키를 보존한다`() = runTest {
        val remote = FakeYoutubeRemoteDataSource().apply {
            playlistsResponse = PlaylistsDTO(items = listOf(remotePlaylist()))
        }
        val source = MainPlaylistPagingSource(null, mutableListOf(), remote::getYPlaylists)

        val page = source.load(PagingSource.LoadParams.Append("requested-token", 10, false))
            as PagingSource.LoadResult.Page<String, Playlist>

        assertEquals(listOf(FakeYoutubeRemoteDataSource.PlaylistsRequest(null, null, "requested-token")), remote.playlistsRequests)
        assertEquals("response-playlist", page.data.single().id)
        assertNull(page.nextKey)
    }

    @Test
    fun `조회 실패는 같은 예외의 Error를 반환한다`() = runTest {
        val failure = IllegalStateException("조회 실패")
        val remote = FakeYoutubeRemoteDataSource().apply { playlistsFailure = failure }
        val source = MainPlaylistPagingSource("channel-7", mutableListOf(), remote::getYPlaylists)

        val result = source.load(PagingSource.LoadParams.Refresh(null, 10, false))

        assertSame(failure, (result as PagingSource.LoadResult.Error<*, *>).throwable)
        assertEquals(listOf(FakeYoutubeRemoteDataSource.PlaylistsRequest("channel-7", null, null)), remote.playlistsRequests)
    }
}
