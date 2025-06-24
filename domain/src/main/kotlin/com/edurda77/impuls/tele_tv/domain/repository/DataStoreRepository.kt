package com.edurda77.impuls.tele_tv.domain.repository

import com.edurda77.impuls.tele_tv.domain.model.Credintial

interface DataStoreRepository {
    suspend fun saveCredintial(username: String, password: String)
    suspend fun getCredintial(): Credintial?
    suspend fun clearCredintial()
    suspend fun saveLastChannel(uuid: String)
    suspend fun getLastChannel(): String?
}