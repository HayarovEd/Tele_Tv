package com.edurda77.impuls.tele_tv.domain.repository

import com.edurda77.impuls.tele_tv.domain.model.TvChannel
import com.edurda77.impuls.tele_tv.domain.utils.DataError
import com.edurda77.impuls.tele_tv.domain.utils.ResultWork
import kotlinx.coroutines.flow.Flow

interface LocalRepository {
    suspend fun insertLocation(
        tvgId: String,
        tvgLogo: String?,
        tvgChno: String,
        name: String,
        url: String
    ): ResultWork<Unit, DataError.LocalDateBase>

    suspend fun getAllLocations(): Flow<ResultWork<List<TvChannel>, DataError.LocalDateBase>>
}