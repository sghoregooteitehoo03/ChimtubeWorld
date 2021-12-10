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
    val cafePosts = repository.getCafePosts().cachedIn(viewModelScope)

    val cafeInfoData: LiveData<Channel> = _cafeInfoData

    // 카페의 정보를 가져옴
    fun getCafeInfo() = viewModelScope.launch {

        _cafeInfoData.value = CoroutineScope(Dispatchers.IO).async {
            repository.getCafeInfo()
        }.await()
    }
}