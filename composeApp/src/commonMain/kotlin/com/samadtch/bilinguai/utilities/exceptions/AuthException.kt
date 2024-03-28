package com.samadtch.bilinguai.utilities.exceptions

class AuthException(val code: Int) : Exception() {
    companion object {
        //Errors
        const val AUTH_ERROR_NETWORK = -20
        //const val AUTH_ERROR_WRONG_CREDENTIALS = 20
        const val AUTH_ERROR_INVALID_EMAIL = 21
        const val AUTH_ERROR_WRONG_PASSWORD = 22
        const val AUTH_ERROR_WRONG_EMAIL = 23//Email doesn't exist or account disabled
        const val AUTH_ERROR_EMAIL_ALREADY_IN_USE = 24

        //User is disabled, deleted or credentials no longer valid
        const val AUTH_ERROR_USER_NOT_FOUND = 25
        const val AUTH_ERROR_WEAK_PASSWORD = 26
        const val AUTH_ERROR_USER_LOGGED_OUT = 27
        const val AUTH_ERROR_SHOULD_REAUTHENTICATE = 28//Ask user to reauthenticate then redo action
        const val AUTH_ERROR_USER_WRONG_CREDENTIALS = 29
    }
}