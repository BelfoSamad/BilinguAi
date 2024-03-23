package com.samadtch.bilinguai.data.datasources.remote.base

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.samadtch.bilinguai.data.repositories.base.GenerationState
import com.samadtch.bilinguai.utilities.exceptions.AuthException
import com.samadtch.bilinguai.utilities.exceptions.DataException
import kotlinx.coroutines.tasks.await
import org.koin.dsl.module

class ConfigRemoteSourceAndroid(
    private val db: FirebaseFirestore,
    private val config: FirebaseRemoteConfig
) : ConfigRemoteSource {

    /***********************************************************************************************
     * ************************* Methods
     */
    override suspend fun getGenerationState(userId: String?): Result<GenerationState> {
        return if (userId == null) Result.failure(AuthException(AuthException.AUTH_ERROR_USER_LOGGED_OUT))
        else try {
            Result.success(
                db.collection("users")
                    .document(userId)
                    .get().await()
                    .toObject(GenerationState::class.java)!!
            )
        } catch (e: FirebaseFirestoreException) {
            Result.failure(DataException(DataException.DATA_ERROR_SERVICE))
        }
    }

    override suspend fun setGenerationState(userId: String?, cooldown: Long, remaining: Int) {
        if (userId == null) throw AuthException(AuthException.AUTH_ERROR_USER_LOGGED_OUT)
        else db.collection("users").document(userId).set(
            mapOf(
                "cooldown" to cooldown,
                "remaining" to remaining
            )
        )
    }

    override suspend fun getRemaining(userId: String?): Int {
        return if (userId == null) throw AuthException(AuthException.AUTH_ERROR_USER_LOGGED_OUT)
        else (db.collection("users")
            .document(userId)
            .get().await()
            .get("remaining") as Long).toInt()
    }

    override fun getStringConfig(key: String) = config.getString(key)

    override fun getLongConfig(key: String) = config.getLong(key)

    override fun getDoubleConfig(key: String) = config.getDouble(key)

    override fun getBooleanConfig(key: String) = config.getBoolean(key)

}

actual fun getConfigRemoteSource() = module {
    single<ConfigRemoteSource> { ConfigRemoteSourceAndroid(get(), get()) }
}