package com.gunt.domain.model

data class Product(
    val id: Double,
    val name: String,
    val image: String,
    val originalPrice: Double,
    val discountPrice: Double,
    val isSoldOut: Boolean,
)