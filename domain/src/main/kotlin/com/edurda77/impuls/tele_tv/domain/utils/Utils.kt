package com.edurda77.impuls.tele_tv.domain.utils

fun Long.calculateHoursMins():Pair<Long, Long> {
    val durationMin = this/60
    val hours = durationMin/60
    val minutes = durationMin%60
    return Pair(hours, minutes)
}