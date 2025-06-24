package com.edurda77.impuls.tele_tv.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.edurda77.impuls.tele_tv.domain.utils.TB_CHANNEL
import com.edurda77.impuls.tele_tv.domain.utils.TB_CHANNEL_CHNO
import com.edurda77.impuls.tele_tv.domain.utils.TB_CHANNEL_ID
import com.edurda77.impuls.tele_tv.domain.utils.TB_CHANNEL_LOGO
import com.edurda77.impuls.tele_tv.domain.utils.TB_CHANNEL_NAME
import com.edurda77.impuls.tele_tv.domain.utils.TB_CHANNEL_TIME
import com.edurda77.impuls.tele_tv.domain.utils.TB_CHANNEL_URL
import kotlinx.datetime.LocalDateTime

@Entity(tableName = TB_CHANNEL)
data class TvChannelEntity(
    @PrimaryKey
    @ColumnInfo(name = TB_CHANNEL_ID)
    val tvgId: String,
    @ColumnInfo(name = TB_CHANNEL_LOGO)
    val tvgLogo: String?,
    @ColumnInfo(name = TB_CHANNEL_CHNO)
    val tvgChno: String,
    @ColumnInfo(name = TB_CHANNEL_NAME)
    val name: String,
    @ColumnInfo(name = TB_CHANNEL_URL)
    val url: String,
    @ColumnInfo(name = TB_CHANNEL_TIME)
    val updateAt: LocalDateTime,
)