package com.samadtch.bilinguai.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.runtime.mutableStateMapOf
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samadtch.bilinguai.Resources.strings
import dev.icerock.moko.resources.StringResource
import com.samadtch.bilinguai.ui.common.ForgotPasswordDialog
import com.samadtch.bilinguai.ui.theme.SecondaryFilledButtonColors
import com.samadtch.bilinguai.ui.theme.SecondaryTextFieldColors
import com.samadtch.bilinguai.utilities.exceptions.AuthException.Companion.AUTH_ERROR_NETWORK
import com.samadtch.bilinguai.utilities.exceptions.AuthException.Companion.AUTH_ERROR_USER_NOT_FOUND
import com.samadtch.bilinguai.utilities.exceptions.AuthException.Companion.AUTH_ERROR_WRONG_EMAIL
import com.samadtch.bilinguai.utilities.exceptions.AuthException.Companion.AUTH_ERROR_WRONG_PASSWORD

/***********************************************************************************************
 * ************************* UI States
 */
data class LoginUiState(
    val isLoading: Boolean = true,
    val errorCode: Int? = null,
    val userId: String? = null
)

/***********************************************************************************************
 * ************************* UI States
 */
@Composable
fun LoginScreen(
    stringRes: (id: StringResource, args: List<Any>?) -> String,
    onShowSnackbar: suspend (Boolean, String, String?) -> Boolean,
    //Resetting Password
    resetPassword: (String) -> Unit,
    passwordResetState: Int?,
    //Go Register
    goRegister: () -> Unit,
    //Login
    login: (String, String) -> Unit,
    loginState: LoginUiState?,
) {
    //------------------------------- Declarations
    var disableInputs by remember { mutableStateOf(false) }
    val annotatedString = buildAnnotatedString {
        append(stringRes(strings.new_msg, null))
        append(" ")
        withStyle(
            style = SpanStyle(
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline,
                color = MaterialTheme.colorScheme.tertiary
            )
        ) {
            append(stringRes(strings.register, null))
        }
        addStringAnnotation(
            tag = "Auth",
            start = 17,
            end = 26,
            annotation = "Clickable"
        )
    }
    var showResetPasswordDialog by rememberSaveable { mutableStateOf(false) }

    //------------------------------- Dialogs
    if (showResetPasswordDialog) ForgotPasswordDialog(
        stringRes = stringRes,
        resetPassword = resetPassword,
        resetPasswordState = passwordResetState,
        onDismiss = {
            if (passwordResetState != -99) showResetPasswordDialog = false
        }
    )

    //------------------------------- Effect
    LaunchedEffect(loginState) {
        if (loginState?.isLoading == true) disableInputs = true
        else if (loginState?.errorCode == AUTH_ERROR_NETWORK) onShowSnackbar(
            false,
            stringRes(strings.error_network, null),
            null
        )
    }

    LaunchedEffect(passwordResetState) {
        when (passwordResetState) {
            AUTH_ERROR_NETWORK -> {
                showResetPasswordDialog = false
                onShowSnackbar(
                    false,
                    stringRes(strings.error_network, null),
                    null
                )
            }

            AUTH_ERROR_USER_NOT_FOUND -> {
                showResetPasswordDialog = false
                onShowSnackbar(
                    false,
                    stringRes(strings.error_account_no_exists, null),
                    null
                )
            }

            -1 -> {
                showResetPasswordDialog = false
                onShowSnackbar(
                    true,
                    stringRes(strings.reset_email_sent, null),
                    null
                )
            }

            null -> {}
        }
    }

    //------------------------------- UI
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {

        Column(Modifier.weight(1f), verticalArrangement = Arrangement.SpaceEvenly) {
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = stringRes(strings.appName, null),
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = MaterialTheme.colorScheme.tertiary,
                    fontSize = 48.sp
                )
            )//Logo

            //Form and Button
            LoginForm(
                stringRes,
                !disableInputs,
                { showResetPasswordDialog = true },
                login,
                loginState
            )
        }

        //Routing to Login
        ClickableText(
            modifier = Modifier.padding(bottom = 16.dp).align(Alignment.CenterHorizontally),
            text = annotatedString,
            style = MaterialTheme.typography.labelLarge.copy(color = Color.White),
            onClick = { offset ->
                if (annotatedString.getStringAnnotations(offset, offset).isNotEmpty()) goRegister()
            }
        )
    }
}

