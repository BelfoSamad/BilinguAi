package com.samadtch.bilinguai.data.datasources.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * ********************************** ChatGpt
 */
@Serializable
data class ChatGptRequest(
    val model: String,
    @SerialName("response_format")
    val responseFormat: ResponseFormat,
    val messages: List<Message>,
    val temperature: Float
)

@Serializable
data class ResponseFormat(val type: String)

/**
 * ********************************** Gemini Pro
 */
@Serializable
data class GeminiRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig
)

@Serializable
data class GenerationConfig(
    val temperature: Float
)