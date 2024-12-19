package com.submision.coursestory.data.result

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: String) : Result<Nothing>()
    object Loading : Result<Nothing>()
}