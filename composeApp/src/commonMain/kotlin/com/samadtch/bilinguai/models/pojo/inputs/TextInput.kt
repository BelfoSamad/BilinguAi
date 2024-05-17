package com.samadtch.bilinguai.models.pojo.inputs

data class TextInput(
    val key: String,
    val label: String,
    val lines: Int?,
    val hint: String,
    val defaultValue: String?,
)

