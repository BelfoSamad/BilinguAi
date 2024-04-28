package com.samadtch.bilinguai.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samadtch.bilinguai.Resources.strings
import com.samadtch.bilinguai.ui.theme.ErrorFilledButtonColors
import com.samadtch.bilinguai.ui.theme.PrimaryIconButtonColors
import com.samadtch.bilinguai.ui.theme.SecondaryFilledButtonColors
import com.samadtch.bilinguai.ui.theme.SecondaryTextFieldColors
import com.samadtch.bilinguai.utilities.exceptions.AuthException.Companion.AUTH_ERROR_USER_WRONG_CREDENTIALS
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun DictionaryDialog(
    dictionary: Map<String, String>,
    unsaveWord: (String, String) -> Unit,
    onDismiss: () -> Unit = {},
) {
    AlertDialog(
        containerColor = MaterialTheme.colorScheme.primary,
        onDismissRequest = { onDismiss() },
        confirmButton = { /*Do Nothing*/ },
        title = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = stringResource(strings.dictionary),
                style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.tertiary)
            )
        },
        text = {
            if (dictionary.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
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
                        text = stringResource(strings.dictionary_error_empty),
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = MaterialTheme.colorScheme.secondary
                        )
                    )
                }
            } else LazyColumn {
                items(items = dictionary.keys.toList()) {
                    Spacer(Modifier.padding(8.dp))
                    Row {
                        OutlinedIconButton(
                            modifier = Modifier.padding(end = 16.dp)
                                .align(Alignment.CenterVertically),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
                            onClick = { unsaveWord(it, dictionary[it]!!) }) {
                            Icon(
                                imageVector = Icons.Outlined.Remove,
                                tint = MaterialTheme.colorScheme.secondary,
                                contentDescription = null
                            )
                        }
                        Column(Modifier.weight(1f)) {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.tertiary)
                            )
                            Text(
                                text = dictionary[it]!!,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontSize = 14.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun ReportDialog(
    id: String,
    topic: String,
    onReport: (String) -> Unit,
    onDismiss: () -> Unit = {},
) {
    AlertDialog(
        containerColor = MaterialTheme.colorScheme.primary,
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = stringResource(strings.report_data),
                style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.error)
            )
        },
        text = {
            Text(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                textAlign = TextAlign.Center,
                text = stringResource(strings.report_data_confirmation, topic),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.secondary, fontSize = 14.sp
                )
            )
        },
        confirmButton = {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                FilledTonalButton(
                    colors = ErrorFilledButtonColors(),
                    onClick = { onReport(id) }
                ) {
                    Text(
                        stringResource(strings.report),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        },
    )
}

@Composable
fun DefinitionDialog(
    word: String,
    definition: String,
    saved: Boolean = false,
    saveWord: (String, String) -> Unit,
    onDismiss: () -> Unit = {},
) {
    AlertDialog(
        containerColor = MaterialTheme.colorScheme.primary,
        onDismissRequest = { onDismiss() },
        confirmButton = { /*Do Nothing*/ },
        title = {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    text = word,
                    style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.tertiary)
                )
                FilledTonalIconButton(
                    colors = PrimaryIconButtonColors(),
                    onClick = { saveWord(word, definition) }) {
                    Icon(if (saved) Icons.Outlined.Bookmark else Icons.Outlined.BookmarkAdd, null)
                }
            }
        },
        text = {
            Text(
                modifier = Modifier.padding(0.dp, 8.dp, 0.dp, 0.dp),
                text = definition,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 14.sp
                )
            )
        }
    )
}

