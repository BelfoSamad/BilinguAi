package com.samadtch.bilinguai.data.datasources.remote.base

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.samadtch.bilinguai.utilities.exceptions.AuthException
import com.samadtch.bilinguai.utilities.exceptions.AuthException.Companion.AUTH_ERROR_EMAIL_ALREADY_IN_USE
import com.samadtch.bilinguai.utilities.exceptions.AuthException.Companion.AUTH_ERROR_INVALID_EMAIL
import com.samadtch.bilinguai.utilities.exceptions.AuthException.Companion.AUTH_ERROR_NETWORK
import com.samadtch.bilinguai.utilities.exceptions.AuthException.Companion.AUTH_ERROR_SHOULD_REAUTHENTICATE
import com.samadtch.bilinguai.utilities.exceptions.AuthException.Companion.AUTH_ERROR_USER_LOGGED_OUT
import com.samadtch.bilinguai.utilities.exceptions.AuthException.Companion.AUTH_ERROR_USER_NOT_FOUND
import com.samadtch.bilinguai.utilities.exceptions.AuthException.Companion.AUTH_ERROR_USER_WRONG_CREDENTIALS
import com.samadtch.bilinguai.utilities.exceptions.AuthException.Companion.AUTH_ERROR_WEAK_PASSWORD
import com.samadtch.bilinguai.utilities.exceptions.AuthException.Companion.AUTH_ERROR_WRONG_EMAIL
import com.samadtch.bilinguai.utilities.exceptions.AuthException.Companion.AUTH_ERROR_WRONG_PASSWORD
import com.samadtch.bilinguai.utilities.exceptions.DataException
import com.samadtch.bilinguai.utilities.exceptions.DataException.Companion.DATA_ERROR_SERVICE
import kotlinx.coroutines.tasks.await
import org.koin.dsl.module

class AuthRemoteSourceAndroid(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) : AuthRemoteSource {

    override suspend fun register(email: String, password: String) = try {
        val user = auth.createUserWithEmailAndPassword(email, password)
            .await().user!!
        user.sendEmailVerification().await()
        db.collection("users").document(user.uid).set(mapOf<String, String>()).await()
        //Return Id
        Result.success(user.uid)
    } catch (e: FirebaseNetworkException) {
        Result.failure(AuthException(AUTH_ERROR_NETWORK))
    } catch (e: FirebaseAuthWeakPasswordException) {
        Result.failure(AuthException(AUTH_ERROR_WEAK_PASSWORD))
    } catch (e: FirebaseAuthInvalidCredentialsException) {
        Result.failure(AuthException(AUTH_ERROR_INVALID_EMAIL))
    } catch (e: FirebaseAuthUserCollisionException) {
        Result.failure(AuthException(AUTH_ERROR_EMAIL_ALREADY_IN_USE))
    } catch (e: FirebaseFirestoreException) {
        Result.failure(DataException(DATA_ERROR_SERVICE))
    }

    override suspend fun login(email: String, password: String) = try {
        Result.success(auth.signInWithEmailAndPassword(email, password).await().user!!.uid)
    } catch (e: FirebaseNetworkException) {
        Result.failure(AuthException(AUTH_ERROR_NETWORK))
    } catch (e: FirebaseAuthInvalidCredentialsException) {
        Result.failure(AuthException(AUTH_ERROR_WRONG_PASSWORD))
    } catch (e: FirebaseAuthInvalidUserException) {
        Result.failure(AuthException(AUTH_ERROR_WRONG_EMAIL))
    }

    override suspend fun checkEmailVerified(): Result<Boolean> {
        val user = auth.currentUser ?: return Result
            .failure(AuthException(AUTH_ERROR_USER_LOGGED_OUT))
        return Result.success(user.isEmailVerified)
    }

    override suspend fun verifyEmail() {
        val user = auth.currentUser ?: throw AuthException(AUTH_ERROR_USER_LOGGED_OUT)
        user.sendEmailVerification().await()
    }

