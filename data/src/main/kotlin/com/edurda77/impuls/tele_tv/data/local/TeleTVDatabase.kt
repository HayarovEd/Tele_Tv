package com.edurda77.impuls.tele_tv.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        TvChannelEntity::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class TeleTVDatabase : RoomDatabase() {
    abstract val channelDao: ChannelDao
}