package com.samadtch.bilinguai.data.datasources.remote.base

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import org.koin.dsl.module

class ConfigRemoteSourceAndroid(private val config: FirebaseRemoteConfig) : ConfigRemoteSource {

    /***********************************************************************************************
     * ************************* Methods
     */
    override fun getStringConfig(key: String) = config.getString(key)

    override fun getLongConfig(key: String) = config.getLong(key)

    override fun getDoubleConfig(key: String) = config.getDouble(key)

    override fun getBooleanConfig(key: String) = config.getBoolean(key)

}

actual fun getConfigRemoteSource() = module {
    single<ConfigRemoteSource> { ConfigRemoteSourceAndroid(get()) }
}