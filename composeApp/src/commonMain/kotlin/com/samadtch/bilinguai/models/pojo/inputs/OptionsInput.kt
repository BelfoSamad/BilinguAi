package com.samadtch.bilinguai.models.pojo.inputs

data class OptionsInput(
    override var key: String,
    override var label: String,
    val hint: String,
    val options: List<String>,
    val multiSelection: Boolean,
    val minSelection: Int?,
    val maxSelection: Int?,
) : BaseInput
