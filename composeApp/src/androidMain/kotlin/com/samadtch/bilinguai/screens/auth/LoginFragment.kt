package com.samadtch.bilinguai.screens.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.samadtch.bilinguai.R
import com.samadtch.bilinguai.databinding.FragmentLoginBinding
import com.samadtch.bilinguai.ui.common.CustomSnackbar
import com.samadtch.bilinguai.ui.screens.LoginScreen
import com.samadtch.bilinguai.ui.screens.LoginUiState
import com.samadtch.bilinguai.ui.theme.AppTheme
import com.samadtch.bilinguai.utilities.stringResource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    /***********************************************************************************************
     * ************************* Declarations
     */
    private val viewModel: AuthViewModel by viewModels()
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    /***********************************************************************************************
     * ************************* LifeCycle
     */
    private fun <T> collectLatestLifecycleFlow(flow: Flow<T>, collect: suspend (T) -> Unit) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                flow.collectLatest(collect)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Collect Login State
        collectLatestLifecycleFlow(viewModel.loginState) {
            if (it?.userId != null) findNavController().navigate(R.id.login)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                //------------------------------- Declarations
                val snackbarHostState = remember { SnackbarHostState() }
                val loginState by viewModel.loginState.collectAsStateWithLifecycle()
                val passwordResetState by viewModel.passwordResetState.collectAsStateWithLifecycle()

                //------------------------------- UI
                AppTheme {
                    Scaffold(snackbarHost = {
                        SnackbarHost(
                            hostState = snackbarHostState,
                            snackbar = { CustomSnackbar(content = it.visuals.message, isSuccess = true) }
                        )
                    }) {
                        Surface(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(it),
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            LoginScreen(
                                stringRes = { id, args ->
                                    stringResource(requireContext(), id, args ?: listOf())
                                },
                                onShowSnackbar = { message, action ->
                                    snackbarHostState.showSnackbar(
                                        message = message,
                                        actionLabel = action,
                                        duration = SnackbarDuration.Short,
                                    ) == SnackbarResult.ActionPerformed
                                },
                                goRegister = { findNavController().navigate(R.id.go_register) },
                                login = viewModel::login,
                                loginState = loginState,
                                resetPassword = viewModel::sendPasswordResetEmail,
                                passwordResetState = passwordResetState
                            )
                        }
                    }
                }
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /***********************************************************************************************
     * ************************* Preview
     */
    @PreviewLightDark
    @Composable
    fun LoginScreenPreview() {
        val snackbarHostState = remember { SnackbarHostState() }
        AppTheme {
            Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it),
                    color = MaterialTheme.colorScheme.primary
                ) {
                    LoginScreen(
                        stringRes = { id, args ->
                            stringResource(requireContext(), id, args ?: listOf())
                        },
                        onShowSnackbar = { message, action ->
                            snackbarHostState.showSnackbar(
                                message = message,
                                actionLabel = action,
                                duration = SnackbarDuration.Short,
                            ) == SnackbarResult.ActionPerformed
                        },
                        goRegister = { findNavController().navigate(R.id.go_login) },
                        login = { email, password -> println("$email $password") },
                        loginState = LoginUiState(),
                        resetPassword = {},
                        passwordResetState = -1
                    )
                }
            }
        }
    }
}