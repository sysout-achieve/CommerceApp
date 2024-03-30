package com.gunt.domain.usecase

import com.gunt.domain.model.Product
import com.gunt.domain.model.Response
import com.gunt.domain.model.Sections
import com.gunt.domain.repository.MainSectionRepository
import com.gunt.domain.repository.ProductRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

// TODO: 병렬 처리에 관해 테스트 필요
class GetSectionsWithProductsUseCase @Inject constructor(
    private val sectionRepository: MainSectionRepository,
    private val productRepository: ProductRepository
) {
    operator fun invoke(page: Int): Flow<Response<Sections>> = flow {
        try {
            emit(Response.Loading)
            val sections = sectionRepository.getSections(page)
            val likeProducts = productRepository.getAllLikeProducts().first()
            var deferredList: List<Deferred<List<Product>>>
            withContext(Dispatchers.IO) {
                deferredList = sections.sectionInfoList.map { sectionInfo ->
                    async {
                        sectionRepository.getSectionProducts(sectionInfo.id).map {
                            it.copy(isLike = likeProducts.contains(it))
                        }
                    }
                }

                val productList = deferredList.awaitAll()
                sections.sectionInfoList.mapIndexed { index, sectionInfo ->
                    sectionInfo.products = productList[index]
                }
            }
            emit(Response.Success(sections))
        } catch (e: Exception) {
            emit(Response.Error(e))
        }
    }
}