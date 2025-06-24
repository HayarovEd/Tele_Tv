package com.edurda77.impuls.tele_tv.domain.model

data class TvChannel(
    val tvgId: String,
    val tvgLogo: String?,
    val tvgChannelNumber: Int,
    val name: String,
    val url: String
)
