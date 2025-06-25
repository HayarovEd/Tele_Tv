package com.edurda77.impuls.tele_tv.data.remote


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EpgDto(
    @SerialName("ageRating")
    val ageRating: Int=-1,
    @SerialName("channelName")
    val channelName: String,
    @SerialName("channelNumber")
    val channelNumber: String,
    @SerialName("channelUuid")
    val channelUuid: String,
    @SerialName("description")
    val description: String = "",
    @SerialName("eventId")
    val eventId: Int,
    @SerialName("nextEventId")
    val nextEventId: Int,
    @SerialName("start")
    val start: Long,
    @SerialName("stop")
    val stop: Long,
    @SerialName("title")
    val title: String
)