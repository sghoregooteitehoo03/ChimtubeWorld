package com.sghore.chimtubeworld.presentation.storeScreen

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
                        goodsList = getGoodsListUseCase(storeInfoList[0].url),
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

    fun changeCategory(url: String) {
        state = state.copy(
            selectedStoreUrl = url,
            goodsList = getGoodsListUseCase(url)
        )
    }
}