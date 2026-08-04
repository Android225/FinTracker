package com.example.fintracker.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.fintracker.di.qualifier.AuthPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

data class StoredPin(
    val salt: String,
    val hash: String
)

class AuthLocalDataSource @Inject constructor(
    @param:AuthPreferences private val dataStore: DataStore<Preferences>
) {
    val pinConfigured: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PinSaltKey] != null && preferences[PinHashKey] != null
    }

    suspend fun readPin(): StoredPin? {
        val preferences = dataStore.data.first()
        val salt = preferences[PinSaltKey] ?: return null
        val hash = preferences[PinHashKey] ?: return null
        return StoredPin(salt = salt, hash = hash)
    }

    suspend fun savePin(pin: StoredPin) {
        dataStore.edit { preferences ->
            preferences[PinSaltKey] = pin.salt
            preferences[PinHashKey] = pin.hash
        }
    }

    private companion object {
        val PinSaltKey = stringPreferencesKey("pin_salt")
        val PinHashKey = stringPreferencesKey("pin_hash")
    }
}
