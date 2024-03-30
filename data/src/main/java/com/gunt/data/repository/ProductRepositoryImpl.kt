package com.gunt.data.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.gunt.domain.model.Product
import com.gunt.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) : ProductRepository {

    private val _likeProducts = MutableStateFlow((emptyList<Product>()))
    val likeProducts = _likeProducts.asStateFlow()
    init {
        _likeProducts.update {
            val type = object : TypeToken<List<Product>>() {}.type
            val listStringData = sharedPreferences.getString(SHARED_PREFERENCE_KEY, "")
            if (listStringData.isNullOrEmpty()) {
                return@update emptyList()
            }
            gson.fromJson(listStringData, type)
        }
    }

    override fun getAllLikeProducts(): Flow<List<Product>> {
        return likeProducts
    }

    override suspend fun insertLikeProduct(product: Product) {
        val products = _likeProducts.value + product
        sharedPreferences.edit { putString(SHARED_PREFERENCE_KEY, gson.toJson(products)) }
        _likeProducts.update { products }
    }

    override suspend fun deleteLikeProduct(product: Product) {
        val products = _likeProducts.value
            .filter { item -> item.id != product.id }
        sharedPreferences.edit { putString(SHARED_PREFERENCE_KEY, gson.toJson(products)) }
        _likeProducts.update { products }
    }

    companion object {
        private const val SHARED_PREFERENCE_KEY = "like_products_storage"
    }
}