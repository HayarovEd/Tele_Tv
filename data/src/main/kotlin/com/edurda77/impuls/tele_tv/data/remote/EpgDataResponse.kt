package com.edurda77.impuls.tele_tv.data.remote


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EpgDataResponse(
    @SerialName("entries")
    val epgDtos: List<EpgDto>,
    @SerialName("totalCount")
    val totalCount: Int
)