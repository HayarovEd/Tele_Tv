package com.edurda77.impuls.tele_tv.data.remote


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChannelResponse(
    @SerialName("autoname")
    val autoname: Boolean,
    @SerialName("bouquet")
    val bouquet: String,
    @SerialName("dvr_pre_time")
    val dvrPreTime: Int,
    @SerialName("dvr_pst_time")
    val dvrPstTime: Int,
    @SerialName("enabled")
    val enabled: Boolean,
    @SerialName("epg_running")
    val epgRunning: Int,
    @SerialName("epgauto")
    val epgauto: Boolean,
    @SerialName("epggrab")
    val epggrab: List<String>,
    @SerialName("epglimit")
    val epglimit: Int,
    @SerialName("icon")
    val icon: String? = "",
    @SerialName("icon_public_url")
    val iconPublicUrl: String? = "",
    @SerialName("name")
    val name: String,
    @SerialName("number")
    val number: Int,
    @SerialName("remote_timeshift")
    val remoteTimeshift: Boolean,
    @SerialName("services")
    val services: List<String>,
    @SerialName("tags")
    val tags: List<String>,
    @SerialName("uuid")
    val uuid: String
)