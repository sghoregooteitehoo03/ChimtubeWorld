package com.sghore.chimtubeworld.data.repository.dataSource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sghore.chimtubeworld.data.model.Playlist
import com.sghore.chimtubeworld.data.retrofit.dto.youtubeAPI.PlaylistsDTO
import kotlin.reflect.KSuspendFunction3

class SubPlaylistPagingSource(
    private val playlistId: List<String>,
    private val getPlaylist: KSuspendFunction3<String?, List<String>?, String?, PlaylistsDTO>
) : PagingSource<Int, Playlist>() {
    override fun getRefreshKey(state: PagingState<Int, Playlist>): Int? {
        return state.anchorPosition?.let {
            val anchorPage = state.closestPageToPosition(it)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Playlist> {
        return try {
            val pageKey = params.key ?: 0
            val nextKey = pageKey + 10

            val pageIdList = if (nextKey > playlistId.size) { // 재생목록 아이디 리스트를 페이징하기 위해 잘라서 가져옴
                playlistId.subList(pageKey, playlistId.size)
            } else {
                playlistId.subList(pageKey, nextKey)
            }

            // 페이징 되어진 아이디들을 가지고 재생목록을 페이징하여 가져옴
            val pageList = getPlaylist(
                null,
                pageIdList,
                null
            ).items.map {
                Playlist(
                    id = it.id,
                    title = it.snippet.title,
                    thumbnailImage = it.snippet.thumbnails.high.url
                )
            }

            LoadResult.Page(
                data = pageList,
                prevKey = null,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }
}