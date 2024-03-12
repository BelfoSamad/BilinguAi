package com.samadtch.bilinguai.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samadtch.bilinguai.Resources.strings
import com.samadtch.bilinguai.models.Data
import com.samadtch.bilinguai.models.pojo.inputs.BaseInput
import com.samadtch.bilinguai.models.pojo.inputs.BooleanInput
import com.samadtch.bilinguai.models.pojo.inputs.NumberInput
import com.samadtch.bilinguai.models.pojo.inputs.OptionsInput
import com.samadtch.bilinguai.models.pojo.inputs.TextInput
import com.samadtch.bilinguai.ui.common.CheckboxInputView
import com.samadtch.bilinguai.ui.common.DefinitionDialog
import com.samadtch.bilinguai.ui.common.DeleteDataDialog
import com.samadtch.bilinguai.ui.common.NumberInputView
import com.samadtch.bilinguai.ui.common.SelectionInputView
import com.samadtch.bilinguai.ui.common.TextInputView
import com.samadtch.bilinguai.ui.common.shimmerModifier
import com.samadtch.bilinguai.ui.theme.OutlinedButtonColors
import com.samadtch.bilinguai.ui.theme.PrimaryFilledButtonColors
import com.samadtch.bilinguai.ui.theme.PrimaryIconButtonColors
import com.samadtch.bilinguai.ui.theme.SecondaryIconButtonColors
import com.samadtch.bilinguai.utilities.exceptions.APIException
import com.samadtch.bilinguai.utilities.exceptions.AuthException.Companion.AUTH_ERROR_USER_LOGGED_OUT
import com.samadtch.bilinguai.utilities.exceptions.DataException
import dev.icerock.moko.resources.StringResource
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.DurationUnit
import kotlin.time.toDuration

/***********************************************************************************************
 * ************************* UI States
 */
data class DataUiState(
    val isLoading: Boolean = true,
    val errorCode: String? = null,
    val data: List<Data>? = null,
    val email: String? = null
)

data class GenerationUiState(
    val isLoading: Boolean = true,
    val errorCode: String? = null,
    val success: Boolean = false
)

/***********************************************************************************************
 * ************************* UI
 */
