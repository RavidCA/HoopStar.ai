package com.starhoop.hoopstar.core

sealed interface DataResult<out T> {
    data class Success<T>(val data: T) : DataResult<T>
    data class Error(val message: String, val code: Int? = null) : DataResult<Nothing>
}