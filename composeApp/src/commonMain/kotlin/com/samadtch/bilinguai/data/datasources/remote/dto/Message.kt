package com.samadtch.bilinguai.data.datasources.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val content: String,
    val role: String
)