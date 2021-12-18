package com.sghore.chimtubeworld.viewmodel.videosFrag

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.sghore.chimtubeworld.adapter.paging.VideosPagingSource
import com.sghore.chimtubeworld.other.Contents
import com.sghore.chimtubeworld.retrofit.RetrofitService
import retrofit2.Retrofit
import javax.inject.Inject

class VideosRepository @Inject constructor(
    private val retrofitBuilder: Retrofit.Builder
) {

    fun getVideos(channelId: String, categoryUrl: String) =
        Pager(PagingConfig(20)) {
            VideosPagingSource(
                channelId = channelId,
                categoryUrl = categoryUrl,
                retrofitBuilder = retrofitBuilder
            )
        }.flow
}