    override suspend fun getEmail(): Result<String> {
        val user = auth.currentUser
        return if (user == null) Result.failure(AuthException(AUTH_ERROR_USER_LOGGED_OUT))
        else Result.success(user.email!!)
    }

    override suspend fun sendPasswordResetEmail(email: String) {
        try {
            auth.sendPasswordResetEmail(email).await()
        } catch (e: FirebaseNetworkException) {
            throw AuthException(AUTH_ERROR_NETWORK)
        } catch (e: FirebaseAuthInvalidUserException) {
            throw AuthException(AUTH_ERROR_USER_NOT_FOUND)
        }
    }

    @Suppress("DEPRECATION")
    override suspend fun updateEmail(email: String) {
        val user = auth.currentUser ?: throw AuthException(AUTH_ERROR_USER_LOGGED_OUT)
        try {
            user.updateEmail(email).await()
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            throw AuthException(AUTH_ERROR_INVALID_EMAIL)
        } catch (e: FirebaseAuthUserCollisionException) {
            throw AuthException(AUTH_ERROR_EMAIL_ALREADY_IN_USE)
        } catch (e: FirebaseAuthInvalidUserException) {
            throw AuthException(AUTH_ERROR_USER_NOT_FOUND)
        } catch (e: FirebaseAuthRecentLoginRequiredException) {
            throw AuthException(AUTH_ERROR_SHOULD_REAUTHENTICATE)
        }
    }

    override suspend fun updatePassword(newPassword: String) {
        val user = auth.currentUser ?: throw AuthException(AUTH_ERROR_USER_LOGGED_OUT)
        try {
            user.updatePassword(newPassword).await()
        } catch (e: FirebaseAuthWeakPasswordException) {
            throw AuthException(AUTH_ERROR_WEAK_PASSWORD)
        } catch (e: FirebaseAuthInvalidUserException) {
            throw AuthException(AUTH_ERROR_USER_NOT_FOUND)
        } catch (e: FirebaseAuthRecentLoginRequiredException) {
            throw AuthException(AUTH_ERROR_SHOULD_REAUTHENTICATE)
        }
    }

    override suspend fun getUserId(): Result<String> {
        val user = auth.currentUser
        return if (user != null) Result.success(user.uid)
        else Result.failure(AuthException(AUTH_ERROR_USER_LOGGED_OUT))
    }

    override suspend fun logout() = auth.signOut()

    override suspend fun deleteAccount() {
        val user = auth.currentUser ?: throw AuthException(AUTH_ERROR_USER_LOGGED_OUT)
        try {
            user.delete().await()
            db.collection("users").document(user.uid).delete().await()
        } catch (e: FirebaseNetworkException) {
            throw AuthException(AUTH_ERROR_NETWORK)
        } catch (e: FirebaseAuthInvalidUserException) {
            throw AuthException(AUTH_ERROR_USER_NOT_FOUND)
        } catch (e: FirebaseFirestoreException) {
            throw DataException(DATA_ERROR_SERVICE)
        }
    }

    override suspend fun reauthenticate(password: String, callback: suspend () -> Unit) {
        val user = auth.currentUser ?: throw AuthException(AUTH_ERROR_USER_LOGGED_OUT)
        try {
            user.reauthenticate(EmailAuthProvider.getCredential(user.email!!, password)).await()
            callback()//Run callback after authentication is performed properly
        } catch (e: FirebaseNetworkException) {
            throw AuthException(AUTH_ERROR_NETWORK)
        } catch (e: FirebaseAuthInvalidUserException) {
            throw AuthException(AUTH_ERROR_USER_NOT_FOUND)
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            throw AuthException(AUTH_ERROR_USER_WRONG_CREDENTIALS)
        }
    }

    override fun isLoggedIn(): Boolean {
        return auth.currentUser != null
    }
}

actual fun getAuthRemoteSource() = module {
    single<AuthRemoteSource> { AuthRemoteSourceAndroid(get(), get()) }
}