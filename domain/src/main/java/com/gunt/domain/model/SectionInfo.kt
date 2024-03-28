package com.gunt.domain.model

data class SectionInfo(
    val title: String,
    val id: Int,
    val type: SectionType,
    val url: String
) {
    var products: List<Product> = emptyList()
}