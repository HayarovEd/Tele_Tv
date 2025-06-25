package com.edurda77.impuls.tele_tv.domain.repository

import com.edurda77.impuls.tele_tv.domain.model.TvChannel
import com.edurda77.impuls.tele_tv.domain.model.TvEpg
import com.edurda77.impuls.tele_tv.domain.utils.ASC
import com.edurda77.impuls.tele_tv.domain.utils.DataError
import com.edurda77.impuls.tele_tv.domain.utils.ResultWork
import com.edurda77.impuls.tele_tv.domain.utils.START_LIMIT
import com.edurda77.impuls.tele_tv.domain.utils.START_POSITION

interface RemoteRepository {
    suspend fun authorization(username: String, password: String): ResultWork<Unit, DataError>
    suspend fun getTvChannels(username: String, password: String): ResultWork<List<TvChannel>, DataError>
    suspend fun getEpg(
        username: String,
        password: String,
        dir: String = ASC,
        start: Int = START_POSITION,
        limit: Int = START_LIMIT
    ): ResultWork<List<TvEpg>, DataError>

    suspend fun getEpgByChannelId(
        username: String,
        password: String,
        dir: String = ASC,
        start: Int = START_POSITION,
        limit: Int,
        channelId: String
    ): ResultWork<List<TvEpg>, DataError>
}