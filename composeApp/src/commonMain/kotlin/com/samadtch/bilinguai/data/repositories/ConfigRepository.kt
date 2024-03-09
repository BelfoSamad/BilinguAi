package com.samadtch.bilinguai.data.repositories

import com.samadtch.bilinguai.data.datasources.local.AppPrefDataSource
import com.samadtch.bilinguai.data.datasources.local.UserPrefDataSource
import com.samadtch.bilinguai.data.datasources.remote.base.ConfigRemoteSource
import com.samadtch.bilinguai.data.repositories.base.ConfigRepository
import com.samadtch.bilinguai.di.Dispatcher
import kotlinx.coroutines.withContext

class ConfigRepository(
    private val appPrefDataSource: AppPrefDataSource,
    private val userPrefDataSource: UserPrefDataSource,
    private val configDataSource: ConfigRemoteSource,
    private val dispatcher: Dispatcher
) : ConfigRepository {

    override suspend fun isFirstTime() = withContext(dispatcher.io) {
        appPrefDataSource.isFirstStart()
    }

    override suspend fun setFirstTime() = withContext(dispatcher.io) {
        appPrefDataSource.setFirstStart()
    }

    override fun getAppDetails() = mapOf(
        "privacy" to configDataSource.getStringConfig("PRIVACY_POLICY_LINK"),
        "tos" to configDataSource.getStringConfig("TOS_LINK"),
        "developer" to configDataSource.getStringConfig("DEVELOPER_ID")
    )

    override fun getBaseCooldown() = configDataSource.getLongConfig("BASE_COOLDOWN")

    override suspend fun getCooldown() = userPrefDataSource.getCooldownTimestamp()

    override suspend fun setCooldown(timestamp: Long) {
        userPrefDataSource.setCooldownTimestamp(timestamp)
    }

    override suspend fun generationsRemaining() = (userPrefDataSource.getRemaining()
        ?: configDataSource.getLongConfig("GENERATIONS_COUNT").toInt()) > 0

    override suspend fun dropRemaining() {
        val remaining = if (userPrefDataSource.getRemaining() == null)
            configDataSource.getLongConfig("GENERATIONS_COUNT").toInt()
        else userPrefDataSource.getRemaining()!!
        userPrefDataSource.setRemaining(remaining - 1)
    }

}