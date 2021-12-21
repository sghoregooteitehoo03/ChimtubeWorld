package com.sghore.chimtubeworld.viewmodel.cafeFrag

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sghore.chimtubeworld.data.Channel
import com.sghore.chimtubeworld.data.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CafeViewModel @Inject constructor(
    private val repository: CafeRepository
) : ViewModel() {

    private val _cafeInfoData = MutableLiveData<Channel>(null) // 카페 정보
    val cafeInfoData: LiveData<Channel> = _cafeInfoData

    val selectedPos = MutableLiveData(0) // 카테고리 선택된 위치
    val categoryId = MutableLiveData<Int>() // 카테고리 아이디
    val cafePosts = repository.getCafePosts(categoryId) // 카페 게시글 리스트
        .cachedIn(viewModelScope)
        .asLiveData(viewModelScope.coroutineContext)

    // 카페의 정보를 가져옴
    fun getCafeInfo() = viewModelScope.launch {
        _cafeInfoData.value = CoroutineScope(Dispatchers.IO).async {
            repository.getCafeInfo()
        }.await()
    }
}