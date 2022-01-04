package com.sghore.chimtubeworld.viewmodel.videosFrag

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.google.firebase.firestore.FirebaseFirestore
import com.sghore.chimtubeworld.R
import com.sghore.chimtubeworld.adapter.paging.TwitchPagingSource
import com.sghore.chimtubeworld.adapter.paging.YoutubePagingSource
import com.sghore.chimtubeworld.db.Dao
import retrofit2.Retrofit
import javax.inject.Inject

class VideosRepository @Inject constructor(
    private val retrofitBuilder: Retrofit.Builder,
    private val store: FirebaseFirestore,
    private val dao: Dao
) {
    fun getVideos(channelId: String, typeImageRes: Int) =
        Pager(PagingConfig(20)) {
            if (typeImageRes == R.drawable.ic_youtube) { // 유튜브 영상일 때
                YoutubePagingSource(
                    channelId = channelId,
                    retrofitBuilder = retrofitBuilder,
                    dao = dao
                )
            } else { // 트위치 영상일 때
                TwitchPagingSource(
                    channelId = channelId,
                    retrofitBuilder = retrofitBuilder,
                    store = store,
                    dao = dao
                )
            }
        }.flow
}