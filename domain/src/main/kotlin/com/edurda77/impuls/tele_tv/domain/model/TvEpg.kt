package com.edurda77.impuls.tele_tv.domain.model

data class TvEpg(
    val ageRating: Int?,
    val channelName: String,
    val channelNumber: String,
    val channelUuid: String,
    val description: String?,
    val eventId: Int,
    val nextEventId: Int,
    val start: Long,
    val stop: Long,
    val title: String
) {
    val duration = stop - start
}
