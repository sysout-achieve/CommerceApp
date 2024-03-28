package com.gunt.data.remote

import com.gunt.data.model.ResponseSectionProducts
import com.gunt.data.model.ResponseSections
import retrofit2.http.GET
import retrofit2.http.Query

interface MainSectionService {

    @GET("sections")
    suspend fun getSections(
        @Query("page") page: Int
    ): ResponseSections

    @GET("section/products")
    suspend fun getSectionProducts(
        @Query("sectionId") sectionId: Int? = 0
    ): ResponseSectionProducts
}