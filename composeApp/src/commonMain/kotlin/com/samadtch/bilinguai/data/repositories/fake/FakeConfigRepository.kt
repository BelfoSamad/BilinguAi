package com.samadtch.bilinguai.data.repositories.fake

import com.samadtch.bilinguai.data.repositories.base.ConfigRepository
import com.samadtch.bilinguai.data.repositories.base.GenerationState

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

    override suspend fun getGenerationState() = Result.success(GenerationState(null, 1))

    override suspend fun handleGenerationState(current: Int) {}
}