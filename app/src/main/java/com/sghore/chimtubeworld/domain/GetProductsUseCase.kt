package com.sghore.chimtubeworld.domain

import com.sghore.chimtubeworld.data.repository.StoreRepository
import com.sghore.chimtubeworld.presentation.storeScreen.ProductType
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(
    private val storeRepository: StoreRepository
) {

    operator fun invoke(productType: ProductType) =
        when (productType) {
            ProductType.MarpleProduct -> {
                storeRepository.getProductsFromMarple()
            }

            ProductType.NaverProduct -> {
                storeRepository.getProductsFromNaver()
            }
        }
}