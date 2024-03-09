package com.samadtch.bilinguai.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.samadtch.bilinguai.R
import org.koin.dsl.module

actual fun getNativeAppModule() = module {
    single <FirebaseFirestore> { FirebaseFirestore.getInstance() }
    single <FirebaseAuth> { FirebaseAuth.getInstance() }
    single <FirebaseRemoteConfig> {
        val remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        remoteConfig
    }
}