package com.samadtch.bilinguai.screens.boarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.samadtch.bilinguai.R
import com.samadtch.bilinguai.databinding.FragmentBoardingBinding
import com.samadtch.bilinguai.ui.screens.BoardingScreen
import com.samadtch.bilinguai.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import getHypes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BoardingFragment : Fragment() {

    /***********************************************************************************************
     * ************************* Declarations
     */
    private val viewModel: BoardingViewModel by viewModels()
    private var _binding: FragmentBoardingBinding? = null
    private val binding get() = _binding!!

    /***********************************************************************************************
     * ************************* LifeCycle
     */
    private fun <T> collectLatestLifecycleFlow(flow: Flow<T>, collect: suspend (T) -> Unit) {
        lifecycleScope.launch {
            flow.collect(collect)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Initialize
        collectLatestLifecycleFlow(viewModel.uiState) { states ->
            if (states != null && !states.isFirstTime) {
                if (states.isLoggedIn) findNavController().navigate(R.id.start_home)
                else findNavController().navigate(R.id.start_login)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBoardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                //------------------------------- UI
                AppTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.tertiary
                    ) {
                        BoardingScreen(
                            goRegister = {
                                viewModel.setFirstTime()
                                findNavController().navigate(R.id.start_register)
                            },
                            hypes = getHypes()
                        )
                    }
                }
            }
        }
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
    fun BoardingScreenPreview() {
        //Done
    }
}