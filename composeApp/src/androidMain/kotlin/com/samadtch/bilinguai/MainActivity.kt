package com.samadtch.bilinguai

import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.SUCCESS
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.samadtch.bilinguai.utilities.stringResource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.Random
import java.util.UUID
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
    @Inject
    lateinit var remoteConfig: FirebaseRemoteConfig

    //Loading
    private val _loaded = MutableStateFlow(true)
    private val loaded = _loaded.asStateFlow()

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

    //GDPR
    private var isMobileAdsInitializeCalled = AtomicBoolean(false)
    private lateinit var consentInformation: ConsentInformation

    //Text To Speech
    private lateinit var tts: TextToSpeech
    private val state = MutableStateFlow<Int?>(null)

    /***********************************************************************************************
     * ************************* LifeCycle
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Splash Screen
        installSplashScreen().apply { this.setKeepOnScreenCondition { loaded.value } }

        //Initializations
        remoteConfig.fetchAndActivate()//Remote Config
        handleGDPR()//GDPR
        tts = TextToSpeech(this) { _ -> }//Init TTS

        //Get Package Info
        try {
            packageInfo = packageManager.getPackageInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            Log.d(TAG, "onCreate: " + e.message)
        }

        //Get Review Manager TODO: Put in Method
        reviewManager = ReviewManagerFactory.create(this)
        val request = reviewManager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                reviewInfo = task.result
            }
        }

        //UI
        setContent {
            App(
                stringRes = { res, args -> stringResource(this, res, args) },
                onSplashScreenDone = { lifecycleScope.launch { _loaded.emit(false) } },
                openWebPage = { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it))) },
                getVersionName = { packageInfo.versionName },
                reviewApp = { reviewManager.launchReviewFlow(this, reviewInfo) },
                showInterstitialAd = {
                    mInterstitialAd?.show(this)
                    loadInterstitialAd()//Reload
                },
                speak = { text, locale, index ->
                    tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                        override fun onDone(utteranceId: String?) {
                            lifecycleScope.launch { state.emit(null) }
                        }
                        override fun onStart(utteranceId: String?) {
                            lifecycleScope.launch { state.emit(index) }
                        }
                        @Deprecated("Deprecated in Java")
                        override fun onError(utteranceId: String?) {}
                    })
                    tts.setLanguage(Locale(when (locale) {
                                "Arabic" -> "ar"
                                "English" -> "en"
                                "French" -> "fr"
                                "Italian" -> "it"
                                else -> "es"
                            }))
                    tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, UUID.randomUUID().toString()) == SUCCESS
                },
                ttsState = state
            )
        }
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

    private fun loadInterstitialAd() {
        InterstitialAd.load(
            this,
            getString(R.string.INTERSTITIAL_AD_ID),
            AdRequest.Builder().build(),
            interstitialAdLoadCallback
        )
    }

}