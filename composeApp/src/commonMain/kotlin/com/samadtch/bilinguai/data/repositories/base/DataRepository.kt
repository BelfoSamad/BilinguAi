package com.samadtch.bilinguai.data.repositories.base

import com.samadtch.bilinguai.models.Data

interface DataRepository {

    suspend fun getDictionary(): Map<String, String>?

    suspend fun saveDictionary(word: String, definition: String, saved: Boolean)

    suspend fun generateData(inputs: Map<String, Any>, temperature: Float): Result<Data>

    suspend fun getData(): Result<List<Data>>

    suspend fun deleteData(dataId: String)

}