package com.samadtch.bilinguai.data.datasources.local

import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual fun getDataStore(filename: String) = module {
    single(qualifier = named(filename)) {
        createDataStore {
            androidContext().filesDir?.resolve(filename)?.absolutePath
                ?: throw Exception("Couldn't get Android Datastore context.")
        }
    }
}