package com.samadtch.bilinguai.data.datasources.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * ********************************** ChatGpt
 */
@Serializable
data class ChatGptResponse(
    val choices: List<Choice>,
    val created: Int,
    val id: String,
    val model: String,
    val `object`: String,
    val usage: Usage
)

@Serializable
data class Usage(
    @SerialName("completion_tokens")
    val completionTokens: Int,
    @SerialName("prompt_tokens")
    val promptTokens: Int,
    @SerialName("total_tokens")
    val totalTokens: Int
)

@Serializable
data class Choice(
    @SerialName("finish_reason")
    val finishReason: String,
    val index: Int,
    val logprobs: Boolean?,
    val message: Message
)

/**
 * ********************************** Gemini Pro
 */
@Serializable
data class GeminiResponse(
    val candidates: List<Candidate>
)

@Serializable
data class Candidate(
    val index: Int,
    val content: Content
)