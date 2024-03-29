package com.sghore.chimtubeworld.presentation.storeScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.sghore.chimtubeworld.R
import com.sghore.chimtubeworld.data.model.GoodsChannelInfo
import com.sghore.chimtubeworld.domain.GetProductsUseCase
import com.sghore.chimtubeworld.other.Contents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class StoreViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(StoreScreenState(isLoading = true))
    val state: StateFlow<StoreScreenState> = _state

    init {
        _state.update {
            StoreScreenState(
                storeInfoList = listOf(
                    GoodsChannelInfo(
                        channelImage = R.drawable.marple,
                        channelName = "침착맨",
                        baseUrl = Contents.MARPLESHOP_BASE_URL,
                        productType = ProductType.MarpleProduct
                    ),
                    GoodsChannelInfo(
                        channelImage = R.drawable.naver,
                        channelName = "얼렁뚱땅 상점",
                        baseUrl = Contents.NAVERSTORE_BASE_URL,
                        productType = ProductType.NaverProduct
                    )
                ),
                marpleGoodsList = getProductsUseCase(ProductType.MarpleProduct)
                    .cachedIn(viewModelScope),
                naverGoodsList = getProductsUseCase(ProductType.NaverProduct)
                    .cachedIn(viewModelScope)
            )
        }
    }

    // 스토어의 카테고리를 변경함
    fun changeCategory(productType: ProductType) {
        _state.update { it.copy(selectedProductType = productType) }
    }
}