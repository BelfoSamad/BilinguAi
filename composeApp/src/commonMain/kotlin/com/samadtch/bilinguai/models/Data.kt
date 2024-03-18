package com.samadtch.bilinguai.models

import androidx.compose.runtime.Immutable

@Immutable
data class Data(
    val dataId: String? = null,
    val language: String = "",
    val topic: String = "",
    val conversation: List<String> = listOf(),
    val translation: List<String>? = null,
    val vocabulary: Map<String, String> = mapOf(),
    val createdAt: Long = 1L
)
