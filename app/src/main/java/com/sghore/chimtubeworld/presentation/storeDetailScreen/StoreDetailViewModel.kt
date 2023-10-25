package com.sghore.chimtubeworld.presentation.storeDetailScreen

import androidx.lifecycle.*
import com.sghore.chimtubeworld.data.model.Goods
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.*

class StoreDetailViewModel @AssistedInject constructor(
    @Assisted val goods: Goods?
) : ViewModel() {
    private val _state = MutableStateFlow(
        StoreDetailScreenState(
            previewImages = goods?.previewImages ?: listOf(),
            selectedImage = goods?.previewImages?.get(0) ?: ""
        )
    )
    val state = _state.asStateFlow()

    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(goods: Goods?): StoreDetailViewModel
    }

    companion object {
        fun provideFactory(
            assistedFactory: AssistedFactory,
            goods: Goods?
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {

            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(goods) as T
            }
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