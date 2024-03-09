package com.samadtch.bilinguai.di

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.koin.java.KoinJavaComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModules {

    @Provides
    fun provideFirebaseAnalytics(@ApplicationContext context: Context): FirebaseAnalytics {
        return FirebaseAnalytics.getInstance(context)
    }

    @Provides
    fun provideFirebaseRemoteConfig(): FirebaseRemoteConfig {
        return KoinJavaComponent.get(FirebaseRemoteConfig::class.java)
    }

}