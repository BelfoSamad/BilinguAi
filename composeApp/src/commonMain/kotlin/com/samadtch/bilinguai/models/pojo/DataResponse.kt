package com.samadtch.bilinguai.models.pojo

import kotlinx.serialization.Serializable

@Serializable
data class DataResponse(
    val conversation: List<String>,
    val vocabulary: Map<String, String>
)
