package com.samadtch.bilinguai.data.repositories

import com.samadtch.bilinguai.data.datasources.local.AppPrefDataSource
import com.samadtch.bilinguai.data.datasources.remote.base.AuthRemoteSource
import com.samadtch.bilinguai.data.datasources.remote.base.ConfigRemoteSource
import com.samadtch.bilinguai.data.repositories.base.ConfigRepository
import com.samadtch.bilinguai.data.repositories.base.GenerationState
import com.samadtch.bilinguai.di.Dispatcher
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

class ConfigRepository(
    private val appPrefDataSource: AppPrefDataSource,
    private val authRemoteSource: AuthRemoteSource,
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

    override suspend fun getGenerationState(): Result<GenerationState> {
        return configDataSource.getGenerationState(authRemoteSource.getUserId().getOrNull())
    }


    override suspend fun handleGenerationState(current: Int) {
        configDataSource.setGenerationState(
            authRemoteSource.getUserId().getOrNull(),
            Clock.System.now().epochSeconds + configDataSource.getLongConfig("BASE_COOLDOWN"),
            (if (current == 0) configDataSource.getLongConfig("GENERATIONS_COUNT").toInt()
            else current) - 1,
        )
    }

}