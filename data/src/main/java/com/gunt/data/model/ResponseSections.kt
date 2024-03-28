package com.gunt.data.model

import com.google.gson.annotations.SerializedName
import com.gunt.domain.model.SectionInfo

data class ResponseSections(
    @SerializedName("data")
    val data: List<Section>,
    @SerializedName("paging")
    val paging: Paging
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
                type = type,
                url = url
            )
    }

    data class Paging(
        @SerializedName("next_page")
        val nextPage: Int?
    )
}