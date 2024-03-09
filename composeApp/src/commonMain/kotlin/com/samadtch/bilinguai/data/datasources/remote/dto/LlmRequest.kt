package com.samadtch.bilinguai.data.datasources.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LlmRequest(
    val model: String,
    @SerialName("response_format")
    val responseFormat: ResponseFormat,
    val messages: List<Message>,
    val temperature: Float
)

@Serializable
data class ResponseFormat(val type: String)