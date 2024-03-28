package com.samadtch.bilinguai.utilities.exceptions

class DataException(val code: Int) : Exception() {
    companion object {
        const val DATA_ERROR_NETWORK = -30 //Either concurrency problem or network interruptions
        const val DATA_ERROR_SERVICE = 30 //An error most likely related to server
        private const val DATA_ERROR_DEADLINE_EXCEEDED = 31 //Either slow internet connection or big query
        const val DATA_ERROR_NOT_FOUND = 32 //Query not founded or empty list
        private const val DATA_ERROR_ALREADY_EXISTS = 33
        private const val DATA_ERROR_CONCURRENCY = 34
        private const val DATA_ERROR_UNAUTHENTICATED = 35

        //Firebase - Errors
        val FIRESTORE_SERVICE_ERRORS = listOf(2, 3, 7, 8, 9, 11, 12, 13, 14, 15)
        val FIRESTORE_NETWORK_ERRORS = listOf(1, 2, 14)
        val FIRESTORE_CONCURRENCY_ERRORS = listOf(1, 10)

        const val FIRESTORE_ERROR_DEADLINE = 4
        const val FIRESTORE_ERROR_NOT_FOUND = 5
        private const val FIRESTORE_ERROR_ALREADY_EXISTS = 6
        private const val FIRESTORE_ERROR_UNAUTHENTICATED = 16


        //Handle Error
        fun handleError(errorCode: Int) {
            when (errorCode) {
                in FIRESTORE_NETWORK_ERRORS -> throw DataException(DATA_ERROR_NETWORK)
                in FIRESTORE_CONCURRENCY_ERRORS -> throw DataException(DATA_ERROR_CONCURRENCY)
                FIRESTORE_ERROR_DEADLINE -> throw DataException(DATA_ERROR_DEADLINE_EXCEEDED)
                FIRESTORE_ERROR_NOT_FOUND -> throw DataException(DATA_ERROR_NOT_FOUND)
                FIRESTORE_ERROR_ALREADY_EXISTS -> throw DataException(DATA_ERROR_ALREADY_EXISTS)
                FIRESTORE_ERROR_UNAUTHENTICATED -> throw DataException(DATA_ERROR_UNAUTHENTICATED)
                else -> throw DataException(DATA_ERROR_SERVICE)
            }
        }

        //Handle Error
        /*
        fun getError(errorCode: Int): DataException {
            return when (errorCode) {
                in FIRESTORE_NETWORK_ERRORS -> DataException(DATA_ERROR_NETWORK)
                in FIRESTORE_CONCURRENCY_ERRORS -> DataException(DATA_ERROR_CONCURRENCY)
                FIRESTORE_ERROR_DEADLINE -> DataException(DATA_ERROR_DEADLINE_EXCEEDED)
                FIRESTORE_ERROR_NOT_FOUND -> DataException(DATA_ERROR_NOT_FOUND)
                FIRESTORE_ERROR_ALREADY_EXISTS -> DataException(DATA_ERROR_ALREADY_EXISTS)
                FIRESTORE_ERROR_UNAUTHENTICATED -> DataException(DATA_ERROR_UNAUTHENTICATED)
                else -> DataException(DATA_ERROR_SERVICE)
            }
        }*/
    }
}