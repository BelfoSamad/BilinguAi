package com.samadtch.bilinguai.data.datasources.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.samadtch.bilinguai.data.datasources.local.AppPrefDataSource.PreferencesKeys.FIRST_START
import kotlinx.coroutines.flow.first

class AppPrefDataSource(private val dataStore: DataStore<Preferences>) {

    private object PreferencesKeys {
        val FIRST_START = booleanPreferencesKey("first_start")
    }

    suspend fun setFirstStart() {
        dataStore.edit { preferences ->
            preferences[FIRST_START] = false
        }
    }

    suspend fun isFirstStart() = dataStore.data.first()[FIRST_START] ?: true

}