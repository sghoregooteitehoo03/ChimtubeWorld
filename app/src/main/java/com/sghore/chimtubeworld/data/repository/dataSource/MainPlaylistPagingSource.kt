package com.sghore.chimtubeworld.data.repository.dataSource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sghore.chimtubeworld.data.model.Playlist
import com.sghore.chimtubeworld.data.retrofit.dto.youtubeAPI.PlaylistsDTO
import kotlin.reflect.KSuspendFunction3

class MainPlaylistPagingSource(
    private val channelId: String?,
    private val playlistId: MutableList<String>,
    private val getPlaylist: KSuspendFunction3<String?, List<String>?, String?, PlaylistsDTO>
) : PagingSource<String, Playlist>() {
    override fun getRefreshKey(state: PagingState<String, Playlist>): String? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey
        }
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Playlist> {
        return try {
            val pageKey = params.key?.ifEmpty { null }

            if (channelId != null && playlistId.isNotEmpty()) { // 침착맨 채널의 최근 업로드된 영상을 가져옴
                val playlist = getPlaylist(null, playlistId, pageKey).items
                    .map {
                        Playlist(
                            id = it.id,
                            title = "최근 업로드된 동영상",
                            thumbnailImage = it.snippet.thumbnails.high.url
                        )
                    }

                playlistId.removeAt(0)
                LoadResult.Page(
                    data = playlist,
                    prevKey = null,
                    nextKey = ""
                )
            } else { // 침착맨 채널의 생성되어있는 재생목록들을 페이징 하여 가져옴
                val result = getPlaylist(channelId, null, pageKey)
                val playlist = result.items.map {
                    Playlist(
                        id = it.id,
                        title = it.snippet.title,
                        thumbnailImage = it.snippet.thumbnails.high.url
                    )
                }

                val nextPageKey = result.nextPageToken
                LoadResult.Page(
                    data = playlist,
                    prevKey = null,
                    nextKey = nextPageKey
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }
}