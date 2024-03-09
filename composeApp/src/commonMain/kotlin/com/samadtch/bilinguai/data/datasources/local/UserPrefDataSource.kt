package com.samadtch.bilinguai.data.datasources.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import com.samadtch.bilinguai.data.datasources.local.UserPrefDataSource.PreferencesKeys.COOLDOWN
import com.samadtch.bilinguai.data.datasources.local.UserPrefDataSource.PreferencesKeys.REMAINING
import kotlinx.coroutines.flow.first

class UserPrefDataSource(private val dataStore: DataStore<Preferences>) {
    private object PreferencesKeys {
        val REMAINING = intPreferencesKey("remaining")
        val COOLDOWN = longPreferencesKey("cooldown")
    }

    suspend fun setCooldownTimestamp(cooldown: Long) {
        dataStore.edit { preferences ->
            preferences[COOLDOWN] = cooldown
        }
    }

    suspend fun getCooldownTimestamp() = dataStore.data.first()[COOLDOWN]

    suspend fun setRemaining(remaining: Int) {
        dataStore.edit { preferences ->
            preferences[REMAINING] = remaining
        }
    }

    suspend fun getRemaining() = dataStore.data.first()[REMAINING]

    suspend fun clear() {
        dataStore.edit {
            it.clear()
        }
    }

}