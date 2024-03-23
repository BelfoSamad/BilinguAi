package com.samadtch.bilinguai.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.samadtch.bilinguai.Resources.strings
import com.samadtch.bilinguai.models.pojo.inputs.BooleanInput
import com.samadtch.bilinguai.models.pojo.inputs.NumberInput
import com.samadtch.bilinguai.models.pojo.inputs.OptionsInput
import com.samadtch.bilinguai.models.pojo.inputs.TextInput
import com.samadtch.bilinguai.ui.theme.PrimaryTextFieldColors
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.flow.MutableSharedFlow

@Composable
fun TextInputView(
    enable: Boolean,
    input: TextInput,
    onDataRequested: Boolean,
    valueFlow: MutableSharedFlow<Pair<String, Any>?>,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    //------------------------------- Declarations
    var value by rememberSaveable { mutableStateOf(input.defaultValue ?: "") }
    var error by rememberSaveable { mutableStateOf<StringResource?>(null) }

    //------------------------------- Effect
    LaunchedEffect(onDataRequested) {
        if (onDataRequested) {
            if (value.isBlank()) {
                error = strings.error_required
                valueFlow.emit(null)
            } else {
                error = null
                valueFlow.emit(Pair(input.key, value))
                value = ""
            }
        }
    }

    //------------------------------- UI
    Column(modifier.padding(16.dp, 4.dp)) {
        Text(
            text = input.label,
            style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.primary)
        )
        TextField(
            modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(top = 4.dp),
            colors = PrimaryTextFieldColors(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            enabled = enable,
            textStyle = MaterialTheme.typography.labelSmall,
            placeholder = {
                Text(
                    text = input.hint,
                    style = MaterialTheme.typography.labelSmall
                )
            },
            isError = error != null,
            minLines = input.lines ?: 1,
            maxLines = input.lines ?: 1,
            supportingText = {
                if (error != null) Text(
                    stringResource(error!!, input.label),
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            value = value,
            onValueChange = { value = it }
        )
    }
}

@Composable
fun NumberInputView(
    enable: Boolean,
    input: NumberInput,
    onDataRequested: Boolean,
    valueFlow: MutableSharedFlow<Pair<String, Any>?>,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    //------------------------------- Declarations
    var value by rememberSaveable { mutableStateOf(input.defaultValue ?: "") }
    var error by rememberSaveable { mutableStateOf<StringResource?>(null) }
    //Data
    val onlyDigitsRegex = """^(?:${'$'}|[1-9]\d*${'$'})"""
    val negativeRegex = """^${'$'}|^(-?[1-9]\d*|0)${'$'}"""
    val decimalRegex = """^${'$'}|^(?!-)(?:\d+|\d*\.\d+)${'$'}"""
    val allRegex = """^(-?\d*\.?\d+|)${'$'}"""

    //------------------------------- Effect
    LaunchedEffect(onDataRequested) {
        if (onDataRequested) {
            if (value.isBlank()) {
                error = strings.error_required
                valueFlow.emit(null)
            } else if (input.minValue != null && value.toDouble() < input.minValue!!) {
                error = strings.error_min
                valueFlow.emit(null)
            } else if (input.maxValue != null && value.toDouble() > input.maxValue!!) {
                error = strings.error_max
                valueFlow.emit(null)
            } else {
                error = null
                valueFlow.emit(Pair(input.key, value))
                value = ""
            }
        }
    }

    //------------------------------- UI
    Column(modifier.padding(16.dp, 8.dp)) {
        Text(
            text = input.label,
            style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.primary)
        )
        TextField(
            modifier = Modifier.padding(top = 4.dp).fillMaxWidth(),
            colors = PrimaryTextFieldColors(),
            enabled = enable,
            textStyle = MaterialTheme.typography.labelSmall,
            placeholder = {
                Text(
                    text = input.hint,
                    style = MaterialTheme.typography.labelSmall
                )
            },
            isError = error != null,
            supportingText = {
                if (error != null) Text(
                    text = when (error!!) {
                        strings.error_required -> stringResource(
                            strings.error_required,
                            listOf(input.label)
                        )

                        strings.error_min -> stringResource(
                            strings.error_min,
                            input.label,
                            input.minValue!!
                        )

                        else -> stringResource(
                            strings.error_max,
                            listOf(input.label, input.maxValue!!)
                        )
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            value = value,
            onValueChange = {
                //Check Decimal/Negative
                val regex: String = if (input.isDecimal && input.isNegative) allRegex
                else if (input.isDecimal) decimalRegex
                else if (input.isNegative) negativeRegex
                else onlyDigitsRegex
                //Validate
                if (Regex(regex).matches(it + "1")) value = it
            }
        )
    }
}

@Composable
fun CheckboxInputView(
    enable: Boolean,
    input: BooleanInput,
    onDataRequested: Boolean,
    valueFlow: MutableSharedFlow<Pair<String, Any>?>,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    //------------------------------- Declarations
    var value by remember { mutableStateOf(input.defaultValue) }

    //------------------------------- Effect
    LaunchedEffect(onDataRequested) {
        if (onDataRequested) {
            valueFlow.emit(Pair(input.key, value))
            value = input.defaultValue
        }
    }

    //------------------------------- UI
    Row(modifier.padding(16.dp, 0.dp)) {
        Checkbox(
            enabled = enable,
            checked = value,
            colors = CheckboxDefaults.colors(
                uncheckedColor = MaterialTheme.colorScheme.primary,
                checkedColor = MaterialTheme.colorScheme.primary,
                checkmarkColor = MaterialTheme.colorScheme.tertiary,
            ),
            onCheckedChange = { value = it },
        )
        Text(
            modifier = Modifier.align(Alignment.CenterVertically).weight(1f),
            text = input.label,
            style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.primary)
        )
    }
}

@Composable
fun SelectionInputView(
    enable: Boolean,
    input: OptionsInput,
    onDataRequested: Boolean,
    valueFlow: MutableSharedFlow<Pair<String, Any>?>,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    //------------------------------- Declarations
    val value = remember { mutableStateListOf<String>() }
    var expanded by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<StringResource?>(null) }

    //------------------------------- Effect
    LaunchedEffect(onDataRequested) {
        if (onDataRequested) {
            if (value.isEmpty()) {
                error = strings.error_required
                valueFlow.emit(null)
            } else if (input.multiSelection && input.minSelection != null && value.size < input.minSelection) {
                error = strings.error_min_selection
                valueFlow.emit(null)
            } else if (input.multiSelection && input.maxSelection != null && value.size > input.maxSelection) {
                error = strings.error_max_selection
                valueFlow.emit(null)
            } else {
                error = null
                valueFlow.emit(Pair(input.key, value.toList()))
                value.clear()
            }
        }
    }

    //------------------------------- UI
    Column(modifier.padding(16.dp, 8.dp)) {
        Text(
            text = input.label,
            style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.primary)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
                .clickable(onClick = { if (enable) expanded = true })
                .border(BorderStroke(1.dp, MaterialTheme.colorScheme.primary), CircleShape)
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier.padding(start = 8.dp).align(Alignment.CenterVertically),
                text = input.hint + if (value.isNotEmpty()) ": ${value.size}" else "",
                style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.primary)
            )
            Icon(
                modifier = Modifier.align(Alignment.CenterVertically),
                imageVector = Icons.Default.ExpandMore,
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = "Dropdown"
            )
        }
        DropdownMenu(
            modifier = Modifier.background(color = MaterialTheme.colorScheme.tertiary),
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            input.options.forEach { s ->
                DropdownMenuItem(
                    text = {
                        Row(Modifier.fillMaxWidth()) {
                            Checkbox(
                                checked = value.contains(s),
                                colors = CheckboxDefaults.colors(
                                    uncheckedColor = MaterialTheme.colorScheme.primary,
                                    checkedColor = MaterialTheme.colorScheme.primary,
                                    checkmarkColor = MaterialTheme.colorScheme.tertiary,
                                ),
                                onCheckedChange = {
                                    if (value.contains(s)) value.remove(s)
                                    else {
                                        if (!input.multiSelection && value.isNotEmpty()) value.clear()
                                        value.add(s)
                                    }
                                },
                            )
                            Text(
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .weight(1f)
                                    .padding(start = 4.dp, end = 16.dp),
                                style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.primary),
                                text = s
                            )
                        }
                    },
                    onClick = {
                        if (value.contains(s)) value.remove(s)
                        else {
                            if (!input.multiSelection && value.isNotEmpty()) value.clear()
                            value.add(s)
                        }
                    }
                )
            }
        }
        if (error != null) Text(
            modifier = Modifier.padding(16.dp, 2.dp),
            text = when (error!!) {
                strings.error_required -> stringResource(
                    strings.error_required,
                    input.label
                )

                strings.error_min_selection -> stringResource(
                    strings.error_min_selection,
                    input.minSelection!!
                )

                else -> stringResource(
                    strings.error_max_selection,
                    input.maxSelection!!
                )
            },
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.primary)
        )
    }
}