@Composable
private fun LoginForm(
    stringRes: (id: StringResource, args: List<Any>?) -> String,
    enable: Boolean,
    onResetPasswordClicked: () -> Unit,
    login: (String, String) -> Unit,
    loginState: LoginUiState?
) {
    //------------------------------- Declarations
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    val errors = remember { mutableStateMapOf<String, String>() }

    //------------------------------- Effects
    LaunchedEffect(loginState) {
        if (loginState != null)
            when (loginState.errorCode) {
                AUTH_ERROR_WRONG_EMAIL -> {
                    errors["email"] = stringRes(strings.error_email_no_exists, null)
                }

                AUTH_ERROR_WRONG_PASSWORD -> {
                    errors["password"] = stringRes(strings.error_password_wrong, null)
                    errors["email"] = stringRes(strings.error_email_might_no_exists, null)
                }

                null -> {
                    /* DO NOTHING */
                }
            }
    }

    //------------------------------- UI
    Column(Modifier.fillMaxWidth()) {
        TextField(
            modifier = Modifier.fillMaxWidth().wrapContentHeight()
                .padding(16.dp, 0.dp, 16.dp, 4.dp),
            colors = SecondaryTextFieldColors(),
            textStyle = MaterialTheme.typography.labelSmall,
            enabled = enable,
            placeholder = {
                Text(
                    text = stringRes(strings.email_placeholder, null),
                    style = MaterialTheme.typography.labelSmall
                )
            },
            isError = errors["email"] != null,
            supportingText = { if (errors["email"] != null) Text(
                text = errors["email"]!!,
                style = MaterialTheme.typography.bodyMedium
            ) },
            value = email,
            onValueChange = { email = it },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        )
        TextField(
            modifier = Modifier.fillMaxWidth().wrapContentHeight()
                .padding(16.dp, 0.dp, 16.dp, 4.dp),
            enabled = enable,
            colors = SecondaryTextFieldColors(),
            textStyle = MaterialTheme.typography.labelSmall,
            placeholder = {
                Text(
                    text = stringRes(strings.password_placeholder, null),
                    style = MaterialTheme.typography.labelSmall
                )
            },
            isError = errors["password"] != null,
            supportingText = { if (errors["password"] != null) Text(
                text = errors["password"]!!,
                style = MaterialTheme.typography.bodyMedium
            ) },
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
        ClickableText(
            modifier = Modifier.padding(end = 16.dp).align(Alignment.End),
            style = MaterialTheme.typography.labelLarge.copy(MaterialTheme.colorScheme.tertiary),
            text = buildAnnotatedString { append(stringRes(strings.forgot_password, null)) },
            onClick = { onResetPasswordClicked() }
        )
        FilledTonalButton(
            modifier = Modifier.padding(top = 24.dp).align(Alignment.CenterHorizontally),
            colors = SecondaryFilledButtonColors(),
            onClick = {
                errors.clear()
                //Validate Form
                if (email.isBlank()) errors["email"] =
                    stringRes(strings.error_email_required, null)
                else if (!Regex("""^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}${'$'}""").matches(
                        email
                    )
                )
                    errors["email"] = stringRes(strings.error_email_invalid, null)
                if (password.isBlank()) errors["password"] =
                    stringRes(strings.error_password_required, null)
                else if (password.length < 6) errors["password"] =
                    stringRes(strings.error_password_weak, null)

                //Register
                if (errors.isEmpty()) login(email, password)
            }
        ) {
            if (loginState != null && loginState.isLoading) CircularProgressIndicator(
                modifier = Modifier.padding(0.dp, 0.dp, 16.dp, 0.dp).size(28.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
            Text(
                stringRes(strings.login, null),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}