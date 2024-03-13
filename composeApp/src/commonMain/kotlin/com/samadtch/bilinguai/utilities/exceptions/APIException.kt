package com.samadtch.bilinguai.utilities.exceptions

class APIException(val code: Int, var errorMessage: String? = null): Exception() {
    companion object {
        const val API_ERROR_NETWORK = -10
        const val API_ERROR_AUTH = 10
        const val API_ERROR_RATE_LIMIT = 11 //Sending requests too quickly/exceeding quota
        //Retry Later
        const val API_ERROR_SERVER_REQUEST = 12
        const val API_ERROR_SERVER_OVERLOAD = 13

        const val API_ERROR_OTHER = 14
    }
}