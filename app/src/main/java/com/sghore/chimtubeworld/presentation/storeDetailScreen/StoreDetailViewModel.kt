package com.sghore.chimtubeworld.presentation.storeDetailScreen

import androidx.lifecycle.*
import com.sghore.chimtubeworld.data.model.Goods
import com.sghore.chimtubeworld.data.model.Resource
import com.sghore.chimtubeworld.domain.GetStorePreviewImagesUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.*

class StoreDetailViewModel @AssistedInject constructor(
    private val getStorePreviewImagesUseCase: GetStorePreviewImagesUseCase,
    @Assisted val goods: Goods?
) : ViewModel() {
    private val _state = MutableStateFlow(StoreDetailScreenState(isLoading = true))
    val state = _state.asStateFlow()

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
                        _state.update {
                            StoreDetailScreenState(
                                previewImages = resource.data ?: emptyList(),
                                selectedImage = resource.data?.get(0) ?: ""
                            )
                        }
                    }
                    is Resource.Loading -> {}
                    is Resource.Error -> {
                        _state.update {
                            StoreDetailScreenState(
                                errorMsg = resource.errorMsg ?: "오류"
                            )
                        }
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun selectPreviewImage(selectedImage: String) {
        _state.update {
            it.copy(
                selectedImage = selectedImage
            )
        }
    }
}