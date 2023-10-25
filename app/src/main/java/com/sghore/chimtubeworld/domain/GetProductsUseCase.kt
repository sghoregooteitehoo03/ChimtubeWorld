package com.sghore.chimtubeworld.domain

import com.sghore.chimtubeworld.data.repository.StoreRepository
import com.sghore.chimtubeworld.other.Contents
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(
    private val storeRepository: StoreRepository
) {

    operator fun invoke(baseUrl: String) =
        when (baseUrl) {
            Contents.MARPLESHOP_BASE_URL -> {
                storeRepository.getProductsFromMarple()
            }

            Contents.NAVERSTORE_BASE_URL -> {
                storeRepository.getProductsFromNaver()
            }

            else -> {
                storeRepository.getProductsFromMarple()
            }
        }
}