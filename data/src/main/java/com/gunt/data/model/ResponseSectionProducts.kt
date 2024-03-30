package com.gunt.data.model

import com.google.gson.annotations.SerializedName
import com.gunt.domain.model.Product

data class ResponseSectionProducts(
    @SerializedName("data")
    val data: List<SectionProduct>
) {
    data class SectionProduct(
        @SerializedName("id")
        val id: Double,
        @SerializedName("name")
        val name: String,
        @SerializedName("image")
        val image: String,
        @SerializedName("originalPrice")
        val originalPrice: Int,
        @SerializedName("discountedPrice")
        val discountedPrice: Int?,
        @SerializedName("isSoldOut")
        val isSoldOut: Boolean
    ) {
        fun toDomainModel(): Product = Product(
            id = id,
            name = name,
            image = image,
            originalPrice = originalPrice,
            discountedPrice = discountedPrice,
            isSoldOut = isSoldOut
        )
    }
}