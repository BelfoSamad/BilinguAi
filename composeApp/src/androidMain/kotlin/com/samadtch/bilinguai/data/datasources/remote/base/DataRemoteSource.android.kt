package com.samadtch.bilinguai.data.datasources.remote.base

import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import com.samadtch.bilinguai.models.Data
import org.koin.dsl.module
import com.samadtch.bilinguai.utilities.exceptions.DataException

class DataRemoteSourceAndroid(private val db: FirebaseFirestore) : DataRemoteSource {

    override suspend fun insertData(userId: String, data: Data): String? {
        return try {
            db.collection("users").document(userId).collection("data")
                .add(data.javaClass.declaredFields.filter { field ->
                    field.isAccessible = true
                    field.get(data) != null
                }.associate { field -> field.name to field.get(data) }).await().id
        } catch (e: FirebaseFirestoreException) {
            DataException.handleError(e.code.ordinal)
            null
        }
    }

    override suspend fun getData(userId: String): List<Data> {
        return try {
            db.collection("users").document(userId).collection("data").get()
                .await().documents.map {
                    val data = it.toObject(Data::class.java)!!
                    data.copy(dataId = it.id)
                }
        } catch (e: FirebaseFirestoreException) {
            if (e.code.ordinal in DataException.FIRESTORE_SERVICE_ERRORS)
                Firebase.crashlytics.recordException(Exception("fetchData: Error ${e.message}"))
            //Handle Exception
            DataException.handleError(e.code.ordinal)
            listOf() // Return empty list
        }
    }

    override suspend fun deleteData(userId: String, dataId: String) {
        try {
            db.collection("users").document(userId).collection("data").document(dataId).delete()
                .await()
        } catch (e: FirebaseFirestoreException) {
            DataException.handleError(e.code.ordinal)
        }
    }

}

actual fun getDataRemoteSource() = module {
    single<DataRemoteSource> { DataRemoteSourceAndroid(get()) }
}