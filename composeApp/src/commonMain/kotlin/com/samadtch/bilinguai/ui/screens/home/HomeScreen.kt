package com.samadtch.bilinguai.ui.screens.home

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
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
import com.samadtch.bilinguai.ui.common.DeleteAccountDialog
import com.samadtch.bilinguai.ui.common.DeleteDataDialog
import com.samadtch.bilinguai.ui.common.NumberInputView
import com.samadtch.bilinguai.ui.common.SelectionInputView
import com.samadtch.bilinguai.ui.common.TextInputView
import com.samadtch.bilinguai.ui.common.shimmerModifier
import com.samadtch.bilinguai.ui.theme.OutlinedButtonColors
import com.samadtch.bilinguai.ui.theme.PrimaryFilledButtonColors
import com.samadtch.bilinguai.ui.theme.PrimaryIconButtonColors
import com.samadtch.bilinguai.ui.theme.SecondaryIconButtonColors
import com.samadtch.bilinguai.utilities.exceptions.APIException.Companion.API_ERROR_AUTH
import com.samadtch.bilinguai.utilities.exceptions.APIException.Companion.API_ERROR_OTHER
import com.samadtch.bilinguai.utilities.exceptions.APIException.Companion.API_ERROR_RATE_LIMIT
import com.samadtch.bilinguai.utilities.exceptions.AuthException
import com.samadtch.bilinguai.utilities.exceptions.AuthException.Companion.AUTH_ERROR_USER_LOGGED_OUT
import com.samadtch.bilinguai.utilities.exceptions.DataException.Companion.DATA_ERROR_NETWORK
import com.samadtch.bilinguai.utilities.exceptions.DataException.Companion.DATA_ERROR_NOT_FOUND
import com.samadtch.bilinguai.utilities.exceptions.DataException.Companion.DATA_ERROR_SERVICE
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import moe.tlaster.precompose.flow.collectAsStateWithLifecycle
import kotlin.time.DurationUnit
import kotlin.time.toDuration

/***********************************************************************************************
 * ************************* UI States
 */
data class DataUiState(
    val isLoading: Boolean = true,
    val errorCode: String? = null,
    val isVerified: Boolean? = null,
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
    viewModel: HomeViewModel,
    onShowSnackbar: (Boolean, StringResource, List<Any>?, String?) -> Unit,
    inputs: List<List<BaseInput>>,
    openDrawer: () -> Unit,
    showInterstitialAd: () -> Unit,
    logout: () -> Unit,
) {
    //------------------------------- Declarations
    val coroutineScope = rememberCoroutineScope()

    //States
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val generationState by viewModel.generationState.collectAsStateWithLifecycle()
    val deletedState by viewModel.deletedState.collectAsStateWithLifecycle()

    //Dialogs
    var showDeleteDataDialog by rememberSaveable { mutableStateOf<String?>(null) }
    var sortByDate by rememberSaveable { mutableStateOf(true) }
    var word by rememberSaveable { mutableStateOf<String?>(null) }
    var definition by rememberSaveable { mutableStateOf<String?>(null) }

    //------------------------------- Effects
    //Data State
    LaunchedEffect(uiState.errorCode) {
        if (uiState.errorCode != null) {
            when (uiState.errorCode!!.split("::")[0]) {
                "AUTH" -> logout()
                "DATA" -> {
                    when (uiState.errorCode!!.split("::")[1].toInt()) {
                        DATA_ERROR_NETWORK -> onShowSnackbar(
                            false,
                            strings.error_network,
                            null,
                            null
                        )

                        DATA_ERROR_SERVICE -> onShowSnackbar(
                            false,
                            strings.error_server,
                            null,
                            null
                        )
                    }
                }
            }
        }
    }

    //Generation Error
    LaunchedEffect(generationState) {
        if (generationState?.success == true) showInterstitialAd()
        else if (generationState?.errorCode != null) {
            when (generationState?.errorCode!!.split("::")[0]) {
                "AUTH" -> logout()
                "API" -> {
                    when (generationState?.errorCode!!.split("::")[1].toInt()) {
                        DATA_ERROR_NETWORK -> onShowSnackbar(
                            false,
                            strings.error_network,
                            null,
                            null
                        )

                        API_ERROR_AUTH -> onShowSnackbar(false, strings.error_api_key, null, null)
                        API_ERROR_RATE_LIMIT -> onShowSnackbar(
                            false,
                            strings.error_rate_limit,
                            null,
                            null
                        )

                        API_ERROR_OTHER -> onShowSnackbar(false, strings.error_api, null, null)
                    }
                }

                "DATA" -> {
                    when (generationState?.errorCode!!.split("::")[1].toInt()) {
                        DATA_ERROR_NETWORK -> onShowSnackbar(
                            false,
                            strings.error_network,
                            null,
                            null
                        )

                        else -> onShowSnackbar(false, strings.error_server, null, null)
                    }
                }

                "VERIFICATION" -> onShowSnackbar(false, strings.error_verification, null, null)

                "COOLDOWN" -> {
                    //Show Snackbar
                    var minutes = generationState?.errorCode!!.split("::")[1]
                        .toLong()
                        .toDuration(DurationUnit.SECONDS).inWholeMinutes
                    if (minutes > 60) {
                        val hours = generationState?.errorCode!!.split("::")[1]
                            .toLong()
                            .toDuration(DurationUnit.SECONDS).inWholeHours
                        minutes -= hours * 60
                        onShowSnackbar(false, strings.cooldown_hours, listOf(hours, minutes), null)
                    } else onShowSnackbar(false, strings.cooldown_minutes, listOf(minutes), null)
                }
            }
        }
    }

    //Deleted Error
    LaunchedEffect(deletedState) {
        when (deletedState) {
            AUTH_ERROR_USER_LOGGED_OUT -> {
                logout()
                showDeleteDataDialog = null
            }

            DATA_ERROR_NETWORK -> {
                onShowSnackbar(false, strings.error_network, null, null)
                showDeleteDataDialog = null
            }

            DATA_ERROR_SERVICE -> {
                onShowSnackbar(false, strings.error_server, null, null)
                showDeleteDataDialog = null
            }

            DATA_ERROR_NOT_FOUND -> {
                onShowSnackbar(false, strings.error_server_not_found, null, null)
                showDeleteDataDialog = null
            }

            null -> showDeleteDataDialog = null
        }
    }

    //------------------------------- Dialogs
    if (word != null) {
        DefinitionDialog(word = word!!, definition = definition!!, onDismiss = {
            word = null
            definition = null
        })
    }
    if (showDeleteDataDialog != null) {
        DeleteDataDialog(
            id = showDeleteDataDialog!!,
            deleteData = { viewModel.deleteData(it) },
            deleteDataState = deletedState,
            onDismiss = { if (deletedState != -99) showDeleteDataDialog = null }
        )
    }

    //------------------------------- UI
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.primary
    ) {
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
                                text = stringResource(strings.appName),
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

                        if (uiState.isVerified == false) ClickableText(
                            modifier = Modifier.fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                                .border(
                                    BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                                    CircleShape
                                )
                                .align(Alignment.CenterHorizontally)
                                .padding(vertical = 16.dp, horizontal = 12.dp),
                            text = buildAnnotatedString {
                                append(stringResource(strings.verification_steps))
                                append("\n")
                                withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
                                    append(stringResource(strings.resend_verification_email))
                                }
                            },
                            style = MaterialTheme.typography.labelMedium.copy(
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 12.sp
                            ),
                            onClick = {
                                viewModel.verifyEmail()
                                coroutineScope.launch {
                                    onShowSnackbar(
                                        false,
                                        strings.verification_email_sent,
                                        null,
                                        null
                                    )
                                }
                            }
                        )

                        //Dynamic Form
                        DynamicForm(
                            inputs = inputs,
                            generationState = generationState,
                            onGenerateClicked = { inputs, temperature ->
                                viewModel.generateData(
                                    inputs,
                                    temperature
                                )
                            }
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
                            text = stringResource(strings.generated_data),
                            style = MaterialTheme.typography.titleLarge
                        )
                        OutlinedButton(
                            modifier = Modifier.align(Alignment.CenterVertically),
                            border = OutlinedButtonColors(),
                            onClick = {
                                sortByDate = !sortByDate
                                viewModel.sortData(sortByDate)
                            }
                        ) {
                            Icon(
                                modifier = Modifier.padding(0.dp, 0.dp, 8.dp, 0.dp)
                                    .align(Alignment.CenterVertically),
                                imageVector = Icons.AutoMirrored.Filled.Sort,
                                tint = MaterialTheme.colorScheme.secondary,
                                contentDescription = null
                            )
                            Text(
                                text = if (sortByDate) stringResource(strings.date)
                                else stringResource(strings.name),
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
            if (uiState.errorCode != null && uiState.errorCode == "DATA::32") {
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
                        text = stringResource(strings.error_empty),
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = MaterialTheme.colorScheme.secondary
                        )
                    )
                }

            }
        }
    }

}

