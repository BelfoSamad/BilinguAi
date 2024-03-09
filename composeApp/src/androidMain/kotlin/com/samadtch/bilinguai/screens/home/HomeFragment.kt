package com.samadtch.bilinguai.screens.home

import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.IntegrationInstructions
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
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
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.samadtch.bilinguai.R
import com.samadtch.bilinguai.Resources.strings
import com.samadtch.bilinguai.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import getInputs
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.samadtch.bilinguai.ui.common.CustomSnackbar
import com.samadtch.bilinguai.ui.common.DeleteAccountDialog
import com.samadtch.bilinguai.ui.screens.HomeScreen
import com.samadtch.bilinguai.ui.theme.AppTheme
import com.samadtch.bilinguai.utilities.exceptions.AuthException.Companion.AUTH_ERROR_USER_NOT_FOUND
import com.samadtch.bilinguai.utilities.exceptions.DataException.Companion.DATA_ERROR_SERVICE
import com.samadtch.bilinguai.utilities.stringResource

@AndroidEntryPoint
class HomeFragment : Fragment() {
    companion object {
        private const val TAG = "HomeFragment"
    }

    /***********************************************************************************************
     * ************************* Declarations
     */
    private val viewModel: HomeViewModel by viewModels()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    //Ads
    private var mInterstitialAd: InterstitialAd? = null
    private val interstitialAdLoadCallback: InterstitialAdLoadCallback =
        object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d(TAG, adError.toString())
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d(TAG, "Ad was loaded.")
                mInterstitialAd = interstitialAd
            }
        }

    //Package Info
    private lateinit var packageInfo: PackageInfo

    //Review
    private lateinit var reviewManager: ReviewManager
    private lateinit var reviewInfo: ReviewInfo

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

        //Initialize ViewModel
        viewModel.initialize()

        //Get Review Manager
        reviewManager = ReviewManagerFactory.create(requireContext())

        //Load Interstitial Ad
        loadInterstitialAd()

        //Get Package Info
        try {
            packageInfo = requireContext().packageManager
                .getPackageInfo(requireContext().packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            Log.d(TAG, "onCreate: " + e.message)
        }

        //Logout
        collectLatestLifecycleFlow(viewModel.outState) {
            findNavController().navigate(R.id.logout)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                //------------------------------- Declarations
                val scope = rememberCoroutineScope()
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val snackbarHostState = remember { SnackbarHostState() }
                var showDeleteAccountDialog by rememberSaveable { mutableStateOf(false) }
                //UI States
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val generationState by viewModel.generationState.collectAsStateWithLifecycle()
                val deleteState by viewModel.deletedState.collectAsStateWithLifecycle()
                val deleteAccountState by viewModel.deleteAccountState.collectAsStateWithLifecycle()

                //------------------------------- Effects
                LaunchedEffect(deleteAccountState) {
                    if (deleteAccountState != null) {
                        when (deleteAccountState) {
                            DATA_ERROR_SERVICE -> {
                                showDeleteAccountDialog = false
                                snackbarHostState.showSnackbar(
                                    message = stringResource(
                                        requireContext(), strings.error_server, null
                                    ),
                                    actionLabel = null,
                                    duration = SnackbarDuration.Short,
                                )
                            }

                            AUTH_ERROR_USER_NOT_FOUND -> {
                                showDeleteAccountDialog = false
                                snackbarHostState.showSnackbar(
                                    message = stringResource(
                                        requireContext(), strings.error_account_no_exists, null
                                    ),
                                    actionLabel = null,
                                    duration = SnackbarDuration.Short,
                                )
                                viewModel.logout()
                            }
                        }
                    }
                }

                //------------------------------- UI
                AppTheme {
                    //------------------------------- Dialogs
                    if (showDeleteAccountDialog) DeleteAccountDialog(
                        stringRes = { id, args ->
                            stringResource(requireContext(), id, args ?: listOf())
                        },
                        deleteAccount = viewModel::deleteAccount,
                        deleteAccountState = deleteAccountState,
                        onDismiss = {
                            if (deleteAccountState != -99) showDeleteAccountDialog = false
                        }
                    )

                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        modifier = Modifier.background(color = MaterialTheme.colorScheme.primary),
                        gesturesEnabled = true,
                        drawerContent = {
                            val links = viewModel.getLinks()
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
                                            text = stringResource(requireContext(), strings.appName, null),
                                            style = MaterialTheme.typography.headlineLarge.copy(
                                                color = MaterialTheme.colorScheme.tertiary,
                                                fontSize = 32.sp
                                            )
                                        )//Logo

                                        NavigationDrawerItem(
                                            label = {
                                                Text(
                                                    text = stringResource(
                                                        requireContext(),
                                                        strings.privacy_policy,
                                                        listOf()
                                                    ),
                                                    style = MaterialTheme.typography.labelLarge.copy(
                                                        color = MaterialTheme.colorScheme.tertiary
                                                    )
                                                )
                                            },
                                            selected = false,
                                            onClick = {
                                                startActivity(
                                                    Intent(
                                                        Intent.ACTION_VIEW,
                                                        Uri.parse(links["privacy"])
                                                    )
                                                )
                                            },
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
                                                    text = stringResource(
                                                        requireContext(),
                                                        strings.tos,
                                                        listOf()
                                                    ),
                                                    style = MaterialTheme.typography.labelLarge.copy(
                                                        color = MaterialTheme.colorScheme.tertiary
                                                    )
                                                )
                                            },
                                            selected = false,
                                            onClick = {
                                                startActivity(
                                                    Intent(
                                                        Intent.ACTION_VIEW, Uri.parse(links["tos"])
                                                    )
                                                )
                                            },
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
                                                    text = stringResource(
                                                        requireContext(),
                                                        strings.check_developer,
                                                        listOf()
                                                    ),
                                                    style = MaterialTheme.typography.labelLarge.copy(
                                                        color = MaterialTheme.colorScheme.tertiary
                                                    )
                                                )
                                            },
                                            selected = false,
                                            onClick = {
                                                startActivity(
                                                    Intent(
                                                        Intent.ACTION_VIEW,
                                                        Uri.parse("https://play.google.com/store/apps/developer?id=${links["developer"]}")
                                                    )
                                                )
                                            },
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
                                                    text = stringResource(
                                                        requireContext(),
                                                        strings.review_app,
                                                        listOf()
                                                    ),
                                                    style = MaterialTheme.typography.labelLarge.copy(
                                                        color = MaterialTheme.colorScheme.tertiary
                                                    )
                                                )
                                            },
                                            selected = false,
                                            onClick = {
                                                reviewManager.launchReviewFlow(
                                                    requireActivity(), reviewInfo
                                                )
                                            },
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
                                                    text = stringResource(
                                                        requireContext(),
                                                        strings.logout,
                                                        listOf()
                                                    ),
                                                    style = MaterialTheme.typography.labelLarge.copy(
                                                        color = MaterialTheme.colorScheme.error
                                                    )
                                                )
                                            },
                                            selected = false,
                                            onClick = { viewModel.logout() },
                                            icon = {
                                                Icon(
                                                    imageVector = Icons.Default.Logout,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.error
                                                )
                                            }
                                        )//Logout

                                        NavigationDrawerItem(
                                            label = {
                                                Text(
                                                    text = stringResource(
                                                        requireContext(),
                                                        strings.delete_account,
                                                        listOf()
                                                    ),
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
                                        if (uiState.email != null) {
                                            Text(
                                                text = stringResource(
                                                    requireContext(),
                                                    strings.signed_as,
                                                    listOf(uiState.email!!)
                                                ),
                                                style = MaterialTheme.typography.titleSmall.copy(
                                                    color = MaterialTheme.colorScheme.secondary
                                                )
                                            )
                                        }//Logged In As

                                        Text(
                                            text = stringResource(
                                                requireContext(),
                                                strings.app_version,
                                                listOf(packageInfo.versionName)
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
                                    snackbar = { CustomSnackbar(content = it.visuals.message) }
                                )
                            }
                        ) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(it),
                                color = MaterialTheme.colorScheme.primary
                            ) {
                                HomeScreen(
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
                                    openDrawer = { scope.launch { drawerState.open() } },
                                    inputs = getInputs(),
                                    //Drawer Menu
                                    logout = viewModel::logout,
                                    //States
                                    uiState = uiState,
                                    generationState = generationState,
                                    deleteState = deleteState,
                                    //Listeners
                                    onSortChanged = viewModel::sortData,
                                    onGenerateClicked = viewModel::generateData,
                                    onDataDeleted = viewModel::deleteData,
                                )
                            }
                        }
                    }
                }
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //init ReviewManager
        val request = reviewManager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                reviewInfo = task.result
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /***********************************************************************************************
     * ************************* Methods
     */
    private fun loadInterstitialAd() {
        InterstitialAd.load(
            requireContext(),
            getString(R.string.INTERSTITIAL_AD_ID),
            AdRequest.Builder().build(),
            interstitialAdLoadCallback
        )
    }

    /***********************************************************************************************
     * ************************* Preview
     */
    @PreviewLightDark
    @Composable
    fun HomeScreenPreview() {
        //Done
    }
}