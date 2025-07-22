package com.edurda77.impuls.tele_tv.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.edurda77.impuls.tele_tv.domain.model.Credintial
import com.edurda77.impuls.tele_tv.domain.repository.DataStoreRepository
import com.edurda77.impuls.tele_tv.domain.utils.LABEL_PASSWORD
import com.edurda77.impuls.tele_tv.domain.utils.LABEL_USERNAME
import com.edurda77.impuls.tele_tv.domain.utils.LABEL_VOLUME
import kotlinx.coroutines.flow.Flow
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

    override suspend fun clearCredintial() {
        dataStore.edit { preferences ->
            preferences.remove(FIELD_LABEL_USERNAME)
            preferences.remove(FIELD_LABEL_PASSWORD)
        }
    }

    override suspend fun saveVolume(
        volume: Float
    ) {
        dataStore.edit { preferences ->
            preferences[FIELD_LABEL_VOLUME] = volume
        }
    }


    override fun getFlowVolume(): Flow<Float> {
        return dataStore.data.map { preferences ->
            preferences[FIELD_LABEL_VOLUME] ?: 0.5f
        }
    }

    companion object {
        val FIELD_LABEL_USERNAME = stringPreferencesKey(LABEL_USERNAME)
        val FIELD_LABEL_PASSWORD = stringPreferencesKey(LABEL_PASSWORD)
        val FIELD_LABEL_VOLUME = floatPreferencesKey(LABEL_VOLUME)
    }
}