@Composable
fun TranslationDialog(
    message: String,
    translation: String,
    onDismiss: () -> Unit = {},
) {
    AlertDialog(containerColor = MaterialTheme.colorScheme.primary,
        onDismissRequest = { onDismiss() },
        confirmButton = { /*Do Nothing*/ },
        title = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = stringResource(strings.translation),
                style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.tertiary)
            )
        },
        text = {
            Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                Text(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
                    textAlign = TextAlign.Center,
                    text = message,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 14.sp
                    )
                )
                Icon(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    tint = MaterialTheme.colorScheme.secondary,
                    imageVector = Icons.Default.Sync,
                    contentDescription = null
                )
                Text(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    textAlign = TextAlign.Center,
                    text = translation,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 14.sp
                    )
                )
            }
        })
}

@Composable
fun DeleteDataDialog(
    id: String,
    topic: String,
    deleteData: (String) -> Unit,
    deleteDataState: Int?,
    onDismiss: () -> Unit = {},
) {
    AlertDialog(
        containerColor = MaterialTheme.colorScheme.primary,
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                FilledTonalButton(
                    colors = ErrorFilledButtonColors(),
                    onClick = { deleteData(id) }
                ) {
                    if (deleteDataState == -99) CircularProgressIndicator(
                        modifier = Modifier.padding(0.dp, 0.dp, 16.dp, 0.dp).size(28.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                    Text(
                        stringResource(strings.delete),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        },
        title = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = stringResource(strings.delete_data),
                style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.error)
            )
        },
        text = {
            Text(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                textAlign = TextAlign.Center,
                text = stringResource(strings.delete_data_confirmation, topic),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.secondary, fontSize = 14.sp
                )
            )
        }
    )
}

/*
@Composable
fun CreditDialog(
    stringRes: (id: StringResource, args: List<Any>?) -> String,
    credit: Int,
    dailyTimestamp: Long?,
    onCreditInc: () -> Unit,
    onDailyTimestampUpdated: (timestamp: Long) -> Unit,
    onRewardedAdClicked: () -> Unit,
    onDismiss: () -> Unit = {},
) {
    //------------------------------- Declarations
    var timeToConvo by remember { mutableStateOf("") }
    var timesUp by remember { mutableStateOf(false) }

    //------------------------------- Effects
    LaunchedEffect(true) {
        while ((dailyTimestamp?.minus(Clock.System.now().epochSeconds) ?: 0) > 0) {
            val time = Instant.fromEpochSeconds(
                dailyTimestamp?.minus(Clock.System.now().epochSeconds) ?: 0
            ).toLocalDateTime(TimeZone.UTC)
            timeToConvo = reformatTime(time.hour) + ":" +
                    reformatTime(time.minute) + ":" +
                    reformatTime(time.second)
            delay(1000)
        }
        timesUp = true
    }

    //------------------------------- UI
    AlertDialog(onDismissRequest = { onDismiss() }, confirmButton = { /*Do Nothing*/ }, title = {
        Text(
            text = stringRes(strings.credit_remaining_x, listOf(credit)),
            style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.tertiary)
        )
    }, text = {
        Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            //Daily Credit
            if (dailyTimestamp == null || timesUp)
                ClickableText(
                    modifier = Modifier.padding(top = 24.dp, bottom = 16.dp),
                    text = buildAnnotatedString {
                        append(stringRes(strings.get_daily_credit, null))
                    },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 16.sp,
                        color = Color.White
                    ),
                    onClick = {
                        onCreditInc()//Add 1 Credit
                        onDailyTimestampUpdated(Clock.System.now().epochSeconds + 86400)//Set new DailyTimestamp + 24h
                    }
                )
            else Text(
                modifier = Modifier.padding(top = 24.dp, bottom = 16.dp),
                text = stringRes(strings.x_time_remaining, listOf(timeToConvo)),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            )

            //Rewarded Credit
            ClickableText(
                modifier = Modifier.padding(vertical = 16.dp),
                text = buildAnnotatedString { append(stringRes(strings.watch_ad, null)) },
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp,
                    color = Color.White
                ),
                onClick = { onRewardedAdClicked() }
            )

            //Buy Credit
            Text(
                modifier = Modifier.padding(top = 16.dp),
                text = stringRes(strings.buy_credit, null),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            )
        }
    })
}

