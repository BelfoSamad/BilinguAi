package com.samadtch.bilinguai.data.datasources.remote.dto

import kotlinx.serialization.Serializable

/**
 * ********************************** ChatGpt
 */
@Serializable
data class Message(
    val content: String,
    val role: String
)

/**
 * ********************************** Gemini Pro
 */
@Serializable
data class Content(
    val parts: List<Part>,
    val role: String? = null,
)

@Serializable
data class Part(val text: String)