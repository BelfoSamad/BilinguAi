package com.samadtch.bilinguai.data.datasources.remote

import org.koin.core.module.Module

interface AuthRemoteSource {

    suspend fun register(email: String, password: String): Result<String>

    suspend fun login(email: String, password: String): Result<String>

    suspend fun checkEmailVerified(): Result<Boolean>

    suspend fun verifyEmail()

    suspend fun getEmail(): Result<String>

    suspend fun sendPasswordResetEmail(email: String)

    suspend fun updateEmail(email: String)

    suspend fun updatePassword(newPassword: String)

    suspend fun getUserId(): Result<String>

    suspend fun logout()

    suspend fun deleteAccount()

    suspend fun reauthenticate(password: String, callback: suspend () -> Unit)

    fun isLoggedIn(): Boolean

}

expect fun getAuthRemoteSource(): Module