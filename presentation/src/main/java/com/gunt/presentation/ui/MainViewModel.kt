package com.gunt.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gunt.domain.model.Product
import com.gunt.domain.model.Response
import com.gunt.domain.model.SectionInfo
import com.gunt.domain.usecase.AddLikeProductUseCase
import com.gunt.domain.usecase.GetAllLikeProductsUseCase
import com.gunt.domain.usecase.GetSectionsWithProductsUseCase
import com.gunt.domain.usecase.RemoveLikeProductUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getSectionsWithProductsUseCase: GetSectionsWithProductsUseCase,
    private val addLikeProductUseCase: AddLikeProductUseCase,
    private val removeLikeProductUseCase: RemoveLikeProductUseCase,
    private val getAllLikeProductsUseCase: GetAllLikeProductsUseCase
) : ViewModel() {

    private val _sections: MutableStateFlow<List<SectionInfo>> = MutableStateFlow(emptyList())
    val sections = _sections.asStateFlow()
    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    private val _errorText: MutableStateFlow<String> = MutableStateFlow("")
    val errorText = _errorText.asStateFlow()
    private var currentPage: Int? = 1
    init {
        loadNextPage()
        collectAllLikeProducts()
    }

    fun loadNextPage() {
        if (currentPage == null || isLoading.value) return
        _isLoading.update { true }
        viewModelScope.launch(Dispatchers.IO) {
            getSectionsWithProductsUseCase(currentPage!!).collect { response ->
                when (response) {
                    Response.Loading -> _isLoading.update { true }

                    is Response.Success -> {
                        _sections.update { sections -> sections + response.data.sectionInfoList }
                        currentPage = response.data.nextPage
                        _isLoading.update { false }
                    }

                    is Response.Error -> {
                        _isLoading.update { false }
                        _errorText.update { "화면을 아래로 당겨 refresh\n${response.e.localizedMessage}" }
                        _sections.update { emptyList() }
                    }
                }
            }
        }
    }

    private fun collectAllLikeProducts() {
        getAllLikeProductsUseCase()
            .onEach { allLikeProducts ->
                _sections.update { sectionInfoList ->
                    sectionInfoList.map { sectionInfo ->
                        sectionInfo.copy(
                            products = sectionInfo.products.map { product ->
                                product.copy(isLike = allLikeProducts.find { it.id == product.id } != null)
                            }
                        )
                    }
                }
            }.launchIn(viewModelScope)
    }

    fun onRefresh() {
        currentPage = 1
        _sections.update { emptyList() }
        loadNextPage()
    }

    fun addLikeProduct(product: Product) {
        viewModelScope.launch(Dispatchers.IO) {
            addLikeProductUseCase(product)
        }
    }

    fun removeLikeProduct(product: Product) {
        viewModelScope.launch(Dispatchers.IO) {
            removeLikeProductUseCase(product)
        }
    }
}