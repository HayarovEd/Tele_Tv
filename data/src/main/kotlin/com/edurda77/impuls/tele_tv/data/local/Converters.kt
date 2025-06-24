package com.edurda77.impuls.tele_tv.data.local

import androidx.room.TypeConverter
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDateTime? {
        return value?.let {
            val instant: Instant = Instant.fromEpochMilliseconds(it)
            instant.toLocalDateTime(TimeZone.currentSystemDefault())
        }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): Long? {
        return date.let {
            val zone = TimeZone.currentSystemDefault()
            val instant = it?.toInstant(zone)
            instant?.toEpochMilliseconds()
        }
    }
}