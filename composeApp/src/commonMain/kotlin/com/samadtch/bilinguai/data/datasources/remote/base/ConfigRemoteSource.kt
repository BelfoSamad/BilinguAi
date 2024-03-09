package com.samadtch.bilinguai.data.datasources.remote.base

import org.koin.core.module.Module

interface ConfigRemoteSource {

    fun getStringConfig(key: String): String

    fun getLongConfig(key: String): Long

    fun getDoubleConfig(key: String): Double

    fun getBooleanConfig(key: String): Boolean

}

expect fun getConfigRemoteSource(): Module