package com.edurda77.impuls.tele_tv.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.edurda77.impuls.tele_tv.domain.model.Credintial
import com.edurda77.impuls.tele_tv.domain.repository.DataStoreRepository
import com.edurda77.impuls.tele_tv.domain.utils.LABEL_PASSWORD
import com.edurda77.impuls.tele_tv.domain.utils.LABEL_USERNAME
import com.edurda77.impuls.tele_tv.domain.utils.LAST_CHANNEL
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class DataStoreRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : DataStoreRepository {


    override suspend fun saveCredintial(
        username: String,
        password: String,
    ) {
        dataStore.edit { preferences ->
            preferences[FIELD_LABEL_USERNAME] = username
        }
        dataStore.edit { preferences ->
            preferences[FIELD_LABEL_PASSWORD] = password
        }
    }


    override suspend fun getCredintial(): Credintial? {
        val username = dataStore.data.map { preferences ->
            preferences[FIELD_LABEL_USERNAME]
        }.first()
        val password = dataStore.data.map { preferences ->
            preferences[FIELD_LABEL_PASSWORD]
        }.first()
        return if (username != null && password != null) Credintial(
            username = username,
            password = password
        ) else null

    }

    override suspend fun saveLastChannel(
        uuid: String,
    ) {
        dataStore.edit { preferences ->
            preferences[FIELD_LAST_CHANNEL] = uuid
        }
    }


    override suspend fun getLastChannel(): String? {
        return dataStore.data.map { preferences ->
            preferences[FIELD_LAST_CHANNEL]
        }.first()
    }

    override suspend fun clearCredintial() {
        dataStore.edit { preferences ->
            preferences.remove(FIELD_LABEL_USERNAME)
            preferences.remove(FIELD_LABEL_PASSWORD)
        }
    }

    companion object {
        val FIELD_LABEL_USERNAME = stringPreferencesKey(LABEL_USERNAME)
        val FIELD_LABEL_PASSWORD = stringPreferencesKey(LABEL_PASSWORD)
        val FIELD_LAST_CHANNEL = stringPreferencesKey(LAST_CHANNEL)
    }
}