fun reformatTime(time: Int): String {
    return if (time < 10) "0$time" else time.toString()
}
*/

@Composable
fun ForgotPasswordDialog(
    resetPassword: (String) -> Unit,
    resetPasswordState: Int?,
    onDismiss: () -> Unit = {},
) {
    //------------------------------- Declarations
    var email by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<StringResource?>(null) }

    AlertDialog(containerColor = MaterialTheme.colorScheme.primary,
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                FilledTonalButton(
                    colors = SecondaryFilledButtonColors(),
                    onClick = {
                        error = null
                        //Validate Form
                        if (email.isBlank()) error = strings.error_email_required
                        else if (!Regex("""^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}${'$'}""").matches(
                                email
                            )
                        ) error = strings.error_email_invalid

                        //Register
                        if (error == null) resetPassword(email)
                    }
                ) {
                    if (resetPasswordState == -99) CircularProgressIndicator(
                        modifier = Modifier.padding(0.dp, 0.dp, 16.dp, 0.dp).size(28.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                    Text(
                        stringResource(strings.send),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        },
        title = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(strings.forgot_password),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.tertiary)
            )
        },
        text = {
            Column(Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    textAlign = TextAlign.Center,
                    text = stringResource(strings.forgot_password_explication),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 14.sp
                    )
                )
                TextField(
                    modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(top = 8.dp),
                    colors = SecondaryTextFieldColors(),
                    textStyle = MaterialTheme.typography.labelSmall,
                    placeholder = {
                        Text(
                            text = stringResource(strings.email_placeholder),
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    isError = error != null,
                    supportingText = {
                        if (error != null) Text(
                            text = stringResource(error!!),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    value = email,
                    onValueChange = { email = it }
                )
            }
        })
}

@Composable
fun DeleteAccountDialog(
    deleteAccount: (String) -> Unit,
    deleteAccountState: Int?,
    onDismiss: () -> Unit = {},
) {
    //------------------------------- Declarations
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<StringResource?>(null) }

    //------------------------------- Effects
    LaunchedEffect(deleteAccountState) {
        if (deleteAccountState == AUTH_ERROR_USER_WRONG_CREDENTIALS)
            error = strings.error_password_wrong
    }

    //------------------------------- UI
    AlertDialog(containerColor = MaterialTheme.colorScheme.primary,
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                FilledTonalButton(
                    colors = ErrorFilledButtonColors(),
                    onClick = {
                        error = null
                        //Validate Form
                        if (password.isBlank())
                            error = strings.error_password_required
                        else if (password.length < 6)
                            error = strings.error_password_weak

                        //Register
                        if (error == null) deleteAccount(password)
                    }
                ) {
                    if (deleteAccountState == -99) CircularProgressIndicator(
                        modifier = Modifier.padding(0.dp, 0.dp, 16.dp, 0.dp).size(28.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                    Text(
                        text = stringResource(strings.delete),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        },
        title = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(strings.delete_account),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.error)
            )
        },
        text = {
            Column(Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    textAlign = TextAlign.Center,
                    text = stringResource(strings.delete_account_explication),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.secondary, fontSize = 14.sp
                    )
                )
                TextField(
                    modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(top = 8.dp),
                    colors = SecondaryTextFieldColors(),
                    placeholder = {
                        Text(
                            text = stringResource(strings.set_password_placeholder),
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    isError = error != null,
                    supportingText = {
                        if (error != null) Text(
                            text = stringResource(error!!),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    value = password,
                    onValueChange = { password = it },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    trailingIcon = {
                        val image = if (passwordVisible)
                            Icons.Filled.Visibility
                        else Icons.Filled.VisibilityOff
                        // Please provide localized description for accessibility services
                        val description = if (passwordVisible) "Hide password" else "Show password"
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, description)
                        }
                    }
                )
            }
        })
}