package com.edurda77.impuls.tele_tv.data.remote


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EntryDto(
    @SerialName("key")
    val key: String,
    @SerialName("val")
    val valX: String
)