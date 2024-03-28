package com.gunt.domain.model

data class Product(
    val id: Int,
    val name: String,
    val image: String,
    val originalPrice: Int,
    val discountedPrice: Int,
    val isSoldOut: Boolean,
    val isLike: Boolean? = false
)