package com.edurda77.impuls.tele_tv.domain.repository

import com.edurda77.impuls.tele_tv.domain.model.Credintial
import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    suspend fun saveCredintial(username: String, password: String)
    suspend fun getCredintial(): Credintial?
    suspend fun clearCredintial()
    suspend fun saveVolume(volume: Float)
    fun getFlowVolume(): Flow<Float>
}