package com.gunt.domain.repository

import com.gunt.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getAllLikeProducts(): Flow<List<Product>>

    suspend fun insertLikeProduct(product: Product)

    suspend fun deleteLikeProduct(product: Product)
}