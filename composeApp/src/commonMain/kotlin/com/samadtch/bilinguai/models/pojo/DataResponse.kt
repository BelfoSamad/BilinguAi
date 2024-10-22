package com.samadtch.bilinguai.models.pojo

import kotlinx.serialization.Serializable

@Serializable
data class DataResponse(
    val conversation: List<String>,
    val translation: List<String>? = null,
    val vocabulary: Map<String, String>
)
