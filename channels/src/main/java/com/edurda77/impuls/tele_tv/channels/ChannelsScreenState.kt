package com.edurda77.impuls.tele_tv.channels

import com.edurda77.impuls.tele_tv.domain.model.Category
import com.edurda77.impuls.tele_tv.domain.model.Credintial
import com.edurda77.impuls.tele_tv.domain.model.LastVersionApp
import com.edurda77.impuls.tele_tv.domain.model.TvChannel
import com.edurda77.resources.uikit.UiText
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

data class ChannelsScreenState @OptIn(ExperimentalTime::class) constructor(
    val credintial: Credintial? = null,
    val tvChannels: List<TvChannel> = emptyList(),
    val categories: List<Category> = emptyList(),
    val lastTvChannels: List<TvChannel> = emptyList(),
    val groupedTvChannels: Map<Category, List<TvChannel>> = emptyMap(),
    val isLoading: Boolean = false,
    val message: UiText? = null,
    //val focusedChannelId: String? = null,
    val enableUpdate: Boolean = false,
    val isUpdating: Boolean = false,
    val release: LastVersionApp? = null,
    val percentDownload: Int = 0,
    val currentTime: LocalDateTime = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
)