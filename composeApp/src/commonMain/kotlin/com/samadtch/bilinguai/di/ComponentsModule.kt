package com.samadtch.bilinguai.di

import com.samadtch.bilinguai.data.datasources.local.UserPrefDataSource
import com.samadtch.bilinguai.data.datasources.local.appDSFileName
import com.samadtch.bilinguai.data.datasources.local.getDataStore
import com.samadtch.bilinguai.data.datasources.local.userDSFileName
import com.samadtch.bilinguai.data.datasources.remote.base.ModelRemoteSource
import com.samadtch.bilinguai.data.datasources.remote.base.getAuthRemoteSource
import com.samadtch.bilinguai.data.datasources.remote.base.getConfigRemoteSource
import com.samadtch.bilinguai.data.datasources.remote.base.getDataRemoteSource
import com.samadtch.bilinguai.data.repositories.base.ConfigRepository
import com.samadtch.bilinguai.data.repositories.base.DataRepository
import com.samadtch.bilinguai.data.repositories.base.UserRepository
import com.samadtch.bilinguai.ui.screens.AppViewModel
import com.samadtch.bilinguai.ui.screens.auth.AuthViewModel
import com.samadtch.bilinguai.ui.screens.boarding.BoardingViewModel
import com.samadtch.bilinguai.ui.screens.home.HomeViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module
import com.samadtch.bilinguai.data.datasources.remote.ModelRemoteSource as ModelRemoteSourceImpl
import com.samadtch.bilinguai.data.repositories.ConfigRepository as ConfigRepositoryImpl
import com.samadtch.bilinguai.data.repositories.DataRepository as DataRepositoryImpl
import com.samadtch.bilinguai.data.repositories.UserRepository as UserRepositoryImpl

val appModule = module {
    // HTTP Client
    single {
        HttpClient(CIO) {
            expectSuccess = true//Throw error if failed

            //Content Negotiation
            val json = Json { ignoreUnknownKeys = true }
            install(ContentNegotiation) {
                json(json, contentType = ContentType.Application.Json)
            }
        }
    }

    //Preferences
    includes(
        getDataStore(userDSFileName),
        getDataStore(appDSFileName)
    )
}

val dataModule = module {
    //------------------------------------------- Data Sources
    // Remote
    includes(
        getAuthRemoteSource(),
        getConfigRemoteSource(),
        getDataRemoteSource(),
    )
    single<ModelRemoteSource> { ModelRemoteSourceImpl(get()) }

    // Preferences
    single<com.samadtch.bilinguai.data.datasources.local.AppPrefDataSource> {
        com.samadtch.bilinguai.data.datasources.local.AppPrefDataSource(
            get(qualifier = named(appDSFileName))
        )
    }
    single<UserPrefDataSource> { UserPrefDataSource(get(qualifier = named(userDSFileName))) }

    //------------------------------------------- Repositories
    single<ConfigRepository> {
        ConfigRepositoryImpl(get(), get(), get(), get(), provideDispatcher())
        //FakeConfigRepository()
    }
    single<DataRepository> {
        DataRepositoryImpl(get(), get(), get(), get(), provideDispatcher())
        //FakeDataRepository()
    }
    single<UserRepository> {
        UserRepositoryImpl(get(), provideDispatcher())
        //FakeUserRepository()
    }
}

val viewmodelModule = module {
    factory { AppViewModel(get(), get()) }
    factory { BoardingViewModel(get(), get()) }
    factory { HomeViewModel(get(), get(), get()) }
    factory { AuthViewModel(get()) }
}