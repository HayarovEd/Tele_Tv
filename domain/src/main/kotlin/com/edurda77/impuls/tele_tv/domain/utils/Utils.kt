package com.edurda77.impuls.tele_tv.domain.utils

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime


fun Long.calculateHoursMins(): String {
    val instant = Instant.fromEpochSeconds(this)
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    return "${localDateTime.hour.toString().padStart(2, '0')}:${
        localDateTime.minute.toString().padStart(2, '0')
    }"
}