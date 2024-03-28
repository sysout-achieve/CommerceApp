package com.gunt.data.repository

import com.gunt.data.remote.MainSectionService
import com.gunt.domain.model.Product
import com.gunt.domain.model.Sections
import com.gunt.domain.repository.MainSectionRepository
import javax.inject.Inject

class MainSectionRepositoryImpl @Inject constructor(
    private val mainSectionService: MainSectionService
) : MainSectionRepository {
    override suspend fun getSectionProducts(sectionId: Int): List<Product> {
        val response = mainSectionService.getSectionProducts(sectionId)
        return response.data.map { it.toDomainModel() }
    }

    override suspend fun getSections(page: Int): Sections {
        val responseSection = mainSectionService.getSections(page)
        return Sections(
            sectionInfoList = responseSection.data.map { it.toDomainModel() },
            nextPage = responseSection.paging?.nextPage
        )
    }
}