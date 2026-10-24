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

class SubPlaylistPagingSourceTest {

    @Test
    fun `ID를 열 개씩 요청하고 응답 필드와 기존 다음 키를 유지한다`() = runTest {
        val ids = (0..11).map { "requested-$it" }
        val remote = FakeYoutubeRemoteDataSource().apply {
            playlistsResponse = PlaylistsDTO(items = listOf(remotePlaylist()))
        }
        val source = SubPlaylistPagingSource(ids, remote::getYPlaylists)

        val first = source.load(PagingSource.LoadParams.Refresh(null, 5, false))
            as PagingSource.LoadResult.Page<Int, Playlist>

        assertEquals(listOf(FakeYoutubeRemoteDataSource.PlaylistsRequest(null, ids.take(10), null)), remote.playlistsRequests)
        assertEquals(listOf(Playlist("response-playlist", "원격 재생목록", "https://example.com/high.jpg")), first.data)
        assertNull(first.prevKey)
        assertEquals(10, first.nextKey)

        val next = source.load(PagingSource.LoadParams.Append(first.nextKey!!, 5, false))
            as PagingSource.LoadResult.Page<Int, Playlist>

        assertEquals(FakeYoutubeRemoteDataSource.PlaylistsRequest(null, ids.drop(10), null), remote.playlistsRequests.last())
        assertNull(next.prevKey)
        // 마지막 구간도 nextKey가 null이 아닌 기존 정책을 보존한다.
        assertEquals(20, next.nextKey)
    }

    @Test
    fun `빈 ID 목록도 요청하고 기존 키를 반환한다`() = runTest {
        val remote = FakeYoutubeRemoteDataSource()
        val source = SubPlaylistPagingSource(emptyList(), remote::getYPlaylists)

        val page = source.load(PagingSource.LoadParams.Refresh(null, 5, false))
            as PagingSource.LoadResult.Page<Int, Playlist>

        assertEquals(listOf(FakeYoutubeRemoteDataSource.PlaylistsRequest(null, emptyList(), null)), remote.playlistsRequests)
        assertEquals(emptyList<Playlist>(), page.data)
        assertEquals(10, page.nextKey)
    }

    @Test
    fun `조회 실패는 같은 예외의 Error를 반환한다`() = runTest {
        val failure = IllegalStateException("조회 실패")
        val remote = FakeYoutubeRemoteDataSource().apply { playlistsFailure = failure }
        val source = SubPlaylistPagingSource(listOf("requested-7"), remote::getYPlaylists)

        val result = source.load(PagingSource.LoadParams.Refresh(null, 5, false))

        assertSame(failure, (result as PagingSource.LoadResult.Error<*, *>).throwable)
        assertEquals(listOf(FakeYoutubeRemoteDataSource.PlaylistsRequest(null, listOf("requested-7"), null)), remote.playlistsRequests)
    }
}