@Composable
fun HomeScreen(
    stringRes: (id: StringResource, args: List<Any>?) -> String,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    inputs: List<List<BaseInput>>,
    openDrawer: () -> Unit,
    //Drawer Menu
    logout: () -> Unit,
    //States
    uiState: DataUiState,
    generationState: GenerationUiState?,
    deleteState: Int?,
    //Listeners
    onSortChanged: (sort: Boolean) -> Unit,
    onGenerateClicked: (data: Map<String, Any>, temperature: Float) -> Unit,
    onDataDeleted: (dataId: String) -> Unit
) {
    //------------------------------- Declarations
    var sort by rememberSaveable { mutableStateOf(true) }
    var word by rememberSaveable { mutableStateOf<String?>(null) }
    var definition by rememberSaveable { mutableStateOf<String?>(null) }
    var showDeleteDataDialog by rememberSaveable { mutableStateOf<String?>(null) }

    //------------------------------- Effects
    //Data State
    LaunchedEffect(uiState.errorCode) {
        if (uiState.errorCode != null) {
            when (uiState.errorCode.split("::")[0]) {
                "AUTH" -> logout()
                "DATA" -> {
                    when (uiState.errorCode.split("::")[1].toInt()) {
                        DataException.DATA_ERROR_NETWORK -> onShowSnackbar(
                            stringRes(strings.error_network, null),
                            null
                        )

                        DataException.DATA_ERROR_SERVICE -> onShowSnackbar(
                            stringRes(strings.error_server, null),
                            null
                        )
                    }
                }
            }
        }
    }

    //Generation Error
    LaunchedEffect(generationState) {
        if (generationState?.errorCode != null)
            when (generationState.errorCode.split("::")[0]) {
                "AUTH" -> logout()
                "API" -> {
                    when (generationState.errorCode.split("::")[1].toInt()) {
                        APIException.API_ERROR_NETWORK -> onShowSnackbar(
                            stringRes(strings.error_network, null),
                            null
                        )

                        APIException.API_ERROR_AUTH -> onShowSnackbar(
                            stringRes(strings.error_api_key, null),
                            null
                        )

                        APIException.API_ERROR_RATE_LIMIT -> onShowSnackbar(
                            stringRes(strings.error_rate_limit, null),
                            null
                        )

                        APIException.API_ERROR_OTHER -> onShowSnackbar(
                            stringRes(strings.error_api, null),
                            null
                        )
                    }
                }

                "DATA" -> {
                    when (generationState.errorCode.split("::")[1].toInt()) {
                        DataException.DATA_ERROR_NETWORK -> onShowSnackbar(
                            stringRes(strings.error_network, null),
                            null
                        )

                        else -> onShowSnackbar(
                            stringRes(strings.error_server, null),
                            null
                        )
                    }
                }

                "COOLDOWN" -> {
                    //Show Snackbar
                    var minutes = generationState.errorCode.split("::")[1]
                        .toLong().toDuration(DurationUnit.SECONDS).inWholeMinutes
                    if (minutes > 60) {
                        val hours = generationState.errorCode.split("::")[1]
                            .toLong().toDuration(DurationUnit.SECONDS).inWholeHours
                        minutes -= hours * 60
                        onShowSnackbar(
                            stringRes(strings.cooldown_hours, listOf(hours, minutes)),
                            null
                        )
                    } else onShowSnackbar(
                        stringRes(strings.cooldown_minutes, listOf(minutes)),
                        null
                    )
                }
            }
    }

    //Deleted Error
    LaunchedEffect(deleteState) {
        when (deleteState) {
            AUTH_ERROR_USER_LOGGED_OUT -> {
                logout()
                showDeleteDataDialog = null
            }

            DataException.DATA_ERROR_NETWORK -> {
                onShowSnackbar(
                    stringRes(strings.error_network, null),
                    null
                )
                showDeleteDataDialog = null
            }

            DataException.DATA_ERROR_SERVICE -> {
                onShowSnackbar(
                    stringRes(strings.error_server, null),
                    null
                )
                showDeleteDataDialog = null
            }

            DataException.DATA_ERROR_NOT_FOUND -> {
                onShowSnackbar(
                    stringRes(strings.error_server_not_found, null),
                    null
                )
                showDeleteDataDialog = null
            }

            null -> showDeleteDataDialog = null
        }
    }

    //------------------------------- Dialogs
    //Word Dialog
    if (word != null) {
        DefinitionDialog(word = word!!, definition = definition!!, onDismiss = {
            word = null
            definition = null
        })
    }

    if (showDeleteDataDialog != null) DeleteDataDialog(
        stringRes,
        id = showDeleteDataDialog!!,
        deleteData = onDataDeleted,
        deleteDataState = deleteState,
        onDismiss = { if (deleteState != -99) showDeleteDataDialog = null }
    )

    //------------------------------- UI
    Column {
        LazyColumn {

            item {
                //Top Section
                Column(Modifier.background(color = MaterialTheme.colorScheme.tertiary)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            modifier = Modifier.padding(top = 28.dp, bottom = 18.dp),
                            text = stringRes(strings.appName, null),
                            style = MaterialTheme.typography.headlineLarge.copy(
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        FilledTonalIconButton(
                            modifier = Modifier.align(Alignment.CenterVertically),
                            onClick = { openDrawer() },
                            colors = PrimaryIconButtonColors()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = null
                            )
                        }
                    }

                    //Dynamic Form
                    DynamicForm(
                        stringRes = stringRes,
                        inputs = inputs,
                        generationState = generationState,
                        onGenerateClicked = onGenerateClicked
                    )
                }
            }

            item {
                //Top Section
                Row(
                    modifier = Modifier.padding(8.dp, 8.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        text = stringRes(strings.generated_data, null),
                        style = MaterialTheme.typography.titleLarge
                    )
                    OutlinedButton(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        border = OutlinedButtonColors(),
                        onClick = {
                            sort = !sort
                            onSortChanged(sort)
                        }
                    ) {
                        Icon(
                            modifier = Modifier.padding(0.dp, 0.dp, 8.dp, 0.dp)
                                .align(Alignment.CenterVertically),
                            imageVector = Icons.Filled.Sort,
                            tint = MaterialTheme.colorScheme.secondary,
                            contentDescription = null
                        )
                        Text(
                            text = if (sort) stringRes(strings.date, null) else stringRes(
                                strings.name,
                                null
                            ),
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = MaterialTheme.colorScheme.secondary
                            )
                        )
                    }
                }
            }

            //Data List
            if (uiState.isLoading) {
                item { Box(shimmerModifier); Box(shimmerModifier); Box(shimmerModifier); }
            } else if (uiState.errorCode == null) {
                items(
                    items = uiState.data ?: listOf(),
                    key = { it.dataId!! }
                ) {
                    DataHolder(
                        stringRes,
                        data = it,
                        onDataDeleted = { id -> showDeleteDataDialog = id },
                        onWordClicked = { w, d ->
                            word = w
                            definition = d
                        }
                    )
                }
            }
        }

        //Error Situation
        if (uiState.errorCode != null && uiState.errorCode == "DATA::94") {
            Column(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp).weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    imageVector = Icons.Default.ErrorOutline,
                    tint = MaterialTheme.colorScheme.secondary,
                    contentDescription = null
                )
                Spacer(Modifier.padding(4.dp))
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = stringRes(strings.error_empty, null),
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = MaterialTheme.colorScheme.secondary
                    )
                )
            }

        }
    }

}

