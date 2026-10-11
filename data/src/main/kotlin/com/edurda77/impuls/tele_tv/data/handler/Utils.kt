package com.edurda77.impuls.tele_tv.data.handler

import android.content.Context
import com.edurda77.impuls.tele_tv.data.remote.EpgDataResponse
import com.edurda77.impuls.tele_tv.domain.model.TvEpg

fun Context.pathToDownloadFile(fileName: String): String =
    "${externalCacheDir?.absolutePath}/$fileName"

fun EpgDataResponse.convertToTvEpg(): List<TvEpg> {
    return epgDtos.map {
        TvEpg(
            ageRating = if (it.ageRating == -1) 0 else it.ageRating,
            channelName = it.channelName,
            channelNumber = it.channelNumber,
            channelUuid = it.channelUuid,
            description = it.description.ifBlank { null },
            eventId = it.eventId,
            stop = it.stop,
            title = it.title,
            start = it.start
        )
    }
}