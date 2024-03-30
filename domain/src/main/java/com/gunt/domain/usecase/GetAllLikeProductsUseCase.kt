package com.gunt.domain.usecase

import com.gunt.domain.repository.ProductRepository
import javax.inject.Inject

class GetAllLikeProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    operator fun invoke() =
        productRepository.getAllLikeProducts()
}