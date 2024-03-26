package com.samadtch.bilinguai.data.datasources.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit

class UserPrefDataSource(private val dataStore: DataStore<Preferences>) {
    private object PreferencesKeys {}
    suspend fun clear() {
        dataStore.edit {
            it.clear()
        }
    }
}