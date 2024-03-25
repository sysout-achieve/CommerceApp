package com.gunt.domain.usecase

import com.gunt.domain.repository.ProductRepository
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(private val repository: ProductRepository) {
    fun getProducts(){
        return repository.getProducts()
    }
}