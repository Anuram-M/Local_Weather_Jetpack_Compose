package com.ram.core_domain

sealed class NETWORK_RESULT<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T?) : NETWORK_RESULT<T>(data)
    class Loading<T>() : NETWORK_RESULT<T>()
    class Error<T>(data: T? = null, message: String?) : NETWORK_RESULT<T>(data, message)
}