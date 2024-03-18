package com.samadtch.bilinguai

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.IntegrationInstructions
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samadtch.bilinguai.ui.common.CustomSnackbar
import com.samadtch.bilinguai.ui.common.DeleteAccountDialog
import com.samadtch.bilinguai.ui.screens.AppViewModel
import com.samadtch.bilinguai.ui.theme.AppTheme
import com.samadtch.bilinguai.utilities.exceptions.AuthException
import com.samadtch.bilinguai.utilities.exceptions.DataException
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import moe.tlaster.precompose.PreComposeApp
import moe.tlaster.precompose.flow.collectAsStateWithLifecycle
import moe.tlaster.precompose.koin.koinViewModel

data class AppUiState(
    val email: String? = null,
    val links: Map<String, String> = mapOf()
)

@Composable
fun App(
    stringRes: (StringResource, List<Any>?) -> String,
    onSplashScreenDone: () -> Unit,
    openWebPage: (String) -> Unit,
    getVersionName: () -> String,
    reviewApp: () -> Unit,
    showInterstitialAd: () -> Unit,
    speak: (String, String, Int) -> Boolean,
    ttsState: StateFlow<Int?>
) {
    PreComposeApp {
        //------------------------------- Declarations
        val scope = rememberCoroutineScope()
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val snackbarHostState = remember { SnackbarHostState() }
        var snackbarSuccess by remember { mutableStateOf(false) }
        var current by remember { mutableStateOf<String?>(null) }
        var showDeleteAccountDialog by rememberSaveable { mutableStateOf(false) }

        //ViewModel
        val viewModel = koinViewModel(AppViewModel::class)
        val appState by viewModel.initUiState.collectAsStateWithLifecycle()
        val deleteAccountState by viewModel.deleteAccountState.collectAsStateWithLifecycle()
        val outState by viewModel.outState.collectAsStateWithLifecycle()
        val tts by ttsState.collectAsStateWithLifecycle()

        //------------------------------- Effects
        //Delete Account
        LaunchedEffect(deleteAccountState) {
            println(deleteAccountState)
            if (deleteAccountState != null) when (deleteAccountState) {

                DataException.DATA_ERROR_SERVICE -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = stringRes(Resources.strings.error_server, null),
                            actionLabel = null,
                            duration = SnackbarDuration.Short,
                        )
                    }
                }

                AuthException.AUTH_ERROR_NETWORK -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = stringRes(Resources.strings.error_network, null),
                            actionLabel = null,
                            duration = SnackbarDuration.Short,
                        )
                    }
                }

                AuthException.AUTH_ERROR_USER_NOT_FOUND -> {
                    viewModel.logout()
                    showDeleteAccountDialog = false
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = stringRes(
                                Resources.strings.error_account_no_exists,
                                null
                            ),
                            actionLabel = null,
                            duration = SnackbarDuration.Short,
                        )
                    }
                }

                -1 -> showDeleteAccountDialog = false
            }
        }

        //------------------------------- UI
        AppTheme {
            //------------------------------- Dialogs
            if (showDeleteAccountDialog) {
                DeleteAccountDialog(
                    deleteAccount = viewModel::deleteAccount,
                    deleteAccountState = deleteAccountState,
                    onDismiss = { if (deleteAccountState != -99) showDeleteAccountDialog = false }
                )
            }

            //------------------------------- UI
            ModalNavigationDrawer(
                drawerState = drawerState,
                gesturesEnabled = current == "/home",
                drawerContent = {
                    ModalDrawerSheet {
                        Column(
                            modifier = Modifier
                                .padding(start = 24.dp, top = 64.dp)
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    modifier = Modifier
                                        .padding(top = 24.dp, bottom = 48.dp)
                                        .align(Alignment.CenterHorizontally),
                                    text = stringResource(Resources.strings.appName),
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        color = MaterialTheme.colorScheme.tertiary,
                                        fontSize = 32.sp
                                    )
                                )//Logo

                                NavigationDrawerItem(
                                    label = {
                                        Text(
                                            text = stringResource(Resources.strings.privacy_policy),
                                            style = MaterialTheme.typography.labelLarge.copy(
                                                color = MaterialTheme.colorScheme.tertiary
                                            )
                                        )
                                    },
                                    selected = false,
                                    onClick = { openWebPage(appState.links["privacy"]!!) },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.tertiary
                                        )
                                    }
                                )//Privacy Policy

                                NavigationDrawerItem(
                                    label = {
                                        Text(
                                            text = stringResource(Resources.strings.tos),
                                            style = MaterialTheme.typography.labelLarge.copy(
                                                color = MaterialTheme.colorScheme.tertiary
                                            )
                                        )
                                    },
                                    selected = false,
                                    onClick = { openWebPage(appState.links["tos"]!!) },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.Description,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.tertiary
                                        )
                                    }
                                )//Terms of Use

                                NavigationDrawerItem(
                                    label = {
                                        Text(
                                            text = stringResource(Resources.strings.check_developer),
                                            style = MaterialTheme.typography.labelLarge.copy(
                                                color = MaterialTheme.colorScheme.tertiary
                                            )
                                        )
                                    },
                                    selected = false,
                                    onClick = { openWebPage("https://play.google.com/store/apps/developer?id=${appState.links["developer"]}") },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.IntegrationInstructions,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.tertiary
                                        )
                                    }
                                )//Check Developer

                                NavigationDrawerItem(
                                    label = {
                                        Text(
                                            text = stringResource(Resources.strings.review_app),
                                            style = MaterialTheme.typography.labelLarge.copy(
                                                color = MaterialTheme.colorScheme.tertiary
                                            )
                                        )
                                    },
                                    selected = false,
                                    onClick = { reviewApp() },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.RateReview,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.tertiary
                                        )
                                    }
                                )//Review App

                            } //Top Section
                            Column(Modifier.padding(bottom = 16.dp)) {
                                NavigationDrawerItem(
                                    label = {
                                        Text(
                                            text = stringResource(Resources.strings.logout),
                                            style = MaterialTheme.typography.labelLarge.copy(
                                                color = MaterialTheme.colorScheme.error
                                            )
                                        )
                                    },
                                    selected = false,
                                    onClick = {
                                        viewModel.logout()
                                        scope.launch { drawerState.close() }
                                    },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Default.Logout,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                )//Logout

                                NavigationDrawerItem(
                                    label = {
                                        Text(
                                            text = stringResource(Resources.strings.delete_account),
                                            style = MaterialTheme.typography.labelLarge.copy(
                                                color = MaterialTheme.colorScheme.error
                                            )
                                        )
                                    },
                                    selected = false,
                                    onClick = {
                                        showDeleteAccountDialog = true
                                        scope.launch { drawerState.close() }
                                    },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.PersonRemove,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                )//Delete Account

                                Spacer(Modifier.padding(16.dp))

                                //Details
                                if (appState.email != null) {
                                    Text(
                                        text = stringResource(
                                            Resources.strings.signed_as,
                                            appState.email!!
                                        ),
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                    )
                                }//Logged In As

                                Text(
                                    text = stringResource(
                                        Resources.strings.app_version,
                                        getVersionName()
                                    ),
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                )//App Version
                            } //Bottom Section
                        }
                    }
                }
            ) {
                Scaffold(
                    snackbarHost = {
                        SnackbarHost(
                            hostState = snackbarHostState,
                            snackbar = {
                                CustomSnackbar(
                                    isSuccess = snackbarSuccess,
                                    content = it.visuals.message
                                )
                            }
                        )
                    }
                ) {
                    Nav(
                        modifier = Modifier.fillMaxSize().padding(it),
                        onSplashScreenDone = onSplashScreenDone,
                        onDirectionChanges = { direction -> current = direction },
                        onShowSnackbar = { success, res, args, action ->
                            snackbarSuccess = success
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = stringRes(res, args),
                                    actionLabel = action,
                                    duration = SnackbarDuration.Short,
                                )
                            }
                        },
                        openDrawer = { scope.launch { drawerState.open() } },
                        showInterstitialAd = showInterstitialAd,
                        speak = speak,
                        ttsState = tts,
                        //Login/Logout
                        logoutState = outState,
                        onLogin = { viewModel.refetchEmail() },
                        onLoggedOut = { viewModel.resetOutState() },
                        onLogout = { viewModel.logout() }
                    )
                }
            }
        }
    }
}