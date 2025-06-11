package com.edurda77.impuls.tele_tv.domain.repository

import com.edurda77.impuls.tele_tv.domain.model.Credintial

interface DataStoreRepository {
    suspend fun saveCredintial(username: String, password: String)
    suspend fun getCredintial(): Credintial?
    suspend fun clearCredintial()
    suspend fun saveLastChannel(number: Int)
    suspend fun getLastChannel(): Int?
}