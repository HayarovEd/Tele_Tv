package com.edurda77.impuls.tele_tv.data.repository

import com.edurda77.impuls.tele_tv.data.handler.handleReadFromDataBase
import com.edurda77.impuls.tele_tv.data.handler.handleWriteToDataBase
import com.edurda77.impuls.tele_tv.data.local.TeleTVDatabase
import com.edurda77.impuls.tele_tv.data.local.TvChannelEntity
import com.edurda77.impuls.tele_tv.domain.model.TvChannel
import com.edurda77.impuls.tele_tv.domain.repository.LocalRepository
import com.edurda77.impuls.tele_tv.domain.utils.DataError
import com.edurda77.impuls.tele_tv.domain.utils.ResultWork
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class LocalRepositoryImpl(
    db: TeleTVDatabase
): LocalRepository {
    private val dao = db.channelDao

    @OptIn(ExperimentalTime::class)
    override suspend fun insertLocation(
        tvChannel: TvChannel
    ): ResultWork<Unit, DataError.LocalDateBase> {
        return handleWriteToDataBase {
            dao.insertTvChannel(
               TvChannelEntity(
                   tvgLogo =tvChannel.tvgLogo,
                   tvgId = tvChannel.tvgId,
                   tvgChno = tvChannel.tvgChannelNumber,
                   name = tvChannel.name,
                   url = tvChannel.url,
                   updateAt = Clock.System.now()
                       .toLocalDateTime(TimeZone.currentSystemDefault())
               )
            )
        }
    }

    override suspend fun getAllChannels(): Flow<ResultWork<List<TvChannel>, DataError.LocalDateBase>> {
        return handleReadFromDataBase {
            dao.getLatest10Items().map { tvChannelEntity ->
                tvChannelEntity.map {
                    TvChannel(
                        tvgId = it.tvgId,
                        tvgLogo = it.tvgLogo,
                        tvgChannelNumber = it.tvgChno,
                        name = it.name,
                        url = it.url
                    )
                }
            }
        }
    }
}