package com.sghore.chimtubeworld.presentation.storeDetailScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.*
import com.sghore.chimtubeworld.data.model.Goods
import com.sghore.chimtubeworld.data.model.Resource
import com.sghore.chimtubeworld.domain.GetStorePreviewImagesUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class StoreDetailViewModel @AssistedInject constructor(
    private val getStorePreviewImagesUseCase: GetStorePreviewImagesUseCase,
    @Assisted val goods: Goods?
) : ViewModel() {
    var state by mutableStateOf(StoreDetailScreenState())
        private set

    init {
        getStoreDetail(goods)
    }

    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(goods: Goods?): StoreDetailViewModel
    }

    companion object {
        fun provideFactory(
            assistedFactory: StoreDetailViewModel.AssistedFactory,
            goods: Goods?
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {

            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(goods) as T
            }
        }
    }

    // 프리뷰 이미지들을 가져옴
    fun getStoreDetail(goods: Goods?) {
        goods?.let {
            getStorePreviewImagesUseCase(it).onEach { resource ->
                when (resource) {
                    is Resource.Success -> {
                        state = StoreDetailScreenState(
                            previewImages = resource.data ?: emptyList(),
                            selectedImage = resource.data?.get(0) ?: ""
                        )
                    }
                    is Resource.Loading -> {
                        state = StoreDetailScreenState(
                            isLoading = true
                        )
                    }
                    is Resource.Error -> {
                        state = StoreDetailScreenState(
                            errorMsg = resource.errorMsg ?: "오류"
                        )
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun selectPreviewImage(selectedImage: String) {
        state = state.copy(
            selectedImage = selectedImage
        )
    }
}