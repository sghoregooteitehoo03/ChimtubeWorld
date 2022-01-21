package com.sghore.chimtubeworld.presentation.storeScreen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sghore.chimtubeworld.data.model.Resource
import com.sghore.chimtubeworld.domain.GetGoodsListUseCase
import com.sghore.chimtubeworld.domain.GetStoreInfoListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoreViewModel @Inject constructor(
    private val getStoreInfoListUseCase: GetStoreInfoListUseCase,
    private val getGoodsListUseCase: GetGoodsListUseCase
) : ViewModel() {
    private val _state = mutableStateOf(StoreScreenState())
    val state: State<StoreScreenState> = _state

//    private val _isLoading = MutableLiveData(true) // 로딩 여부
//    private val _storeImages = MutableLiveData<List<Channel>>(listOf()) // 스토어 정보
//    private val _goodsList = MutableLiveData<List<Goods>?>(null) // 선택된 스토어에 제공하고 있는 상품리스트
//
//    val isLoading: LiveData<Boolean> = _isLoading
//    val storeImages: LiveData<List<Channel>> = _storeImages
//    val goodsList: LiveData<List<Goods>?> = _goodsList
//
//    val selectedPos = MutableLiveData(0) // 선택된 스토어에 위치
//    val currentUrl = MutableLiveData("") // 선택된 스토어에 url

    init {
        getStoreInfo()
    }

    // 스토어의 정보를 가져옴
    fun getStoreInfo() {
        getStoreInfoListUseCase().onEach { resource ->
            when (resource) {
                is Resource.Success -> {
                    val storeInfoList = resource.data ?: emptyList()

                    _state.value = StoreScreenState(
                        storeInfoList = storeInfoList,
                        goodsList = getGoodsListUseCase(storeInfoList[0].url),
                        selectedStoreUrl = storeInfoList[0].url
                    )
                }
                is Resource.Loading -> {
                    _state.value = StoreScreenState(
                        isLoading = true
                    )
                }
                is Resource.Error -> {
                    _state.value = StoreScreenState(
                        errorMsg = resource.errorMsg ?: ""
                    )
                }
            }
        }.launchIn(viewModelScope)
//        _storeImages.value = CoroutineScope(Dispatchers.IO).async {
//            repository.getStoreInfo()
//        }.await()!!
//        _isLoading.value = false // 로딩 끝
    }

    fun changeCategory(url: String) {
        _state.value = _state.value.copy(
            selectedStoreUrl = url,
            goodsList = getGoodsListUseCase(url)
        )
    }
}