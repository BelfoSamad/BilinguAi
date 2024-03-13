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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.samadtch.bilinguai.databinding.FragmentRegisterBinding
import com.samadtch.bilinguai.ui.common.CustomSnackbar
import com.samadtch.bilinguai.ui.screens.RegisterScreen
import com.samadtch.bilinguai.ui.theme.AppTheme
import com.samadtch.bilinguai.utilities.stringResource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    /***********************************************************************************************
     * ************************* Declarations
     */
    private val viewModel: AuthViewModel by viewModels()
    private var _binding: FragmentRegisterBinding? = null
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

        //Collect Register State
        collectLatestLifecycleFlow(viewModel.registerState) {
            if (it?.userId != null) findNavController().navigate(R.id.register)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        binding.composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                //------------------------------- Declarations
                val snackbarHostState = remember { SnackbarHostState() }
                var snackbarSuccess by remember { mutableStateOf(false) }
                val registerState by viewModel.registerState.collectAsStateWithLifecycle()

                //------------------------------- Effects
                AppTheme {
                    Scaffold(snackbarHost = {
                        SnackbarHost(
                            hostState = snackbarHostState,
                            snackbar = {
                                CustomSnackbar(
                                    content = it.visuals.message,
                                    isSuccess = snackbarSuccess
                                )
                            }
                        )
                    }) {
                        Surface(
                            modifier = Modifier.fillMaxSize()
                                .padding(it),
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            RegisterScreen(
                                stringRes = { id, args ->
                                    stringResource(requireContext(), id, args ?: listOf())
                                },
                                onShowSnackbar = { success, message, action ->
                                    snackbarSuccess = success
                                    snackbarHostState.showSnackbar(
                                        message = message,
                                        actionLabel = action,
                                        duration = SnackbarDuration.Short,
                                    ) == SnackbarResult.ActionPerformed
                                },
                                goLogin = { findNavController().navigate(R.id.go_login) },
                                register = viewModel::register,
                                registerState = registerState
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
    fun RegisterScreenPreview() {
        //Done
    }
}