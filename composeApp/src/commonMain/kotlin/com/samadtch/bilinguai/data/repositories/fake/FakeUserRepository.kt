package com.samadtch.bilinguai.data.repositories.fake

import com.samadtch.bilinguai.data.repositories.base.UserRepository
import kotlinx.coroutines.delay

class FakeUserRepository : UserRepository {

    //Success
    override suspend fun register(email: String, password: String): Result<String> {
        delay(3000)
        return Result.success("abcd")
    }

    //Success
    override suspend fun login(email: String, password: String): Result<String> {
        delay(3000)
        return Result.success("abcd")
    }

    //For Later
    override suspend fun checkEmailVerified(): Result<Boolean> {
        return Result.success(true)
    }

    override suspend fun getEmail(): Result<String> {
        return Result.success("")
    }

    //For Later
    override suspend fun verifyEmail() {}

    //Success
    override suspend fun sendPasswordResetEmail(email: String) {
        delay(3000)
    }

    //For Later
    override suspend fun updateEmail(email: String) {}

    //For Later
    override suspend fun updatePassword(newPassword: String) {}

    override fun isLoggedIn(): Boolean {
        return true
    }

    //Success
    override suspend fun getUserId(): Result<String> {
        return Result.success("abc")
    }

    //Success
    override suspend fun deleteAccount(password: String) {
        //DO NOTHING
    }

    //Success
    override suspend fun logout() {}
}