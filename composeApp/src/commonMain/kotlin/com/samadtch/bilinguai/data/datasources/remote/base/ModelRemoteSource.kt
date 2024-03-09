package com.samadtch.bilinguai.data.datasources.remote.base

import com.samadtch.bilinguai.models.pojo.DataResponse

interface ModelRemoteSource {

    suspend fun generateData(settings: Map<String, Any>, prompt: String): Result<DataResponse>

}