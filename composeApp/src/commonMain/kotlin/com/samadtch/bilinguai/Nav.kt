package com.samadtch.bilinguai

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.samadtch.bilinguai.ui.screens.auth.AuthViewModel
import com.samadtch.bilinguai.ui.screens.auth.LoginScreen
import com.samadtch.bilinguai.ui.screens.auth.RegisterScreen
import com.samadtch.bilinguai.ui.screens.boarding.BoardingScreen
import com.samadtch.bilinguai.ui.screens.boarding.BoardingViewModel
import com.samadtch.bilinguai.ui.screens.home.HomeScreen
import com.samadtch.bilinguai.ui.screens.home.HomeViewModel
import dev.icerock.moko.resources.StringResource
import getHypes
import getInputs
import kotlinx.coroutines.flow.StateFlow
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.NavOptions
import moe.tlaster.precompose.navigation.PopUpTo
import moe.tlaster.precompose.navigation.rememberNavigator
import moe.tlaster.precompose.navigation.transition.NavTransition

@Composable
fun Nav(
    modifier : Modifier,
    onSplashScreenDone: () -> Unit,
    onDirectionChanges: (String?) -> Unit,
    onShowSnackbar: (Boolean, StringResource, List<Any>?, String?) -> Unit,
    openDrawer: () -> Unit,
    showInterstitialAd: () -> Unit,
    speak: (String, String, Int) -> Boolean,
    ttsState: Int?,
    //Login/Logout
    logoutState: Unit?,
    onLogin: () -> Unit,
    onLogout: () -> Unit,//From Home (Auth Error)
    onLoggedOut: () -> Unit
) {
    //------------------------------- Declarations
    val navigator = rememberNavigator()
    val current by navigator.currentEntry.collectAsState(null)

    //------------------------------- Effects
    LaunchedEffect(current) { onDirectionChanges(current?.path) }

    LaunchedEffect(logoutState) {
        if (logoutState != null){
            navigator.navigate(
                "/login",
                NavOptions(popUpTo = PopUpTo("/home", true))
            )
            onLoggedOut()
        }
    }

    //------------------------------- UI
    NavHost(
        modifier = modifier,
        navigator = navigator,
        navTransition = NavTransition(),
        initialRoute = "/boarding",
    ) {

        //Boarding
        scene(
            route = "/boarding",
            navTransition = NavTransition(),
        ) {
            val viewmodel = koinViewModel(BoardingViewModel::class)
            BoardingScreen(
                viewmodel,
                splashScreenDone = onSplashScreenDone,
                goHome = {
                    navigator.navigate(
                        "/home",
                        NavOptions(popUpTo = PopUpTo("/boarding", true))
                    )
                },
                goRegister = {
                    viewmodel.setFirstTime()
                    navigator.navigate(
                        "/register",
                        NavOptions(popUpTo = PopUpTo("/boarding", true))
                    )
                },
                hypes = getHypes()
            )
        }

        //Register
        scene(
            route = "/register",
            navTransition = NavTransition(),
        ) {
            val viewmodel = koinViewModel(AuthViewModel::class)
            RegisterScreen(
                viewmodel,
                onShowSnackbar = onShowSnackbar,
                goLogin = {
                    navigator.navigate(
                        "/login",
                        NavOptions(popUpTo = PopUpTo("/register", true))
                    )
                },
                goHome = {
                    onLogin()
                    navigator.navigate(
                        "/home",
                        NavOptions(popUpTo = PopUpTo("/register", true))
                    )
                }
            )
        }

        //Boarding
        scene(
            route = "/login",
            navTransition = NavTransition(),
        ) {
            val viewmodel = koinViewModel(AuthViewModel::class)
            LoginScreen(
                viewmodel,
                onShowSnackbar = onShowSnackbar,
                goRegister = {
                    navigator.navigate(
                        "/register",
                        NavOptions(popUpTo = PopUpTo("/login", true))
                    )
                },
                goHome = {
                    onLogin()
                    navigator.navigate(
                        "/home",
                        NavOptions(popUpTo = PopUpTo("/login", true))
                    )
                }
            )
        }

        //Home
        scene(
            route = "/home",
            navTransition = NavTransition(),
        ) {
            val viewmodel = koinViewModel(HomeViewModel::class)
            viewmodel.initialize()
            HomeScreen(
                viewmodel,
                onShowSnackbar = onShowSnackbar,
                inputs = getInputs(),
                openDrawer = openDrawer,
                showInterstitialAd = showInterstitialAd,
                speak = speak,
                ttsState = ttsState,
                logout = onLogout
            )
        }
    }
}