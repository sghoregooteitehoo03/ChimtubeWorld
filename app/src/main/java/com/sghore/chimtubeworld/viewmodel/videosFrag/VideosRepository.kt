package com.sghore.chimtubeworld.viewmodel.videosFrag

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.google.firebase.firestore.FirebaseFirestore
import com.sghore.chimtubeworld.R
import com.sghore.chimtubeworld.adapter.paging.TwitchPagingSource
import com.sghore.chimtubeworld.adapter.paging.YoutubePagingSource
import retrofit2.Retrofit
import javax.inject.Inject

class VideosRepository @Inject constructor(
    private val retrofitBuilder: Retrofit.Builder,
    private val store: FirebaseFirestore
) {
    fun getVideos(channelId: String, typeImageRes: Int) =
        Pager(PagingConfig(20)) {
            if (typeImageRes == R.drawable.ic_youtube) { // 유튜브 영상일 때
                YoutubePagingSource(
                    channelId = channelId,
                    retrofitBuilder = retrofitBuilder
                )
            } else { // 트위치 영상일 때
                TwitchPagingSource(
                    channelId = channelId,
                    retrofitBuilder = retrofitBuilder,
                    store = store
                )
            }
        }.flow
}