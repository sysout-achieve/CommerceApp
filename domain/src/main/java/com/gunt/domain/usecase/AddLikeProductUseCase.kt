package com.gunt.domain.usecase

import com.gunt.domain.model.Product
import com.gunt.domain.repository.ProductRepository
import javax.inject.Inject

class AddLikeProductUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(product: Product) =
        productRepository.insertLikeProduct(product)
}