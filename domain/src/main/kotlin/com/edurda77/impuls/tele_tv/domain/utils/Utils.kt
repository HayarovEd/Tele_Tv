package com.edurda77.impuls.tele_tv.domain.utils

import com.edurda77.impuls.tele_tv.domain.model.Category
import com.edurda77.impuls.tele_tv.domain.model.TvChannel
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant


@OptIn(ExperimentalTime::class)
fun Long.calculateHoursMins(): String {
    val instant = Instant.fromEpochSeconds(this)
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    return "${localDateTime.hour.toString().padStart(2, '0')}:${
        localDateTime.minute.toString().padStart(2, '0')
    }"
}

@OptIn(ExperimentalTime::class)
fun Long.convertToDate(): LocalDate {
    val instant = Instant.fromEpochSeconds(this)
    return instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
}

fun createCategoryChannelMap(
    categories: List<Category>,
    channels: List<TvChannel>
): Map<Category, List<TvChannel>> {
    val categoryMap = categories.associateBy { it.key }

    val flattenedMap = mutableMapOf<Category, MutableList<TvChannel>>()

    channels.forEach { channel ->
        channel.categoryIds.forEach { categoryId ->
            categoryMap[categoryId]?.let { category ->
                val list = flattenedMap.getOrPut(category) { mutableListOf() }
                list.add(channel)
            }
        }
    }


    return flattenedMap
}