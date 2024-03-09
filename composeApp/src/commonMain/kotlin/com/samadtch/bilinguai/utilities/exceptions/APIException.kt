package com.samadtch.bilinguai.utilities.exceptions

class APIException(val code: Int, var errorMessage: String? = null): Exception() {
    companion object {
        const val API_ERROR_AUTH = 0
        const val API_ERROR_RATE_LIMIT = 1 //Sending requests too quickly/exceeding quota
        //Retry Later
        const val API_ERROR_SERVER_REQUEST = 2
        const val API_ERROR_SERVER_OVERLOAD = 3

        const val API_ERROR_OTHER = 4
    }
}