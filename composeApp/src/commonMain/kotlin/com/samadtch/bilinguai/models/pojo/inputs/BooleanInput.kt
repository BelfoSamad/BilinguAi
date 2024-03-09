package com.samadtch.bilinguai.models.pojo.inputs

//{{key0::TRUE("")FALSE("")}} on Prompt
data class BooleanInput(
    override var key: String,
    override var label: String,
    val defaultValue: Boolean
): BaseInput