@Composable
fun DynamicForm(
    stringRes: (id: StringResource, args: List<Any>?) -> String,
    inputs: List<List<BaseInput>>,
    generationState: GenerationUiState?,
    onGenerateClicked: (data: Map<String, Any>, temperature: Float) -> Unit
) {
    //------------------------------- Declarations
    var sliderPosition by remember { mutableFloatStateOf(0.7f) }
    val dataFlow = remember { inputs.flatten().map { MutableSharedFlow<Pair<String, Any>?>() } }
    var generateClicked by remember { mutableStateOf(false) }
    var disableInputs by remember { mutableStateOf(false) }

    //------------------------------- Effects
    LaunchedEffect(true) {
        var data: MutableMap<String, Any>?
        combine(dataFlow) { combined ->
            if (combined.none { it == null }) {
                data = mutableMapOf()
                combined.forEach { data!![it!!.first] = it.second }
            } else data = null
            data
        }.collect {
            if (it != null) {
                onGenerateClicked(it, sliderPosition)
            }
            generateClicked = false
        }
    }

    LaunchedEffect(generationState) {
        disableInputs = generationState?.isLoading == true
    }

    //------------------------------- UI
    //Form
    var flattenedIndex = 0
    inputs.forEach { row ->
        if (row.size == 1) {
            when (val input = row[0]) {
                is TextInput -> TextInputView(
                    stringRes,
                    !disableInputs,
                    input,
                    generateClicked,
                    dataFlow[flattenedIndex]
                )

                is NumberInput -> NumberInputView(
                    stringRes,
                    !disableInputs,
                    input,
                    generateClicked,
                    dataFlow[flattenedIndex]
                )

                is OptionsInput -> SelectionInputView(
                    stringRes,
                    !disableInputs,
                    input,
                    generateClicked,
                    dataFlow[flattenedIndex]
                )

                is BooleanInput -> CheckboxInputView(
                    !disableInputs,
                    input,
                    generateClicked,
                    dataFlow[flattenedIndex]
                )
            }
            flattenedIndex++
        } else {
            Row(Modifier.fillMaxWidth()) {
                when (val input = row[0]) {
                    is TextInput -> TextInputView(
                        stringRes,
                        !disableInputs,
                        input,
                        generateClicked,
                        dataFlow[flattenedIndex],
                        Modifier.weight(1.25f).align(Alignment.CenterVertically)
                    )

                    is NumberInput -> NumberInputView(
                        stringRes,
                        !disableInputs,
                        input,
                        generateClicked,
                        dataFlow[flattenedIndex],
                        Modifier.weight(1f).align(Alignment.CenterVertically)
                    )

                    is OptionsInput -> SelectionInputView(
                        stringRes,
                        !disableInputs,
                        input,
                        generateClicked,
                        dataFlow[flattenedIndex],
                        Modifier.weight(1f).align(Alignment.CenterVertically)
                    )

                    is BooleanInput -> CheckboxInputView(
                        !disableInputs,
                        input,
                        generateClicked,
                        dataFlow[flattenedIndex],
                        Modifier.weight(1f).align(Alignment.CenterVertically)
                    )
                }
                flattenedIndex++
                Spacer(Modifier.padding(4.dp))
                when (val input = row[1]) {
                    is TextInput -> TextInputView(
                        stringRes,
                        !disableInputs,
                        input,
                        generateClicked,
                        dataFlow[flattenedIndex],
                        Modifier.weight(1f).align(Alignment.CenterVertically)
                    )

                    is NumberInput -> NumberInputView(
                        stringRes,
                        !disableInputs,
                        input,
                        generateClicked,
                        dataFlow[flattenedIndex],
                        Modifier.weight(1f).align(Alignment.CenterVertically)
                    )

                    is OptionsInput -> SelectionInputView(
                        stringRes,
                        !disableInputs,
                        input,
                        generateClicked,
                        dataFlow[flattenedIndex],
                        Modifier.weight(1f).align(Alignment.CenterVertically)
                    )

                    is BooleanInput -> CheckboxInputView(
                        !disableInputs,
                        input,
                        generateClicked,
                        dataFlow[flattenedIndex],
                        Modifier.weight(1f).align(Alignment.CenterVertically)
                    )
                }
                flattenedIndex++
            }
        }
    }

    //Generate
    Row(
        Modifier.padding(16.dp, 32.dp, 16.dp, 16.dp).fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(Modifier.padding(end = 8.dp).align(Alignment.CenterVertically).weight(1f)) {
            Row {
                Text(
                    modifier = Modifier.padding(0.dp, 0.dp, 4.dp, 0.dp),
                    text = stringRes(strings.temperature, listOf()),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp
                    )
                )
                Text(
                    text = sliderPosition.toString().split(".")[0] + "." + sliderPosition.toString()
                        .split(".")[1][0] + when (sliderPosition) {
                        in 0f..0.3f -> " " + stringRes(strings.basic, null)
                        in 0.3f..0.7f -> " " + stringRes(strings.normal, null)
                        else -> " " + stringRes(strings.creative, null)
                    },
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp
                    )
                )
            }
            Slider(
                modifier = Modifier.padding(0.dp),
                value = sliderPosition,
                onValueChange = { sliderPosition = it },
                steps = 10,
                colors = SliderDefaults.colors(
                    inactiveTickColor = MaterialTheme.colorScheme.primary,
                    activeTickColor = MaterialTheme.colorScheme.tertiary
                ),
                valueRange = 0f..1f
            )
        }

        FilledTonalButton(
            colors = PrimaryFilledButtonColors(),
            onClick = { generateClicked = true }
        ) {
            if (generationState?.isLoading == true) CircularProgressIndicator(
                modifier = Modifier.padding(end = 16.dp).align(Alignment.CenterVertically)
                    .size(28.dp),
                color = MaterialTheme.colorScheme.tertiary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
            Text(
                stringRes(strings.generate, listOf()),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
fun DataHolder(
    stringRes: (id: StringResource, args: List<Any>?) -> String,
    data: Data,
    onDataDeleted: (dataId: String) -> Unit,
    onWordClicked: (word: String, definition: String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    //Data Header
    Column(
        modifier = Modifier.animateContentSize(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow
            )
        )
    ) {
        Row(
            modifier = Modifier.padding(8.dp, 8.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(Modifier.weight(1f).padding(start = 8.dp)) {
                Text(
                    text = data.language,
                    style = MaterialTheme.typography.titleSmall.copy(color = MaterialTheme.colorScheme.tertiary)
                )
                Text(
                    text = data.topic,
                    modifier = Modifier.padding(vertical = 2.dp),
                    style = MaterialTheme.typography.titleMedium
                )
                val time = Instant.fromEpochSeconds(data.createdAt)
                    .toLocalDateTime(TimeZone.currentSystemDefault())
                val month = time.month.name
                Text(
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.secondary),
                    text = stringRes(
                        strings.on_x,
                        listOf(
                            time.dayOfMonth,
                            month[0] + month.substring(1).lowercase(),
                            time.year
                        )
                    ),
                )
            }
            Row(Modifier.align(Alignment.CenterVertically)) {
                IconButton(
                    colors = SecondaryIconButtonColors(),
                    onClick = { expanded = !expanded }
                ) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null
                    )
                }
                IconButton(
                    colors = SecondaryIconButtonColors(),
                    onClick = {
                        onDataDeleted(data.dataId!!)
                    }
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                }
            }
        }
        if (expanded) {
            Spacer(Modifier.padding(4.dp))
            DataContent(data, onWordClicked)
        }
    }

}

@Composable
fun DataContent(data: Data, onWordClicked: (word: String, definition: String) -> Unit) {
    Column(Modifier.fillMaxWidth()) {
        data.conversation.forEachIndexed { index, item ->
            Row(
                modifier = Modifier.padding(
                    start = if (index % 2 != 0) 32.dp else 0.dp,
                    end = if (index % 2 == 0) 32.dp else 0.dp,
                ).fillMaxWidth(),
                horizontalArrangement = if (index % 2 == 0) Arrangement.Start else Arrangement.End
            ) {
                val annotatedString = buildAnnotatedString {
                    append(item)
                    data.vocabulary.keys.forEach {
                        if (item.lowercase().contains(it.lowercase())) {
                            val start = item.lowercase().indexOf(it.lowercase())
                            val end = item.lowercase().indexOf(it.lowercase()) + it.length
                            addStyle(
                                SpanStyle(
                                    fontWeight = FontWeight.Bold,
                                    textDecoration = TextDecoration.Underline,
                                    color = MaterialTheme.colorScheme.tertiary
                                ), start = start, end = end
                            )
                            addStringAnnotation(
                                "Word", start = start, end = end, annotation = it
                            )
                        }
                    }
                }
                ClickableText(modifier = Modifier.border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.secondary,
                    shape = MaterialTheme.shapes.large
                ).padding(16.dp),
                    text = annotatedString,
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
                    onClick = { offset ->
                        annotatedString.getStringAnnotations(offset, offset).forEach {
                            onWordClicked(it.item, data.vocabulary[it.item]!!)
                        }
                    })
            }
            Spacer(Modifier.padding(8.dp))
        }
    }
}