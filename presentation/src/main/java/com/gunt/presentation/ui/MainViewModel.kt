package com.gunt.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gunt.domain.model.Response
import com.gunt.domain.model.SectionInfo
import com.gunt.domain.usecase.GetSectionsWithProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getSectionsWithProductsUseCase: GetSectionsWithProductsUseCase
) : ViewModel() {

    private val _sections: MutableStateFlow<List<SectionInfo>> = MutableStateFlow(emptyList())
    val sections = _sections.asStateFlow()
    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    var isLoading = _isLoading.asStateFlow()
    private var currentPage: Int? = 1

    init {
        loadNextPage()
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
                        // TODO: 에러 처리
                    }
                }
            }
        }
    }
}