package com.gunt.domain.repository

import com.gunt.domain.model.Product
import com.gunt.domain.model.Sections

interface MainSectionRepository {

    suspend fun getSections(page: Int): Sections

    suspend fun getSectionProducts(sectionId: Int): List<Product>
}