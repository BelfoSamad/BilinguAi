package com.samadtch.bilinguai.data.datasources.remote.base

import com.samadtch.bilinguai.models.Data
import org.koin.core.module.Module

interface DataRemoteSource {

    suspend fun insertData(userId: String, data: Data): String?

    suspend fun getData(userId: String): List<Data>

    suspend fun deleteData(userId: String, dataId: String)

}

expect fun getDataRemoteSource(): Module