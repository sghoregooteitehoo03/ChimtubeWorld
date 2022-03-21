package com.sghore.chimtubeworld.domain

import com.sghore.chimtubeworld.data.model.Channel
import com.sghore.chimtubeworld.data.model.Resource
import com.sghore.chimtubeworld.data.repository.StoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetStoreInfoListUseCase @Inject constructor(
    private val repository: StoreRepository
) {
    operator fun invoke(): Flow<Resource<List<Channel>>> = flow {
        try {
            val linkList = repository.getStoreLinkData() // 스토어 링크 리스트
            val storeInfoList = repository.getStoreInfo(linkList) // 스토어 정보 리스트

            emit(Resource.Success<List<Channel>>(storeInfoList))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error<List<Channel>>("데이터를 불러오는데 오류가 발생하였습니다."))
        }
    }
}