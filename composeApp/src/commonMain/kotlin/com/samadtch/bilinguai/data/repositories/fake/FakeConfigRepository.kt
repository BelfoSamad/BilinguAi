package com.samadtch.bilinguai.data.repositories.fake

import com.samadtch.bilinguai.data.repositories.base.ConfigRepository

class FakeConfigRepository : ConfigRepository {

    //Success
    override suspend fun isFirstTime() = false

    //Success
    override suspend fun setFirstTime() {}

    //Success
    override fun getAppDetails(): Map<String, String> = mapOf(
        "privacy" to "https://www.facebook.com",
        "tos" to "https://www.facebook.com",
        "developer" to "Headspace+for+Meditation,+Mindfulness+and+Sleep"
    )

    override fun getBaseCooldown(): Long = 86400

    override suspend fun getCooldown() = 1710011064L

    override suspend fun setCooldown(timestamp: Long) {}

    override suspend fun generationsRemaining() = false

    override suspend fun dropRemaining() {}
}