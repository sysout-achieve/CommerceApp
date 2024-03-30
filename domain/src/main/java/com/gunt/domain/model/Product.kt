package com.gunt.domain.model

data class Product(
    val id: Double,
    val name: String,
    val image: String,
    val originalPrice: Int,
    val discountedPrice: Int?,
    val isSoldOut: Boolean,
    var isLike: Boolean? = false
){
    fun getSaleStatus():SalesStatus{
        if (isSoldOut) return SalesStatus.SOLD_OUT
        if (discountedPrice != null) return SalesStatus.ON_DISCOUNT
        return SalesStatus.ON_SALE
    }
}