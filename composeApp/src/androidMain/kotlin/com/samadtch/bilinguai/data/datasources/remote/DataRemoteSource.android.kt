package com.samadtch.bilinguai.data.datasources.remote

import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import com.samadtch.bilinguai.models.Data
import org.koin.dsl.module
import com.samadtch.bilinguai.utilities.exceptions.DataException
import kotlinx.datetime.Clock

class DataRemoteSourceAndroid(private val db: FirebaseFirestore) : DataRemoteSource {

    override suspend fun saveDictionary(
        userId: String?,
        word: String,
        definition: String,
        saved: Boolean
    ) {
        try {
            if (userId != null) {
                val userRef = db.collection("users").document(userId)
                db.runTransaction {
                    //Update Dictionary
                    val dictionary = (it.get(userRef).data!!["dictionary"] as Map<*, *>?)
                        ?.toMutableMap() ?: mutableMapOf()
                    if (saved) dictionary[word] = definition else dictionary.remove(word)

                    //Push Dictionary Updates
                    it.update(userRef, mapOf("dictionary" to dictionary))
                }
            }
        } catch (_: Exception) {
            //DO NOTHING
        }
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun getDictionary(userId: String?): Map<String, String>? {
        return try {
            if (userId == null) null
            else db.collection("users")
                .document(userId)
                .get()
                .await()
                .data!!["dictionary"] as Map<String, String>?
        } catch (e: Exception) {
            //DO NOTHING
            null
        }
    }

    override suspend fun insertData(userId: String, data: Data) = try {
        db.collection("users").document(userId).collection("data")
            .add(data.javaClass.declaredFields.filter { field ->
                field.isAccessible = true
                field.get(data) != null
            }.associate { field -> field.name to field.get(data) }).await().id
    } catch (e: FirebaseFirestoreException) {
        if (e.code.ordinal in DataException.FIRESTORE_SERVICE_ERRORS)
            Firebase.crashlytics.recordException(Exception("insertData: Error ${e.message}"))
        DataException.handleError(e.code.ordinal)
        null
    }

    override suspend fun getData(userId: String) = try {
        db.collection("users").document(userId).collection("data").get()
            .await().documents.map {
                val data = it.toObject(Data::class.java)!!
                data.copy(dataId = it.id)
            }
    } catch (e: FirebaseFirestoreException) {
        if (e.code.ordinal in DataException.FIRESTORE_SERVICE_ERRORS)
            Firebase.crashlytics.recordException(Exception("getData: Error ${e.message}"))
        //Handle Exception
        DataException.handleError(e.code.ordinal)
        listOf() // Return empty list
    }

    override suspend fun deleteData(userId: String, dataId: String) {
        try {
            db.collection("users").document(userId).collection("data").document(dataId).delete()
                .await()
        } catch (e: FirebaseFirestoreException) {
            if (e.code.ordinal in DataException.FIRESTORE_SERVICE_ERRORS)
                Firebase.crashlytics.recordException(Exception("deleteData: Error ${e.message}"))
            DataException.handleError(e.code.ordinal)
        }
    }

    override suspend fun reportData(userId: String, dataId: String) {
        try {
            val dataRef = db.collection("users")
                .document(userId)
                .collection("data")
                .document(dataId)
            val reportRef = db.collection("reports").document()

            db.runTransaction {
                it.update(dataRef, mapOf("reported" to true))
                it.set(
                    reportRef, mapOf(
                        "userId" to userId,
                        "dataId" to dataId,
                        "timestamp" to Clock.System.now().epochSeconds
                    )
                )
            }
        } catch (e: FirebaseFirestoreException) {
            if (e.code.ordinal in DataException.FIRESTORE_SERVICE_ERRORS)
                Firebase.crashlytics.recordException(Exception("reportData: Error ${e.message}"))
            DataException.handleError(e.code.ordinal)
        }
    }

}

actual fun getDataRemoteSource() = module {
    single<DataRemoteSource> { DataRemoteSourceAndroid(get()) }
}