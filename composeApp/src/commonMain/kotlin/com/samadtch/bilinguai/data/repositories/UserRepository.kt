package com.samadtch.bilinguai.data.repositories

import com.samadtch.bilinguai.data.datasources.remote.base.AuthRemoteSource
import com.samadtch.bilinguai.data.repositories.base.UserRepository
import com.samadtch.bilinguai.di.Dispatcher
import com.samadtch.bilinguai.utilities.exceptions.AuthException
import kotlinx.coroutines.withContext

class UserRepository(
    private val authRemoteDataSource: AuthRemoteSource,
    private val dispatcher: Dispatcher
) : UserRepository {

    override suspend fun register(email: String, password: String): Result<String> =
        withContext(dispatcher.io) {
            try {
                val userId = authRemoteDataSource.register(email,password).getOrThrow()
                Result.success(userId)
            } catch (e: AuthException) {
                Result.failure(e)
            }
        }

    override suspend fun login(email: String, password: String): Result<String> =
        withContext(dispatcher.io) {
            try {
                val userId = authRemoteDataSource.login(email, password).getOrThrow()
                Result.success(userId)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun checkEmailVerified(): Result<Boolean> = withContext(dispatcher.io) {
        authRemoteDataSource.checkEmailVerified()
    }

    override suspend fun getEmail() = withContext(dispatcher.io) { authRemoteDataSource.getEmail() }

    override suspend fun verifyEmail() = withContext(dispatcher.io) {
        authRemoteDataSource.verifyEmail()
    }

    override suspend fun sendPasswordResetEmail(email: String) = withContext(dispatcher.io) {
        authRemoteDataSource.sendPasswordResetEmail(email)
    }

    override suspend fun updateEmail(email: String) = withContext(dispatcher.io) {
        authRemoteDataSource.updateEmail(email)
    }

    override suspend fun updatePassword(newPassword: String) = withContext(dispatcher.io) {
        authRemoteDataSource.updatePassword(newPassword)
    }

    override fun isLoggedIn() = authRemoteDataSource.isLoggedIn()

    override suspend fun getUserId() = withContext(dispatcher.io) {
        authRemoteDataSource.getUserId()
    }

    override suspend fun deleteAccount(password: String) = withContext(dispatcher.io) {
        authRemoteDataSource.reauthenticate(password) {
            authRemoteDataSource.deleteAccount()
        }
    }

    override suspend fun logout() = withContext(dispatcher.io) {
        authRemoteDataSource.logout()
    }
}