@Composable
fun DynamicForm(
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
                    !disableInputs,
                    input,
                    generateClicked,
                    dataFlow[flattenedIndex]
                )

                is NumberInput -> NumberInputView(
                    !disableInputs,
                    input,
                    generateClicked,
                    dataFlow[flattenedIndex]
                )

                is OptionsInput -> SelectionInputView(
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
                        !disableInputs,
                        input,
                        generateClicked,
                        dataFlow[flattenedIndex],
                        Modifier.weight(1.25f).align(Alignment.CenterVertically)
                    )

                    is NumberInput -> NumberInputView(
                        !disableInputs,
                        input,
                        generateClicked,
                        dataFlow[flattenedIndex],
                        Modifier.weight(1f).align(Alignment.CenterVertically)
                    )

                    is OptionsInput -> SelectionInputView(
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
                        !disableInputs,
                        input,
                        generateClicked,
                        dataFlow[flattenedIndex],
                        Modifier.weight(1f).align(Alignment.CenterVertically)
                    )

                    is NumberInput -> NumberInputView(
                        !disableInputs,
                        input,
                        generateClicked,
                        dataFlow[flattenedIndex],
                        Modifier.weight(1f).align(Alignment.CenterVertically)
                    )

                    is OptionsInput -> SelectionInputView(
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
                    text = stringResource(strings.temperature),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp
                    )
                )
                Text(
                    text = sliderPosition.toString().split(".")[0] + "." + sliderPosition.toString()
                        .split(".")[1][0] + when (sliderPosition) {
                        in 0f..0.3f -> " " + stringResource(strings.basic)
                        in 0.3f..0.7f -> " " + stringResource(strings.normal)
                        else -> " " + stringResource(strings.creative)
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
                stringResource(strings.generate),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
fun DataHolder(
    data: Data,
    onDataDeleted: (dataId: String) -> Unit,
    onWordClicked: (word: String, definition: String) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    //Data Header
    Column(
        modifier = Modifier.animateContentSize(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMedium
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
                    text = stringResource(
                        strings.on_x,
                        time.dayOfMonth,
                        month[0] + month.substring(1).lowercase(),
                        time.year
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