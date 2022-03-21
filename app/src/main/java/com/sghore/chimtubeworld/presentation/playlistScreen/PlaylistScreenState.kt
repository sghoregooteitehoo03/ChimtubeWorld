package com.sghore.chimtubeworld.presentation.playlistScreen

import androidx.paging.PagingData
import com.sghore.chimtubeworld.data.model.Playlist
import kotlinx.coroutines.flow.Flow

data class PlaylistScreenState(
    val playlists: Flow<PagingData<Playlist>>? = null
)
