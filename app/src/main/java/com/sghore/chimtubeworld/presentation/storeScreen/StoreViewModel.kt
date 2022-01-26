package com.sghore.chimtubeworld.presentation.storeScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sghore.chimtubeworld.data.model.Goods
import com.sghore.chimtubeworld.data.model.Resource
import com.sghore.chimtubeworld.domain.GetGoodsListUseCase
import com.sghore.chimtubeworld.domain.GetStoreInfoListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class StoreViewModel @Inject constructor(
    private val getStoreInfoListUseCase: GetStoreInfoListUseCase,
    private val getGoodsListUseCase: GetGoodsListUseCase
) : ViewModel() {
    var state by mutableStateOf(StoreScreenState())
        private set

    init {
        getStoreInfo()
    }

    // 스토어의 정보를 가져옴
    fun getStoreInfo() {
        getStoreInfoListUseCase().onEach { resource ->
            state = when (resource) {
                is Resource.Success -> {
                    val storeInfoList = resource.data ?: emptyList()

                    StoreScreenState(
                        storeInfoList = storeInfoList,
                        goodsListFlow = getGoodsListUseCase(storeInfoList[0].url),
                        selectedStoreUrl = storeInfoList[0].url
                    )
                }
                is Resource.Loading -> {
                    StoreScreenState(
                        isLoading = true
                    )
                }
                is Resource.Error -> {
                    StoreScreenState(
                        errorMsg = resource.errorMsg ?: ""
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    // 스토어의 카테고리를 변경함
    fun changeCategory(url: String) {
        state = state.copy(
            selectedStoreUrl = url,
            goodsListFlow = getGoodsListUseCase(url),
            goodsList = emptyList()
        )
    }

    // 플로우를 통해 수집한 리스트를 적용함
    fun setGoodsList(list: List<Goods>) {
        state = state.copy(
            goodsList = list
        )
    }
}