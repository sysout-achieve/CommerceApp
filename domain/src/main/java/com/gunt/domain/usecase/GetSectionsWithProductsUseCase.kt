package com.gunt.domain.usecase

import com.gunt.domain.model.Response
import com.gunt.domain.model.Sections
import com.gunt.domain.repository.MainSectionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

// TODO: 병렬 처리에 관해 테스트 필요
class GetSectionsWithProductsUseCase @Inject constructor(
    private val sectionRepository: MainSectionRepository
) {
    operator fun invoke(page: Int): Flow<Response<Sections>> = flow {
        try {
            emit(Response.Loading)
            val sections = sectionRepository.getSections(page)
            var deferredList = emptyList<Deferred<Unit>>()
            CoroutineScope(Dispatchers.IO).launch {
                deferredList = sections.sectionInfoList.map { sectionInfo ->
                    async {
                        val products = sectionRepository.getSectionProducts(sectionInfo.id)
                        sectionInfo.products = products
                    }
                }
            }

            deferredList.awaitAll()

            emit(Response.Success(sections))
        } catch (e: Exception) {
            emit(Response.Error(e))
        }
    }
}