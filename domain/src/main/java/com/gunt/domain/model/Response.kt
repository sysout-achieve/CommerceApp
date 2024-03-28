package com.gunt.domain.model

sealed class Response<out T: Any?> {
    data object Loading: Response<Nothing>()

    data class Success<out T: Any?>(val data: T): Response<T>()

    data class Error(val e: Exception): Response<Nothing>()
}