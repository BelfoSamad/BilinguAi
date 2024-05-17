package com.samadtch.bilinguai.models.pojo.inputs

data class OptionsInput(
    val key: String,
    val label: String,
    val hint: String,
    val options: List<String>,
    val multiSelection: Boolean,
    val minSelection: Int?,
    val maxSelection: Int?,
)
