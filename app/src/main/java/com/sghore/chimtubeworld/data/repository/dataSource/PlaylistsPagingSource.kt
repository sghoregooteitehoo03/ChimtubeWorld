package com.sghore.chimtubeworld.data.repository.dataSource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sghore.chimtubeworld.data.model.Playlist
import com.sghore.chimtubeworld.data.retrofit.RetrofitService

class PlaylistsPagingSource(
    private val channelId: String?,
    private val playlistId: MutableList<String>,
    private val retrofitService: RetrofitService
) : PagingSource<String, Playlist>() {
    override fun getRefreshKey(state: PagingState<String, Playlist>): String? {
        return null
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Playlist> {
        return try {
            val pageKey = params.key?.ifEmpty { null }

            if (channelId != null && playlistId.isNotEmpty()) {
                val result = retrofitService.getYPlaylists(
                    channelId = null,
                    playlistId = playlistId,
                    pageToken = pageKey
                )

                val playlist = result.items.map {
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
            } else {
                val result = retrofitService.getYPlaylists(
                    channelId = channelId,
                    playlistId = playlistId.ifEmpty { null },
                    pageToken = pageKey
                )

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