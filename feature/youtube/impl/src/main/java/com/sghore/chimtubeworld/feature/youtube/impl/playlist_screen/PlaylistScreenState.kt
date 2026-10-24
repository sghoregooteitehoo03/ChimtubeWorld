package com.sghore.chimtubeworld.feature.youtube.impl.playlist_screen

import androidx.paging.PagingData
import com.sghore.chimtubeworld.core.model.Playlist
import kotlinx.coroutines.flow.Flow

data class PlaylistScreenState(
    val playlists: Flow<PagingData<Playlist>>? = null
)
