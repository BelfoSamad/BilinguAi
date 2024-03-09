package com.samadtch.bilinguai.models.pojo.inputs

data class TextInput(
    override var key: String,
    override var label: String,
    val lines: Int?,
    val hint: String,
    val defaultValue: String?,
): BaseInput

