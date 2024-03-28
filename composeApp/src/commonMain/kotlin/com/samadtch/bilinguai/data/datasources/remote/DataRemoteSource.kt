package com.samadtch.bilinguai.data.datasources.remote

import com.samadtch.bilinguai.models.Data
import org.koin.core.module.Module

interface DataRemoteSource {

    suspend fun saveDictionary(userId: String?, word: String, definition: String, saved: Boolean)

    suspend fun getDictionary(userId: String?): Map<String, String>?

    suspend fun insertData(userId: String, data: Data): String?

    suspend fun getData(userId: String): List<Data>

    suspend fun deleteData(userId: String, dataId: String)

}

expect fun getDataRemoteSource(): Module