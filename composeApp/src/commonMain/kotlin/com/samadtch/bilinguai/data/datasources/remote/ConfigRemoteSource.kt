package com.samadtch.bilinguai.data.datasources.remote

import com.samadtch.bilinguai.data.repositories.base.GenerationState
import org.koin.core.module.Module

interface ConfigRemoteSource {

    suspend fun getGenerationState(userId: String?): Result<GenerationState>

    suspend fun setGenerationState(userId: String?, cooldown: Long, remaining: Int)

    suspend fun getRemaining(userId: String?) : Int

    fun getStringConfig(key: String): String

    fun getLongConfig(key: String): Long

    fun getDoubleConfig(key: String): Double

    fun getBooleanConfig(key: String): Boolean

}

expect fun getConfigRemoteSource(): Module