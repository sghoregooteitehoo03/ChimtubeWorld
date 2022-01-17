package com.sghore.chimtubeworld.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.google.firebase.firestore.FirebaseFirestore
import com.sghore.chimtubeworld.R
import com.sghore.chimtubeworld.data.repository.dataSource.TwitchPagingSource
import com.sghore.chimtubeworld.data.repository.dataSource.YoutubePagingSource
import com.sghore.chimtubeworld.data.db.Dao
import com.sghore.chimtubeworld.other.Contents
import com.sghore.chimtubeworld.data.retrofit.RetrofitService
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
                val retrofitService = retrofitBuilder.baseUrl(Contents.YOUTUBE_API_URL)
                    .build()
                    .create(RetrofitService::class.java)

                YoutubePagingSource(
                    channelId = channelId,
                    retrofitService = retrofitService,
                    dao = dao,
                )
            } else { // 트위치 영상일 때
                val retrofitService = retrofitBuilder.baseUrl(Contents.TWITCH_API_URL)
                    .build()
                    .create(RetrofitService::class.java)

                TwitchPagingSource(
                    channelId = channelId,
                    retrofitService = retrofitService,
                    store = store,
                    dao = dao
                )
            }
        }.flow
}