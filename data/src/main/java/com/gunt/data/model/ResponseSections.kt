package com.gunt.data.model

import com.google.gson.annotations.SerializedName
import com.gunt.domain.model.SectionInfo
import com.gunt.domain.model.SectionType

data class ResponseSections(
    @SerializedName("data")
    val data: List<Section>,
    @SerializedName("paging")
    val paging: Paging?
) {
    data class Section(
        @SerializedName("id")
        val id: Int,
        @SerializedName("title")
        val title: String,
        @SerializedName("type")
        val type: String,
        @SerializedName("url")
        val url: String
    ) {
        fun toDomainModel(): SectionInfo =
            SectionInfo(
                id = id,
                title = title,
                type = getSectionType(type),
                url = url
            )

        private fun getSectionType(typeStr: String): SectionType {
            return when (typeStr) {
                SectionType.VERTICAL.typeName -> SectionType.VERTICAL
                SectionType.HORIZONTAL.typeName -> SectionType.HORIZONTAL
                else -> SectionType.GRID
            }
        }
    }

    data class Paging(
        @SerializedName("next_page")
        val nextPage: Int?
    )
}