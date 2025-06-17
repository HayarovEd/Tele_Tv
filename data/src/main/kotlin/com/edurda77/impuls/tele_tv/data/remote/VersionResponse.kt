package com.edurda77.impuls.tele_tv.data.remote


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VersionResponse(
    @SerialName("createdDate")
    val createdDate: String,
    @SerialName("group")
    val groupResponseDto: GroupResponseDto,
    @SerialName("hasFile")
    val hasFile: Boolean,
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String
)