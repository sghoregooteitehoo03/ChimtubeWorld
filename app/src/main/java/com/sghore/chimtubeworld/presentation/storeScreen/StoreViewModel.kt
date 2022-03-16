package com.sghore.chimtubeworld.presentation.storeScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sghore.chimtubeworld.data.model.Resource
import com.sghore.chimtubeworld.domain.GetGoodsListUseCase
import com.sghore.chimtubeworld.domain.GetStoreInfoListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class StoreViewModel @Inject constructor(
    private val getStoreInfoListUseCase: GetStoreInfoListUseCase,
    private val getGoodsListUseCase: GetGoodsListUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(StoreScreenState(isLoading = true))
    val state: StateFlow<StoreScreenState> = _state

    private var job: Job? = null

    init {
        getStoreInfo()
    }

    // 스토어의 정보를 가져옴
    fun getStoreInfo() {
        getStoreInfoListUseCase().onEach { resource ->
            when (resource) {
                is Resource.Success -> {
                    val storeInfoList = resource.data ?: emptyList()

                    _state.update {
                        StoreScreenState(
                            storeInfoList = storeInfoList
                        )
                    }
                    changeCategory(url = storeInfoList[0].url)
                }
                is Resource.Loading -> {}
                is Resource.Error -> {
                    _state.update {
                        StoreScreenState(
                            errorMsg = resource.errorMsg ?: ""
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    // 스토어의 카테고리를 변경함
    fun changeCategory(url: String) {
        job?.cancel() // 이전 작업이 존재하면 취소함

        _state.update {
            it.copy(
                selectedStoreUrl = url,
                goodsList = emptyList()
            )
        }
        job = getGoodsListUseCase(url).onEach { goods ->
            _state.update {
                it.copy(
                    goodsList = goods
                )
            }
        }.launchIn(viewModelScope)
    }
}