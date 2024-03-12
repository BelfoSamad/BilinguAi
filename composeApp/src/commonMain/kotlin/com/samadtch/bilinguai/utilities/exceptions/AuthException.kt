package com.samadtch.bilinguai.utilities.exceptions

class AuthException(val code: Int) : Exception() {
    companion object {
        //Errors
        const val AUTH_ERROR_NETWORK = 999
        const val AUTH_ERROR_WRONG_CREDENTIALS = 0
        const val AUTH_ERROR_INVALID_EMAIL = 1
        const val AUTH_ERROR_WRONG_EMAIL = 3//Email doesn't exist or account disabled
        const val AUTH_ERROR_WRONG_PASSWORD = 2
        const val AUTH_ERROR_EMAIL_ALREADY_IN_USE = 4

        //User is disabled, deleted or credentials no longer valid
        const val AUTH_ERROR_USER_NOT_FOUND = 6
        const val AUTH_ERROR_WEAK_PASSWORD = 7
        const val AUTH_ERROR_USER_LOGGED_OUT = 8
        const val AUTH_ERROR_SHOULD_REAUTHENTICATE = 3//Ask user to reauthenticate then redo action
        const val AUTH_ERROR_USER_WRONG_CREDENTIALS = 9
    }
}
