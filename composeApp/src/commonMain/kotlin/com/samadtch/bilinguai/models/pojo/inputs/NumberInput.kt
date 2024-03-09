package com.samadtch.bilinguai.models.pojo.inputs

data class NumberInput(
    override var key: String,
    override var label: String,
    var hint: String,
    var defaultValue: String?,
    var isDecimal: Boolean,
    var isNegative: Boolean,
    var maxValue: Double?,
    var minValue: Double?
): BaseInput

