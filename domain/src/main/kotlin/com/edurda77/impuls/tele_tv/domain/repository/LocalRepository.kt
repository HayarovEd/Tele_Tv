package com.edurda77.impuls.tele_tv.domain.repository

import com.edurda77.impuls.tele_tv.domain.model.TvChannel
import com.edurda77.impuls.tele_tv.domain.utils.DataError
import com.edurda77.impuls.tele_tv.domain.utils.ResultWork
import kotlinx.coroutines.flow.Flow

interface LocalRepository {
    suspend fun insertChannel(
        tvChannel: TvChannel
    ): ResultWork<Unit, DataError.LocalDateBase>

    suspend fun getAllChannels(): Flow<ResultWork<List<TvChannel>, DataError.LocalDateBase>>
}