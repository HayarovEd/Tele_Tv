package com.edurda77.impuls.tele_tv.data.remote


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChannelListResponse(
    @SerialName("entries")
    val entries: List<ChannelResponse>,
    @SerialName("total")
    val total: Int
)