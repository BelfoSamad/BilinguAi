package com.samadtch.bilinguai.data.repositories.base

interface ConfigRepository {

    suspend fun isFirstTime(): Boolean

    suspend fun setFirstTime()

    fun getAppDetails(): Map<String, String>

    fun getBaseCooldown(): Long

    suspend fun getCooldown(): Long?

    suspend fun setCooldown(timestamp: Long)

    suspend fun generationsRemaining(): Boolean

    suspend fun dropRemaining()

}