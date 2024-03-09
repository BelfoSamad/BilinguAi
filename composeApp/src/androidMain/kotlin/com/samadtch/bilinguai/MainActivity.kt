package com.samadtch.bilinguai

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.MobileAds
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.samadtch.bilinguai.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    /***********************************************************************************************
     * ************************* Declarations
     */
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    //Declarations
    @Inject
    lateinit var remoteConfig: FirebaseRemoteConfig
    //Loading
    private val _loading = MutableStateFlow(true)
    private val loading = _loading.asStateFlow()

    //GDPR
    private var isMobileAdsInitializeCalled = AtomicBoolean(false)
    private lateinit var consentInformation: ConsentInformation

    /***********************************************************************************************
     * ************************* LifeCycle
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Splash Screen, TODO: Better Solution Later
        installSplashScreen().apply {
            lifecycleScope.launch { delay(1000); _loading.emit(false) }//Loading Delay
            this.setKeepOnScreenCondition { loading.value }
        }

        //Init Activity
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Fetch RemoteConfig
        remoteConfig.fetchAndActivate().addOnCompleteListener {
            Log.d(
                "App - ",
                "OnCreateActivity: Fetch Remote Configurations (${it.isSuccessful})"
            )
        }

        //Init Ads and GDPR
        handleGDPR()
    }

    private fun handleGDPR() {
        consentInformation = UserMessagingPlatform.getConsentInformation(this)
        consentInformation.requestConsentInfoUpdate(
            this,
            ConsentRequestParameters.Builder().setTagForUnderAgeOfConsent(false).build(),
            { loadForm() },
            { Log.d(TAG, "handleGDPR: Failed to update - " + it.message) }
        )

        //Check in Parallel
        if (consentInformation.canRequestAds()) {
            initializeMobileAdsSdk()
        }
    }

    private fun loadForm() {
        UserMessagingPlatform.loadAndShowConsentFormIfRequired(this@MainActivity) {
            Log.w(TAG, String.format("%s: %s", it?.errorCode, it?.message))
            if (consentInformation.canRequestAds()) {
                initializeMobileAdsSdk()
            }
        }
    }

    private fun initializeMobileAdsSdk() {
        if (isMobileAdsInitializeCalled.getAndSet(true)) return

        // Initialize the Google Mobile Ads SDK.
        MobileAds.initialize(this)
    }

}