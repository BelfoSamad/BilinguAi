package com.samadtch.bilinguai.di

import com.samadtch.bilinguai.data.repositories.base.ConfigRepository
import com.samadtch.bilinguai.data.repositories.base.DataRepository
import com.samadtch.bilinguai.data.repositories.base.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.koin.java.KoinJavaComponent.get

@Module
@InstallIn(SingletonComponent::class)
object RepositoriesModule {

    @Provides
    fun provideConfigRepository(): ConfigRepository {
        return get(ConfigRepository::class.java)
    }

    @Provides
    fun provideUserRepository(): UserRepository {
        return get(UserRepository::class.java)
    }

    @Provides
    fun provideDataRepository(): DataRepository {
        return get(DataRepository::class.java)
    }

}