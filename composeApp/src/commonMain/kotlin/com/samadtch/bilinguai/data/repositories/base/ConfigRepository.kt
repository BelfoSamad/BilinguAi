package com.samadtch.bilinguai.data.repositories.base

data class GenerationState(
    val cooldown: Long? = null,
    val remaining: Int = 3
)

interface ConfigRepository {

    suspend fun isFirstTime(): Boolean

    suspend fun setFirstTime()

    fun getAppDetails(): Map<String, String>

    suspend fun getGenerationState(): Result<GenerationState>

    suspend fun handleGenerationState(current: Int)

}