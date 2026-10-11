package com.edurda77.impuls.tele_tv.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.edurda77.impuls.tele_tv.domain.utils.TB_CHANNEL
import com.edurda77.impuls.tele_tv.domain.utils.TB_CHANNEL_TIME
import kotlinx.coroutines.flow.Flow

@Dao
interface ChannelDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTvChannel(tvChannelEntity: TvChannelEntity)

    @Query("SELECT * FROM $TB_CHANNEL ORDER BY $TB_CHANNEL_TIME DESC LIMIT 10")
    fun getLatest10Items(): Flow<List<TvChannelEntity>>
}