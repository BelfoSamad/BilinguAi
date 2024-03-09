package com.samadtch.bilinguai.data.repositories.base

interface UserRepository {

    suspend fun register(email: String, password: String): Result<String>

    suspend fun login(email: String, password: String): Result<String>

    suspend fun checkEmailVerified(): Result<Boolean>

    suspend fun getEmail(): Result<String>

    suspend fun verifyEmail()

    suspend fun sendPasswordResetEmail(email: String)

    suspend fun updateEmail(email: String)

    suspend fun updatePassword(newPassword: String)

    fun isLoggedIn(): Boolean

    suspend fun getUserId(): Result<String>

    suspend fun deleteAccount(password: String)

    suspend fun logout()

}