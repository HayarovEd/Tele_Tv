package com.edurda77.impuls.tele_tv.domain.repository

import com.edurda77.impuls.tele_tv.domain.model.TvChannel
import com.edurda77.impuls.tele_tv.domain.utils.DataError
import com.edurda77.impuls.tele_tv.domain.utils.ResultWork

interface RemoteRepository {
    suspend fun authorization(username: String, password: String): ResultWork<Unit, DataError>
    suspend fun downloadPlaylist(username: String, password: String): ResultWork<List<TvChannel>, DataError>
}