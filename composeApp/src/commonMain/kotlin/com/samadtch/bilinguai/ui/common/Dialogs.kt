package com.samadtch.bilinguai.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samadtch.bilinguai.Resources.strings
import com.samadtch.bilinguai.ui.theme.ErrorFilledButtonColors
import com.samadtch.bilinguai.ui.theme.SecondaryFilledButtonColors
import com.samadtch.bilinguai.ui.theme.SecondaryTextFieldColors
import com.samadtch.bilinguai.utilities.exceptions.AuthException.Companion.AUTH_ERROR_USER_WRONG_CREDENTIALS
import dev.icerock.moko.resources.StringResource
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun DefinitionDialog(
    word: String,
    definition: String,
    onDismiss: () -> Unit = {},
) {
    AlertDialog(containerColor = MaterialTheme.colorScheme.primary,
        onDismissRequest = { onDismiss() },
        confirmButton = { /*Do Nothing*/ },
        title = {
            Text(
                text = word,
                style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.tertiary)
            )
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
        })
}

@Composable
fun DeleteDataDialog(
    stringRes: (id: StringResource, args: List<Any>?) -> String,
    id: String,
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
                        stringRes(strings.delete, null),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        },
        title = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = stringRes(strings.delete_data, null),
                style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.error)
            )
        },
        text = {
            Text(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                textAlign = TextAlign.Center,
                text = stringRes(strings.delete_data_confirmation, null),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.secondary, fontSize = 14.sp
                )
            )
        })
}

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

@Composable
fun ForgotPasswordDialog(
    stringRes: (id: StringResource, args: List<Any>?) -> String,
    resetPassword: (String) -> Unit,
    resetPasswordState: Int?,
    onDismiss: () -> Unit = {},
) {
    //------------------------------- Declarations
    var email by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(containerColor = MaterialTheme.colorScheme.primary,
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                FilledTonalButton(
                    colors = SecondaryFilledButtonColors(),
                    onClick = {
                        error = null
                        //Validate Form
                        if (email.isBlank()) error =
                            stringRes(strings.error_email_required, null)
                        else if (!Regex("""^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}${'$'}""").matches(
                                email
                            )
                        )
                            error = stringRes(strings.error_email_invalid, null)

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
                        stringRes(strings.send, null),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        },
        title = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringRes(strings.forgot_password, null),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.tertiary)
            )
        },
        text = {
            Column(Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    textAlign = TextAlign.Center,
                    text = stringRes(strings.forgot_password_explication, null),
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
                            text = stringRes(strings.email_placeholder, null),
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    isError = error != null,
                    supportingText = {
                        if (error != null) Text(
                            text = error!!,
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
    stringRes: (id: StringResource, args: List<Any>?) -> String,
    deleteAccount: (String) -> Unit,
    deleteAccountState: Int?,
    onDismiss: () -> Unit = {},
) {
    //------------------------------- Declarations
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    //------------------------------- Effects
    LaunchedEffect(deleteAccountState) {
        if (deleteAccountState == AUTH_ERROR_USER_WRONG_CREDENTIALS)
            error = stringRes(strings.error_password_wrong, null)
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
                            error = stringRes(strings.error_password_required, null)
                        else if (password.length < 6)
                            error = stringRes(strings.error_password_weak, null)

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
                        stringRes(strings.delete, null),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        },
        title = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringRes(strings.delete_account, null),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.error)
            )
        },
        text = {
            Column(Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    textAlign = TextAlign.Center,
                    text = stringRes(strings.delete_account_explication, null),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.secondary, fontSize = 14.sp
                    )
                )
                TextField(
                    modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(top = 8.dp),
                    colors = SecondaryTextFieldColors(),
                    placeholder = {
                        Text(
                            text = stringRes(strings.password_placeholder, null),
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    isError = error != null,
                    supportingText = {
                        if (error != null) Text(
                            text = error!!,
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

fun reformatTime(time: Int): String {
    return if (time < 10) "0$time" else time.toString()
}