package com.samadtch.bilinguai.ui.screens.auth

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
import androidx.compose.material3.Surface
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
import com.samadtch.bilinguai.ui.theme.SecondaryFilledButtonColors
import com.samadtch.bilinguai.ui.theme.SecondaryTextFieldColors
import com.samadtch.bilinguai.utilities.exceptions.AuthException.Companion.AUTH_ERROR_EMAIL_ALREADY_IN_USE
import com.samadtch.bilinguai.utilities.exceptions.AuthException.Companion.AUTH_ERROR_INVALID_EMAIL
import com.samadtch.bilinguai.utilities.exceptions.AuthException.Companion.AUTH_ERROR_NETWORK
import com.samadtch.bilinguai.utilities.exceptions.AuthException.Companion.AUTH_ERROR_WEAK_PASSWORD
import dev.icerock.moko.resources.compose.stringResource
import moe.tlaster.precompose.flow.collectAsStateWithLifecycle

/***********************************************************************************************
 * ************************* UI States
 */
data class RegisterUiState(
    val isLoading: Boolean = true,
    val errorCode: Int? = null,
    val userId: String? = null
)

/***********************************************************************************************
 * ************************* UI
 */
@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onShowSnackbar: (Boolean, StringResource, List<Any>?, String?) -> Unit,
    goLogin: () -> Unit,
    goHome: () -> Unit
) {
    //------------------------------- Declarations
    val registerState by viewModel.registerState.collectAsStateWithLifecycle()
    var disableInputs by remember { mutableStateOf(false) }
    val annotatedString = buildAnnotatedString {
        append(stringResource(strings.old_msg))
        append(" ")
        withStyle(
            style = SpanStyle(
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline,
                color = MaterialTheme.colorScheme.tertiary
            )
        ) {
            append(stringResource(strings.login))
        }
        addStringAnnotation(
            tag = "Auth",
            start = 26,
            end = 31,
            annotation = "Clickable"
        )
    }

    //------------------------------- Effect
    LaunchedEffect(registerState) {
        if (registerState?.isLoading == true) disableInputs = true
        else if (registerState?.userId != null) goHome()
        else {
            disableInputs = false
            if (registerState?.errorCode == AUTH_ERROR_NETWORK)
                onShowSnackbar(false, strings.error_network, null, null)
        }
    }

    //------------------------------- UI
    Surface(color = MaterialTheme.colorScheme.primary) {
        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.SpaceEvenly) {
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = stringResource(strings.appName),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        color = MaterialTheme.colorScheme.tertiary,
                        fontSize = 48.sp
                    )
                )//Logo

                //Form and Button
                RegisterForm(
                    !disableInputs,
                    { email, password -> viewModel.register(email, password) },
                    registerState
                )
            }

            //Routing to Login
            ClickableText(
                modifier = Modifier.padding(bottom = 16.dp).align(Alignment.CenterHorizontally),
                text = annotatedString,
                style = MaterialTheme.typography.labelLarge.copy(color = Color.White),
                onClick = { offset ->
                    if (annotatedString.getStringAnnotations(offset, offset).isNotEmpty()) goLogin()
                }
            )
        }
    }
}

@Composable
private fun RegisterForm(
    enable: Boolean,
    register: (String, String) -> Unit,
    registerState: RegisterUiState?
) {
    //------------------------------- Declarations
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    val errors = remember { mutableStateMapOf<String, StringResource>() }

    //------------------------------- Effects
    LaunchedEffect(registerState) {
        if (registerState != null)
            when (registerState.errorCode) {
                AUTH_ERROR_EMAIL_ALREADY_IN_USE -> {
                    errors["email"] = strings.error_account_exists
                }

                AUTH_ERROR_INVALID_EMAIL -> {
                    errors["email"] = strings.error_email_invalid
                }

                AUTH_ERROR_WEAK_PASSWORD -> {
                    errors["password"] = strings.error_password_weak
                }

                null -> {}
            }
    }

    //------------------------------- UI
    Column(Modifier.fillMaxWidth()) {
        TextField(
            modifier = Modifier.fillMaxWidth().wrapContentHeight()
                .padding(16.dp, 0.dp, 16.dp, 4.dp),
            colors = SecondaryTextFieldColors(),
            enabled = enable,
            textStyle = MaterialTheme.typography.labelSmall,
            placeholder = {
                Text(
                    text = stringResource(strings.email_placeholder),
                    style = MaterialTheme.typography.labelSmall
                )
            },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            isError = errors["email"] != null,
            supportingText = {
                if (errors["email"] != null) Text(
                    text = stringResource(errors["email"]!!),
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            value = email,
            onValueChange = { email = it }
        )
        TextField(
            modifier = Modifier.fillMaxWidth().wrapContentHeight()
                .padding(16.dp, 0.dp, 16.dp, 4.dp),
            colors = SecondaryTextFieldColors(),
            enabled = enable,
            textStyle = MaterialTheme.typography.labelSmall,
            placeholder = {
                Text(
                    text = stringResource(strings.password_placeholder),
                    style = MaterialTheme.typography.labelSmall
                )
            },
            isError = errors["password"] != null,
            supportingText = {
                if (errors["password"] != null) Text(
                    text = stringResource(errors["password"]!!),
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
        TextField(
            modifier = Modifier.fillMaxWidth().wrapContentHeight()
                .padding(16.dp, 0.dp, 16.dp, 4.dp),
            colors = SecondaryTextFieldColors(),
            textStyle = MaterialTheme.typography.labelSmall,
            enabled = enable,
            placeholder = {
                Text(
                    text = stringResource(strings.confirm_placeholder),
                    style = MaterialTheme.typography.labelSmall
                )
            },
            isError = errors["confirmPassword"] != null,
            supportingText = {
                if (errors["confirmPassword"] != null) Text(
                    text = stringResource(errors["confirmPassword"]!!),
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
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
        FilledTonalButton(
            modifier = Modifier.padding(top = 24.dp).align(Alignment.CenterHorizontally),
            colors = SecondaryFilledButtonColors(),
            onClick = {
                errors.clear()
                //Validate Form
                if (email.isBlank()) errors["email"] = strings.error_email_required
                else if (!Regex("""^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}${'$'}""")
                        .matches(email)
                ) errors["email"] = strings.error_email_invalid

                if (password.isBlank()) errors["password"] = strings.error_password_required
                else if (password.length < 6) errors["password"] = strings.error_password_weak
                if (confirmPassword.isBlank()) errors["confirmPassword"] =
                    strings.error_confirm_password_required
                if (errors.isEmpty() && confirmPassword != password)
                    errors["confirmPassword"] = strings.error_passwords_unmatching

                //Register
                if (errors.isEmpty()) register(email, password)
            }
        ) {
            if (registerState != null && registerState.isLoading) CircularProgressIndicator(
                modifier = Modifier.padding(0.dp, 0.dp, 16.dp, 0.dp).size(28.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
            Text(
                stringResource(strings.register),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}