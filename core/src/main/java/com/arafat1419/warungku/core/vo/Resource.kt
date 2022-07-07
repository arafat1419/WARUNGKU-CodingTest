package com.arafat1419.warungku.core.vo

sealed class Resource<out T> {
    class Loading<out T> : Resource<T>()
    data class Success<out T>(val data: T) : Resource<T>()
    data class Failure<out T>(val message: String?) : Resource<T>()
}